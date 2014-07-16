package it.schillaci.jif.configuration;

/*
 * JifConfigurationDocument.java
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
 * e-m@il: silver.slade@tiscali.it
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
import it.schillaci.jif.gui.jFrame;
import it.schillaci.jif.inform.InformContext;
import it.schillaci.jif.inform.InformSyntax;
import java.util.Hashtable;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.GapContent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleContext;

/**
 * An extension of DefaultStyledDocument for the JifConfiguration Syntax Highlight
 *
 * @author Alessandro Schillaci
 * @author Peter Piggott 
 */
public class JifConfigurationDocument extends JifDocument {
    /**
     *
     */
    private static final long serialVersionUID = 4399244071115666713L;
    
    jFrame jframe;
    DefaultStyledDocument doc;
    MutableAttributeSet normal;
    MutableAttributeSet keyword;
    MutableAttributeSet comment;
    
    
    Hashtable keywords;

    /**
     * Constructs a JifConfiguration document with a shared set of styles for syntax
     * highlighting.
     * 
     * @param c
     *            the container for the content
     * @param styles
     *            the set of styles for Inform syntax highlighting which may be
     *            shared across documents
     */
    protected JifConfigurationDocument(Content c, StyleContext styles) {
        super(c, styles);
    }

    /**
     * Constructs a JifConfiguration document with a shared set of styles for syntax
     * highlighting.
     * 
     * @param c
     *            the container for the content
     * @param styles
     *            the set of styles for Inform syntax highlighting which may be
     *            shared across documents
     */
    public JifConfigurationDocument(Content c, InformContext styles) {
        this(c, (StyleContext) styles);
    }

    /**
     * Constructs a JifConfiguration document with the default content storage
     * implementation and a shared set of styles for syntax highlighting.
     * 
     * @param styles
     *            the set of styles for Inform syntax highlighting
     */
    protected JifConfigurationDocument(StyleContext styles) {
        this(new GapContent(BUFFER_SIZE_DEFAULT), styles);
    }

    /**
     * Constructs a JifConfiguration document with the default content storage
     * implementation and a shared set of styles for syntax highlighting.
     * 
     * @param styles
     *            The set of styles for Inform syntax highlighting
     */
    public JifConfigurationDocument(InformContext styles) {
        this((StyleContext) styles);
    }

    /**
     * Constructs a JifConfiguration document with the default content storage
     * implementation and a default set of styles for syntax highlighting.
     * 
     */
    public JifConfigurationDocument() {
        this(new GapContent(BUFFER_SIZE_DEFAULT), (StyleContext) new InformContext());
    }

    /**
     * Insert a string with syntax highlighting
     * @param offset 
     *            The offset to insert the string
     * @param str 
     *            The String to be inserted in the document
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
    public void remove(int offset, int length)
            throws BadLocationException {
        
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
        changeLength = line.getEndOffset() - changeOffset; 

        applyHighlighting(changeOffset, changeLength);

    }
    
    /**
     * Apply inform syntax highlighting to the specified portion of the document
     * @param offset 
     *            The initial offset at which to apply syntax highlighting
     * @param length 
     *            The length of the document text to be highlighted
     * @throws BadLocationException
     *            If applying highlighting fails
     */
    public void applyHighlighting(int offset, int length)
            throws BadLocationException {

        int startOffset;
        int endOffset;
        int tokenLength;
        MutableAttributeSet syntax;

        String source = getText(offset, length);
        JifConfigurationLexer lexer = new JifConfigurationLexer(source, offset);
        JifConfigurationToken token = lexer.nextElement();

        while (token.getType() != JifConfigurationToken.EOS) {

            startOffset = token.getStartPosition();
            endOffset = token.getEndPosition();
            tokenLength = endOffset - startOffset;

            if (token.getType() == JifConfigurationToken.COMMENT) {
                syntax = getStyle(InformSyntax.Comment.getName());
            } else if (token.getType() == JifConfigurationToken.SYMBOL) {
                if (JifConfigurationDAO.isKeyword(token.getContent())) {
                    syntax = getStyle(InformSyntax.Keyword.getName());
                } else {
                    syntax = getStyle(InformSyntax.Normal.getName());
                }
            } else if (token.getType() == JifConfigurationToken.WHITESPACE) {
                syntax = getStyle(InformSyntax.White.getName());
            } else {
                syntax = getStyle(InformSyntax.Normal.getName());
            }

            setCharacterAttributes(startOffset, tokenLength, syntax, true);
            token = lexer.nextElement();
        }
    }
}