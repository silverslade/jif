package it.schillaci.jif.inform;

/*
 * InformDocument.java
 *
 * This file is part of JIF.
 *
 * Jif is substantially an editor entirely written in java that allows the
 * file management for the creation of text-adventures based on Graham
 * Nelson's Inform standard [a programming language for Interactive Fiction].
 * With Jif, it's possible to edit, compile and run a Text Adventure in
 * Inform format.
 *
 * Copyright (C) 2004-2013  Alessandro Schillaci
 *
 * WeB   : http://www.slade.altervista.org/
 * e-m@il: silver.slade@tiscalinet.it
 *
 * Jif is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jif; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

import it.schillaci.jif.core.JifDocument;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.GapContent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleContext;

/**
 * An extension of Jif Document for Inform Syntax Highlighting
 * 
 * @author Alessandro Schillaci
 * @author Peter Piggott
 * @version 2.0
 * @since Jif 3.2
 */
public class InformDocument extends JifDocument implements InformParser.Callback {

    private static final long serialVersionUID = 5856047697369563208L;
    
    /**
     * Constructs an Inform document with a shared set of styles for syntax
     * highlighting.
     * 
     * @param c
     *            the container for the content
     * @param styles
     *            the set of styles for Inform syntax highlighting which may be
     *            shared across documents
     */
    protected  InformDocument(Content c, StyleContext styles) {
        super(c, styles);
    }

    /**
     * Constructs an Inform document with a shared set of styles for syntax
     * highlighting.
     * 
     * @param c
     *            the container for the content
     * @param styles
     *            the set of styles for Inform syntax highlighting which may be
     *            shared across documents
     */
    public InformDocument(Content c, InformContext styles) {
        this(c, (StyleContext) styles);
    }

    /**
     * Constructs an Inform document with the default content storage
     * implementation and a shared set of styles for syntax highlighting.
     * 
     * @param styles
     *            the set of styles for Inform syntax highlighting
     */
    protected InformDocument(StyleContext styles) {
        this(new GapContent(BUFFER_SIZE_DEFAULT), styles);
    }

    /**
     * Constructs an Inform document with the default content storage
     * implementation and a shared set of styles for syntax highlighting.
     * 
     * @param styles
     *            The set of styles for Inform syntax highlighting
     */
    public InformDocument(InformContext styles) {
        this((StyleContext) styles);
    }

    /**
     * Constructs an Inform document with the default content storage
     * implementation and a default set of styles for syntax highlighting.
     * 
     */
    public InformDocument() {
        this(new GapContent(BUFFER_SIZE_DEFAULT), (StyleContext) new InformContext());
    }

    /**
     * Insert a string with syntax highlighting
     * @param offset
     *            The offset to insert the string
     * @param str 
     *            The String to be inserted into the document
     * @param a 
     *            The AttributeSet for the String
     * @throws BadLocationException 
     *            If the insert action fails
     */
    @Override
    public void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException {

        super.insertString(offset, str, getStyle(InformSyntax.Normal.getName()));
        processChangedLines(offset, str.length());

    }

    /**
     * Remove a String from a document
     * 
     * @param offset
     *            The initial offset
     * @param length
     *            Length of the String to be removed
     * @throws BadLocationException
     *            If the remove action fails
     */
    @Override
    public void remove(int offset, int length) throws BadLocationException {

        super.remove(offset, length);
        processChangedLines(offset, length);

    }

    /**
     * Determine the area of the document whose syntax highlighting is impacted
     * by the change of source content
     *
     * @param offset
     *            The initial offset of the change
     * @param length
     *            The length of the change
     * @throws BadLocationException
     *            If the change processing fails
     */
    public void processChangedLines(int offset, int length)
            throws BadLocationException {

        Element line;
        int startOffset = offset;
        int changeOffset = offset;
        int changeLength = length;
        MutableAttributeSet highlight = getStyle(InformSyntax.Normal.getName());
        MutableAttributeSet tokenCheck = getStyle(InformSyntax.Normal.getName());
        
        // Locate start of first highlight token at insert/remove offset 
        
        if (changeOffset>0) {
            
            // Check for the element before the insertion/removal point (offset-1) so if the
            // insertion/removal point is at the boundary of two elements we get the previous
            // highlight token not the following highlight token 
            
            Element token = getCharacterElement(offset-1);
            startOffset = token.getStartOffset();
            highlight = (MutableAttributeSet) token.getAttributes();
            
            tokenCheck = getStyle((String)highlight.getAttribute(AttributeSet.NameAttribute));
            
            while (highlight.containsAttributes(tokenCheck) && changeOffset>0) {
                changeOffset = startOffset;
                token = getCharacterElement(changeOffset-1);
                startOffset = token.getStartOffset();
                highlight = (MutableAttributeSet) token.getAttributes();
            }
        }
        
        // Find the length of the text in the document impacted by the insert/remove. 
        // The length of the text between the start of the first highlight token at the insert point
        // to the end of the current line plus the length of the text being inserted into the document.
        
        if (length>0) {
            line = getParagraphElement(offset+length);
        } else {
            line = getParagraphElement(offset);
        }
        changeLength = line.getEndOffset()-changeOffset; 

        applyHighlighting(changeOffset, changeLength);

    }
    
