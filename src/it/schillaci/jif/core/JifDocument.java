package it.schillaci.jif.core;

/*
 * JifDocument.java
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

import it.schillaci.jif.inform.InformContext;
import it.schillaci.jif.inform.InformLexer;
import it.schillaci.jif.inform.InformSyntax;
import it.schillaci.jif.inform.InformToken;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.GapContent;
import javax.swing.text.StyleContext;

/**
 * An extension of DefaultStyledDocument for Inform Syntax Highlighting
 * 
 * @author Peter Piggott
 * @version 2.0
 * @since JIF 3.1
 */
public class JifDocument extends DefaultStyledDocument {

    /**
     * 
     */
    private static final long serialVersionUID = 1556152317453621340L;
    
    private static int tabSize = InformContext.defaultTabSize;
    private static final String defaultComment = "!";

    /**
     * Constructs a Jif document with a shared set of styles for syntax
     * highlighting.
     * 
     * @param c
     *            the container for the content
     * @param styles
     *            the set of styles for Inform syntax highlighting which may be
     *            shared across documents
     */
    public JifDocument(Content c, StyleContext styles) {
        super(c, styles);
        putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
    }
    
    public JifDocument(InformContext styles) {
        this(new GapContent(BUFFER_SIZE_DEFAULT), (StyleContext)styles);
    }
    
    public JifDocument() {
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

    }

    // TODO
    // JifDocument specific routine but this is really Inform source code specific
    public Iterator bracketErrors() {
        Set errors = new TreeSet();
        try {
            int routine = 0;
            Stack stack = new Stack();
            InformLexer lexer = new InformLexer(getText(0, getLength()));
            InformToken token = lexer.nextBracket();
            InformToken peek;
            InformToken.Lexeme type = token.getType();
            while (type != InformToken.EOS) {
                if (type == InformToken.OPENBRACE ||
                        type == InformToken.OPENBRACKET ||
                        type == InformToken.OPENROUTINE) {
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
    
    // --- Class methods ----------------------------------------------------
    /**
     * get the tab size for indenting Jif documents
     * 
     * @return
     *            The number of spaces in one level of indenting
     */
    public static int getTabSize() {
        return tabSize;
    }

    /**
     * Set the tab size for indenting Jif documents
     * 
     * @param tabSize
     *            The number of spaces in one level of indenting
     */
    public static void setTabSize(int tabSize) {
        JifDocument.tabSize = tabSize;
    }
}