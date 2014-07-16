package it.schillaci.jif.resource;

/*
 * ResourceLexer.java
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
 * ResourceLexer: Used to split a string of Resource source code into a series
 * of tokens
 * 
 * @author Peter Piggott
 * @version Revision: 1.0
 * @since 3.2
 */

public final class ResourceLexer {

    private char[] source;

    private int offset = 0;

    private int startPos = 0;

    private int endPos = 0;

    private ResourceToken token;

    private ResourceToken.Lexeme tokenType;

    private String tokenContent = "";
    // Lexer processing a Resource single or double quoted string
    private boolean inString = false;
    // Marker for the end of the lexer source buffer
    private static final char endOfSourceGuard = '\u0000';

    /**
     * Creates a new Resource lexer object for a string of Resource source code.
     * 
     * @param resourceSource
     *            The text of Resource source code to be split into tokens
     */
    public ResourceLexer(String resourceSource) {

        this(resourceSource, 0);

    }

    /**
     * Creates a new Resource lexer object for a string of Resource source code
     * with an offset.
     * 
     * @param resourceSource
     *            The text to be split into tokens
     * @param offset
     *            The base offset of the string within its parent string
     */
    public ResourceLexer(String resourceSource, int offset) {

        // An end of source guard character is added to the end of the source
        // so that it can be used to safely terminate the lexer without checking
        // the index against the length all the time when incrementing the index
        this.source = (resourceSource + endOfSourceGuard).toCharArray();
        this.offset = offset;

    }

    /**
     * Creates a new Resource lexer object for a stringbuffer of Resource source
     * code.
     * 
     * @param resourceSource
     *            The text of Resource source code to be split into tokens
     */
    public ResourceLexer(StringBuffer resourceSource) {

        this(resourceSource, 0);

    }

    /**
     * Creates a new Resource lexer object for a stringbuffer of Resource source
     * code with an offset.
     * 
     * @param resourceSource
     *            The text to be split into tokens
     * @param offset
     *            The base offset of the string within its parent string
     */
    public ResourceLexer(StringBuffer resourceSource, int offset) {

        this(resourceSource.toString());
        this.offset = offset;

    }

    /**
     * Returns an array of tokens representing the markup of an Resource source
     * code string. This filters out any whitespace and newlines.
     */
    public ResourceToken[] getMarkup() {

        ArrayList tokens = new ArrayList();

        token = nextMarkup();
        tokenType = token.getType();

        while (tokenType != ResourceToken.EOS) {

            tokens.add(token);

            token = nextMarkup();
            tokenType = token.getType();

        }
        return (ResourceToken[]) tokens.toArray(new ResourceToken[0]);
    }

    /**
     * Returns the next Resource language lexical token from the Resource source
     * code. This filters out any comments and whitespace.
     */
    public ResourceToken nextToken() {

        token = nextElement();
        tokenType = token.getType();

        while (tokenType == ResourceToken.WHITESPACE
                || tokenType == ResourceToken.COMMENT) {

            token = nextElement();
            tokenType = token.getType();

        }

        return token;

    }

    /**
     * Returns the next Resource language element for markup from the Resource
     * source code string. This filters out any whitespace.
     */
    public ResourceToken nextMarkup() {

        token = nextElement();
        tokenType = token.getType();

        while (tokenType == ResourceToken.WHITESPACE) {

            token = nextElement();
            tokenType = token.getType();

        }

        return token;

    }

    /**
     * Returns the next continuous Resource language element from the Resource
     * source code string. This includes comments and whitespace separating
     * language tokens.
     */
    public ResourceToken nextElement() {

        // Continue from last call
        startPos = endPos;
        // Start building content string
        tokenContent = new String(source, endPos, 1);

        switch (source[endPos++]) {
            case '\"':
                inString = true;
                while (inString) {
                    switch (source[endPos]) {
                        case '\"':
                            tokenContent += source[endPos];
                            if (source[endPos - 1] != '\\') {
                                inString = false;
                            }
                            ++endPos;
                            break;
                        case '\n':
                        case endOfSourceGuard:
                            inString = false;
                            break;
                        default:
                            tokenContent += source[endPos];
                            ++endPos;
                            break;
                    }
                }
                tokenType = ResourceToken.STRING;
                break;
            case '\'':
                inString = true;
                while (inString) {
                    switch (source[endPos]) {
                        case '\'':
                            tokenContent += source[endPos];
                            if (source[endPos - 1] != '\\') {
                                inString = false;
                            }
                            ++endPos;
                            break;
                        case '\n':
                        case endOfSourceGuard:
                            inString = false;
                            break;
                        default:
                            tokenContent += source[endPos];
                            ++endPos;
                            break;
                    }
                }
                tokenType = ResourceToken.STRING;
                break;
            case '!':
                while (source[endPos] != '\n' && source[endPos] != endOfSourceGuard) {
                    tokenContent += source[endPos];
                    ++endPos;
                }
                tokenType = ResourceToken.COMMENT;
                break;
            case ' ':
            case '\n':
            case '\t':
                while (isWhiteSpace(source[endPos])) {
                    tokenContent += source[endPos];
                    ++endPos;
                }
                tokenType = ResourceToken.WHITESPACE;
                break;
            case '^':
            case '.':
            case '-':
            case '=':
            case '~':
            case '<':
            case '>':
            case '+':
            case '&':
            case '|':
            case ':':
            case ';':
            case ',':
            case '*':
            case '/':
            case '%':
            case '{':
            case '(':
            case '[':
            case '}':
            case ')':
            case ']':
                while (isDelimiter(source[endPos])) {
                    tokenContent += source[endPos];
                    ++endPos;
                }
                tokenType = ResourceToken.DELIMITER;
                break;
            case endOfSourceGuard:
                tokenType = ResourceToken.EOS;
                break;
            default:
                while (isSymbol(source[endPos])) {
                    tokenContent += source[endPos];
                    ++endPos;
                }
                tokenType = ResourceToken.SYMBOL;
                break;
        }

        return new ResourceToken(tokenType, startPos + offset, endPos + offset,
                tokenContent);

    }

    /**
     * Set the position within the Resource source code where the following
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
        
        String operands = "\'\";:{}()[]+-/%<=>&|^~*,.";
        return operands.indexOf(c) != -1; 
        
    }

    /**
     * Should the character be shown as a symbol.
     *
     * @param c
     *            The character to be tested
     * @returns <tt>True</tt> if the character is a symbol.
     */
    private boolean isSymbol(char c) {
        
        return !isDelimiter(c) && !isWhiteSpace(c);
        
    }
}