    /**
     * Apply inform syntax highlighting to the specified portion of the document
     * 
     * 
     * @param offset 
     *            The initial offset at which to apply syntax highlighting
     * @param length 
     *            The changeLength of the document text to be highlighted
     * @throws BadLocationException
     *            If applying highlighting fails
     */
    public void applyHighlighting(int offset, int length) throws BadLocationException {

        int startOffset;
        int endOffset;
        int changeLength;
        MutableAttributeSet syntax;

        String source = getText(offset, length);
        InformLexer lexer = new InformLexer(source, offset);
        InformToken token = lexer.nextElement();

        while (token.getType() != InformToken.EOS) {

            startOffset = token.getStartPosition();
            endOffset = token.getEndPosition();
            changeLength = endOffset - startOffset;

            if (token.getType() == InformToken.COMMENT) {
                syntax = getStyle(InformSyntax.Comment.getName());
            } else if (token.getType() == InformToken.NUMBER) {
                syntax = getStyle(InformSyntax.Number.getName());
            } else if (token.getType() == InformToken.STRING) {
                syntax = getStyle(InformSyntax.String.getName());
            } else if (token.getType() == InformToken.SYMBOL) {
                if (isAttribute(token.getContent())) {
                    syntax = getStyle(InformSyntax.Attribute.getName());
                } else if (isProperty(token.getContent())) {
                    syntax = getStyle(InformSyntax.Property.getName());
                } else if (isVerb(token.getContent())) {
                    syntax = getStyle(InformSyntax.Verb.getName());
                } else if (isKeyword(token.getContent())) {
                    syntax = getStyle(InformSyntax.Keyword.getName());
                } else {
                    syntax = getStyle(InformSyntax.Normal.getName());
                }
            } else if (token.getType() == InformToken.WHITESPACE) {
                syntax = getStyle(InformSyntax.White.getName());
            } else if (token.getType() == InformToken.WORD) {
                syntax = getStyle(InformSyntax.Word.getName());
            } else {
                syntax = getStyle(InformSyntax.Normal.getName());
            }

            setCharacterAttributes(startOffset, changeLength, syntax, true);
            token = lexer.nextElement();
        }
    }
    
    @Override
    public void handleToken(InformToken token) {
        setCharacterAttributes(
                token.getStartPosition(),
                token.getEndPosition() - token.getStartPosition(),
                getStyle(token.getType().getName()),
                true);
    }

    @Override
    public boolean isAttribute(String name){
        return InformLibrary.isAttribute(name);
    }
    
    public boolean isDirective(String name) {
        return InformLibrary.isDirective(name);
    }

    public boolean isKeyword(String name) {
        return InformLibrary.isKeyword(name) || InformLibrary.isDirective(name);
    }

    @Override
    public boolean isProperty(String name){
        return InformLibrary.isProperty(name);
    }

    public boolean isStatement(String name) {
        return InformLibrary.isStatement(name);
    }

    @Override
    public boolean isVerb(String name) {
        return InformLibrary.isVerb(name);
    }
    
    // TODO Fix automatic bracket and quote completion 
    protected String addMatchingBrace(String brace, int offset) throws BadLocationException {
        StringBuilder whiteSpace = new StringBuilder();
        int line = getDefaultRootElement().getElementIndex(offset);
        int i = getDefaultRootElement().getElement(line).getStartOffset();
        while (true) {
            String temp = getText(i, 1);
            if (temp.equals(" ") || temp.equals("\t")) {
                whiteSpace.append(temp);
                i++;
            } else {
                break;
            }
        }
        return (brace.equals("{") ? "{" : "[") + "\n" + whiteSpace.toString() + whiteSpace.toString() + "\n"
                + whiteSpace.toString() + (brace.equals("{") ? "}" : "]");
    }

    /**
     * Find any logical errors in the significant brackets in the Inform source
     * code in the document. This ignores comments, strings and words.
     *
     * @return <code>Iterator</code> for bracket error positions in the document
     */
    @Override
    public Iterator bracketErrors() {
        Set errors = new TreeSet();
        try {
            Stack stack = new Stack();
            InformLexer lexer = new InformLexer(getText(0, getLength()));
            InformToken token = lexer.nextBracket();
            InformToken.Lexeme type = token.getType();
            InformToken peek = null;
            while (type != InformToken.EOS) {
                if (type == InformToken.OPENBRACE
                        || type == InformToken.OPENBRACKET
                        || type == InformToken.OPENROUTINE) {
                    stack.push(token);
                }
                if (type == InformToken.CLOSEBRACE) {
                    if (stack.empty()) {
                        errors.add(new Integer(token.getStartPosition()));
                    } else {
                        peek = (InformToken) stack.pop();
                        if (peek.getType() != InformToken.OPENBRACE) {
                            errors.add(new Integer(peek.getStartPosition()));
                            errors.add(new Integer(token.getStartPosition()));
                        }
                    }
                }
                if (type == InformToken.CLOSEBRACKET) {
                    if (stack.empty()) {
                        errors.add(new Integer(token.getStartPosition()));
                    } else {
                        peek = (InformToken) stack.pop();
                        if (peek.getType() != InformToken.OPENBRACKET) {
                            errors.add(new Integer(peek.getStartPosition()));
                            errors.add(new Integer(token.getStartPosition()));
                        }
                    }
                }
                if (type == InformToken.CLOSEROUTINE) {
                    if (stack.empty()) {
                        errors.add(new Integer(token.getStartPosition()));
                    } else {
                        peek = (InformToken) stack.pop();
                        if (peek.getType() != InformToken.OPENROUTINE) {
                            errors.add(new Integer(peek.getStartPosition()));
                            errors.add(new Integer(token.getStartPosition()));
                        }
                    }
                }
                token = lexer.nextBracket();
                type = token.getType();
            }

            while (!stack.empty()) {
                peek = (InformToken) stack.pop();
                errors.add(new Integer(peek.getStartPosition()));
            }
        } catch (BadLocationException ex) {
        } finally {
            return errors.iterator();
        }
    }
}