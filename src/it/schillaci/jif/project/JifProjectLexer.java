package it.schillaci.jif.project;

/*
 * JifProjectLexer.java
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

import java.util.ArrayList;

/**
 * ConfigurationLexer: Used to split a string of Configuration source code into a series
 * of tokens
 * 
 * @author Peter Piggott
 * @version Revision: 1.0
 * @since 3.2
 */

public final class JifProjectLexer {

    private char[] source;

    private int offset = 0;

    private int startPos = 0;

    private int endPos = 0;

    private JifProjectToken token;

    private JifProjectToken.Lexeme tokenType;

    private String tokenContent = "";
    // Lexer processing a Configuration single or double quoted string
    private boolean inKeyword = false;
    // Marker for the end of the lexer source buffer
    private static final char endOfSourceGuard = '\u0000';

    /**
     * Creates a new Configuration lexer object for a string of Configuration source code.
     * 
     * @param ConfigurationSource
     *            The text of Configuration source code to be split into tokens
     */
    public JifProjectLexer(String ConfigurationSource) {

        this(ConfigurationSource, 0);

    }

    /**
     * Creates a new JifProject lexer object for a string of
     * JifProject source code with an offset.
     * 
     * @param configurationSource
     *            The text to be split into tokens
     * @param offset
     *            The base offset of the string within its parent string
     */
    public JifProjectLexer(String configurationSource, int offset) {

        // An end of source guard character is added to the end of the source
        // so that it can be used to safely terminate the lexer without checking
        // the index against the length all the time when incrementing the index
        this.source = (configurationSource + endOfSourceGuard).toCharArray();
        this.offset = offset;

    }

    /**
     * Creates a new JifProject lexer object for a stringbuffer of
     * JifProject source code.
     * 
     * @param configurationSource
     *            The text of Configuration source code to be split into tokens
     */
    public JifProjectLexer(StringBuffer configurationSource) {

        this(configurationSource, 0);

    }

    /**
     * Creates a new JifProject lexer object for a a stringbuffer of
     * JifProject source code with an offset.
     * 
     * @param configurationSource
     *            The text to be split into tokens
     * @param offset
     *            The base offset of the string within its parent string
     */
    public JifProjectLexer(StringBuffer configurationSource, int offset) {

        this(configurationSource.toString());
        this.offset = offset;

    }

    /**
     * Returns an array of tokens representing the markup of a JifProject
     * source code string. This filters out any whitespace and newlines.
     */
    public JifProjectToken[] getMarkup() {

        ArrayList tokens = new ArrayList();

        token = nextMarkup();
        tokenType = token.getType();

        while (tokenType != JifProjectToken.EOS) {

            tokens.add(token);

            token = nextMarkup();
            tokenType = token.getType();

        }
        return (JifProjectToken[]) tokens.toArray(new JifProjectToken[0]);
    }

    /**
     * Returns the next JifProject lexical token from the JifProject
     * source code. This filters out any comments and whitespace.
     */
    public JifProjectToken nextToken() {

        token = nextElement();
        tokenType = token.getType();

        while (tokenType == JifProjectToken.WHITESPACE
                || tokenType == JifProjectToken.COMMENT) {

            token = nextElement();
            tokenType = token.getType();

        }

        return token;

    }

    /**
     * Returns the next JifProject element for markup from the 
     * JifProject source code string. This filters out any whitespace.
     */
    public JifProjectToken nextMarkup() {

        token = nextElement();
        tokenType = token.getType();

        while (tokenType == JifProjectToken.WHITESPACE) {

            token = nextElement();
            tokenType = token.getType();

        }

        return token;

    }

    /**
     * Returns the next continuous Configuration language element from the Configuration
     * source code string. This includes comments and whitespace separating
     * language tokens.
     */
    public JifProjectToken nextElement() {

        // Continue from last call
        startPos = endPos;
        // Start building content string
        tokenContent = new String(source, endPos, 1);

        switch (source[endPos++]) {
            case '[':
                inKeyword = true;
                tokenType = JifProjectToken.SYMBOL;
                while (inKeyword) {
                    switch (source[endPos]) {
                        case ']':
                            tokenContent += source[endPos];
                            inKeyword = false;
                            ++endPos;
                            break;
                        case ',':
                        case ';':
                        case ':':
                        case '.':
                        case '@':
                        case '#':
                        case '\'':
                        case '\"':
                        case '{':
                        case '}':
                        case '(':
                        case ')':
                        case '!':
                        case '£':
                        case '$':
                        case '%':
                        case '^':
                        case '&':
                        case '*':
                        case '-':
                        case '+':
                        case '/':
                        case '\\':
                        case '|':
                        case '~':
                        case '>':
                        case '<':
                        case '¬':
                        case '`':
                        case ' ':
                        case '\t':
                        case '\n':
                        case endOfSourceGuard:
                            inKeyword = false;
                            break;
                        default:
                            tokenContent += source[endPos];
                            ++endPos;
                            break;
                    }
                }
                break;
            case '#':
                while (source[endPos] != '\n' && source[endPos] != endOfSourceGuard) {
                    tokenContent += source[endPos];
                    ++endPos;
                }
                tokenType = JifProjectToken.COMMENT;
                break;
            case ' ':
            case '\n':
            case '\t':
                while (isWhiteSpace(source[endPos])) {
                    tokenContent += source[endPos];
                    ++endPos;
                }
                tokenType = JifProjectToken.WHITESPACE;
                break;
            case endOfSourceGuard:
                tokenType = JifProjectToken.EOS;
                break;
            default:
                while (isSymbol(source[endPos])) {
                    tokenContent += source[endPos];
                    ++endPos;
                }
                tokenType = JifProjectToken.SYMBOL;
                break;
        }

        return new JifProjectToken(tokenType, startPos + offset, endPos + offset,
                tokenContent);

    }

    /**
     * Set the position within the Configuration source code where the following
     * nextToken, nextMarkup, nextElement or nextBracket method call will start.
     * 
     * @param newPos
     *            The position in the source code string where following next
     *            methods will calls will start.
     */
    public void setPosition(int newPos) {

        endPos = newPos;

    }

    /**
     * Should the character be shown as white space.
     *
     * @param c
     *            The character to be tested
     * @returns <tt>True</tt> if the character is white space.
     */
    private boolean isWhiteSpace(char c) {

        return (c == ' ' || c == '\n' || c == '\t');

    }
    
    /**
     * Should the character be shown as a delimiter.
     *
     * @param c
     *            The character to be tested
     * @returns <tt>True</tt> if the character is a delimiter.
     */
    private boolean isDelimiter(char c) {
        
        return (c == '[' || c == '#'); 
        
    }

    /**
     * Should the character be shown as a symbol.
     *
     * @param c
     *            The character to be tested
     * @returns <tt>True</tt> if the character is a symbol.
     */
    private boolean isSymbol(char c) {
        
        return !(isDelimiter(c) || isWhiteSpace(c) || c == endOfSourceGuard);
        
    }
}