package it.schillaci.jif.inform;

/*
 * InformLexer.java
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
 * InformLexer: Used to split a string of Inform source code into a series of
 * tokens
 * 
 * @author Peter Piggott
 * @version Revision: 1.2
 * @since 3.1
 */

public final class InformLexer {
    // Buffer for source
    private char[] source;
    // Position of source buffer within source document
    private int offset = 0;
    // Start position of current token
    private int startPos = 0;
    // Current position of current token (end position when token complete)
    private int endPos = 0;
    // Current token
    private InformToken token;
    // Current token type
    private InformToken.Lexeme tokenType;
    // Current token content
    private String tokenContent = "";
    // Lexer processing an Inform single quoted word
    private boolean inWord = false;
    // Lexer processing an Inform double quoted string
    private boolean inString = false;
    // Marker for the end of the lexer source buffer
    private static final char endOfSourceGuard = '\u0000';

    /**
     * Creates a new Inform lexer object for a string of Inform source code.
     * 
     * @param informSource
     *            The text of Inform source code to be split into tokens
     */
    public InformLexer(String informSource) {

        this(informSource, 0);

    }

    /**
     * Creates a new Inform lexer object for a string of Inform source code with
     * an offset.
     * 
     * @param informSource
     *            The text to be split into tokens
     * @param offset
     *            The base offset of the string within its parent string
     */
    public InformLexer(String informSource, int offset) {

        // An end of source guard character is added to the end of the source so that
        // it can be used to safely terminate the lexer without checking the
        // index against the length all the time when incrementing the index.
        this.source = (informSource + endOfSourceGuard).toCharArray();
        this.offset = offset;

    }

    /**
     * Creates a new Inform lexer object for a stringbuffer of Inform source
     * code.
     * 
     * @param informSource
     *            The text of Inform source code to be split into tokens
     */
    public InformLexer(StringBuffer informSource) {

        this(informSource, 0);

    }

    /**
     * Creates a new Inform lexer object for a stringbuffer of Inform source
     * code with an offset.
     * 
     * @param informSource
     *            The text to be split into tokens
     * @param offset
     *            The base offset of the string within its parent string
     */
    public InformLexer(StringBuffer informSource, int offset) {

        this(informSource.toString());
        this.offset = offset;

    }

    /**
     * Returns an array of tokens representing the tokens of an Inform source
     * code string. This filters out any comments, whitespace and newlines.
     */
    public InformToken[] getTokens() {

        ArrayList tokens = new ArrayList();

        token = nextToken();
        tokenType = token.getType();

        while (tokenType != InformToken.EOS) {

            tokens.add(token);

            token = nextToken();
            tokenType = token.getType();

        }
        return (InformToken[]) tokens.toArray(new InformToken[0]);
    }

    /**
     * Returns an array of tokens representing the markup of an Inform source
     * code string. This filters out any whitespace and newlines.
     */
    public InformToken[] getMarkup() {

        ArrayList tokens = new ArrayList();

        token = nextMarkup();
        tokenType = token.getType();

        while (tokenType != InformToken.EOS) {

            tokens.add(token);

            token = nextMarkup();
            tokenType = token.getType();

        }
        return (InformToken[]) tokens.toArray(new InformToken[0]);
    }

    /**
     * Returns the next bracket token from the Inform source code string. This
     * filters all tokens except for open/close brackets (, ), {, }, [ or ].
     */
    public InformToken nextBracket() {

        token = nextElement();
        tokenType = token.getType();

        while (tokenType != InformToken.OPENBRACE
                && tokenType != InformToken.OPENBRACKET
                && tokenType != InformToken.OPENROUTINE
                && tokenType != InformToken.CLOSEBRACE
                && tokenType != InformToken.CLOSEBRACKET
                && tokenType != InformToken.CLOSEROUTINE
                && tokenType != InformToken.EOS) {

            token = nextElement();
            tokenType = token.getType();

        }

        return token;

    }

    /**
     * Returns the next Inform language lexical token from the Inform source
     * code. This filters out any comments, whitespace and newlines.
     */
    public InformToken nextToken() {

        token = nextElement();
        tokenType = token.getType();

        while (tokenType == InformToken.WHITESPACE
                || tokenType == InformToken.NEWLINE
                || tokenType == InformToken.COMMENT) {

            token = nextElement();
            tokenType = token.getType();

        }

        return token;

    }

    /**
     * Returns the next Inform language element for markup from the Inform
     * source code string. This filters out any whitespace and newlines.
     */
    public InformToken nextMarkup() {

        token = nextElement();
        tokenType = token.getType();

        while (tokenType == InformToken.WHITESPACE
                || tokenType == InformToken.NEWLINE) {

            token = nextElement();
            tokenType = token.getType();

        }

        return token;

    }

    /**
     * Returns the next continuous Inform language element from the Inform
     * source code string. This includes comments, whitespace and newlines
     * separating language tokens.
     */
    public InformToken nextElement() {

        // Continue from last call
        startPos = endPos;
        // Start building content string
        tokenContent = new String(source, endPos, 1);

        switch (source[endPos++]) {
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
                while (isIdentifier(source[endPos])) {
                    tokenContent += source[endPos];
                    ++endPos;
                }
                tokenType = InformToken.SYMBOL;
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                while (isDecimal(source[endPos])) {
                    tokenContent += source[endPos];
                    ++endPos;
                }
                tokenType = InformToken.NUMBER;
                break;
            case '\"':
                inString = true;
                tokenType = InformToken.STRING;
                while (inString) {
                    switch (source[endPos]) {
                        case '\"':
                            tokenContent += source[endPos];
                            inString = false;
                            ++endPos;
                            break;
                        case endOfSourceGuard:
                            inString = false;
                            break;
                        default:
                            tokenContent += source[endPos];
                            ++endPos;
                            break;
                    }
                }
                break;
            case '\'':
                inWord = true;
                while (inWord) {
                    switch (source[endPos]) {
                        case '\'':
                            tokenContent += source[endPos];
                            if (source[endPos - 1] != '@') {
                                inWord = false;
                            }
                            ++endPos;
                            break;
                        case '\n':
                        case endOfSourceGuard:
                            inWord = false;
                            break;
                        default:
                            tokenContent += source[endPos];
                            ++endPos;
                            break;
                    }
                }
                tokenType = InformToken.WORD;
                break;
            case '!':
                while (source[endPos] != '\n' && source[endPos] != endOfSourceGuard) {
                    tokenContent += source[endPos];
                    ++endPos;
                }
                tokenType = InformToken.COMMENT;
                break;
            case ' ':
            case '\t':
                while (isWhiteSpace(source[endPos])) {
                    tokenContent += source[endPos];
                    ++endPos;
                }
                tokenType = InformToken.WHITESPACE;
                break;
            case '$':
                if (source[endPos] == '$') {
                    tokenContent += source[endPos];
                    ++endPos;
                    while (isBinary(source[endPos])) {
                        tokenContent += source[endPos];
                        ++endPos;
                    }
                    tokenType = InformToken.BINARY;
                } else {
                    while (isHexidecimal(source[endPos])) {
                        tokenContent += source[endPos];
                        ++endPos;
                    }
                    tokenType = InformToken.HEXIDECIMAL;
                }
                break;
            case '#':
                switch (source[endPos]) {
                    case '#':
                        ++endPos;
                        tokenType = InformToken.HASHHASH;
                        tokenContent = "##";
                        break;
                    case 'a':
                        if (source[endPos + 1] == '$') {
                            tokenType = InformToken.HASHADOLLAR;
                            tokenContent = "#a$";
                            endPos = endPos + 2;
                        } else {
                            tokenType = InformToken.HASH;
                        }
                        break;
                    case 'n':
                        if (source[endPos + 1] == '$') {
                            tokenType = InformToken.HASHNDOLLAR;
                            tokenContent = "#n$";
                            endPos = endPos + 2;
                        } else {
                            tokenType = InformToken.HASH;
                        }
                        break;
                    case 'r':
                        if (source[endPos + 1] == '$') {
                            tokenType = InformToken.HASHRDOLLAR;
                            tokenContent = "#r$";
                            endPos = endPos + 2;
                        } else {
                            tokenType = InformToken.HASH;
                        }
                        break;
                    case 'w':
                        if (source[endPos + 1] == '$') {
                            tokenType = InformToken.HASHWDOLLAR;
                            tokenContent = "#w$";
                            endPos = endPos + 2;
                        } else {
                            tokenType = InformToken.HASH;
                        }
                        break;
                    default:
                        tokenType = InformToken.HASH;
                        break;
                }
                break;
            case '.':
                switch (source[endPos]) {
                    case '.':
                        ++endPos;
                        switch (source[endPos]) {
                            case '#':
                                ++endPos;
                                tokenType = InformToken.DOTDOTHASH;
                                tokenContent = "..#";
                                break;
                            case '@':
                                ++endPos;
                                tokenType = InformToken.DOTDOTAMPERSAND;
                                tokenContent = "..@";
                                break;
                            default:
                                tokenType = InformToken.DOTDOT;
                                tokenContent = "..";
                                break;
                        }
                        break;
                    case '#':
                        ++endPos;
                        tokenType = InformToken.DOTHASH;
                        tokenContent = ".#";
                        break;
                    case '@':
                        ++endPos;
                        tokenType = InformToken.DOTAMPERSAND;
                        tokenContent = ".@";
                        break;
                    default:
                        tokenType = InformToken.DOT;
                        break;
                }
                break;
            case '-':
                switch (source[endPos]) {
                    case '-':
                        ++endPos;
                        if (source[endPos] == '>') {
                            ++endPos;
                            tokenType = InformToken.MINUSMINUSARROW;
                            tokenContent = "-->";
                        } else {
                            tokenType = InformToken.MINUSMINUS;
                            tokenContent = "--";
                        }
                        break;
                    case '>':
                        ++endPos;
                        tokenType = InformToken.MINUSARROW;
                        tokenContent = "->";
                        break;
                    default:
                        tokenType = InformToken.MINUS;
                        break;
                }
                break;
            case '=':
                switch (source[endPos]) {
                    case '=':
                        ++endPos;
                        tokenType = InformToken.EQUALEQUAL;
                        tokenContent = "==";
                        break;
                    case '>':
                        ++endPos;
                        tokenType = InformToken.EQUALARROW;
                        tokenContent = "=>";
                        break;
                    default:
                        tokenType = InformToken.EQUAL;
                        break;
                }
                break;
            case '~':
                switch (source[endPos]) {
                    case '~':
                        ++endPos;
                        tokenType = InformToken.NOTNOT;
                        tokenContent = "~~";
                        break;
                    case '=':
                        ++endPos;
                        tokenType = InformToken.NOTEQUAL;
                        tokenContent = "~=";
                        break;
                    default:
                        tokenType = InformToken.NOT;
                        break;
                }
                break;
            case '<':
                switch (source[endPos]) {
                    case '<':
                        ++endPos;
                        tokenType = InformToken.LESSLESS;
                        tokenContent = "<<";
                        break;
                    case '=':
                        ++endPos;
                        tokenType = InformToken.LESSEQUAL;
                        tokenContent = "<=";
                        break;
                    default:
                        tokenType = InformToken.LESS;
                        break;
                }
                break;
            case '>':
                switch (source[endPos]) {
                    case '>':
                        ++endPos;
                        tokenType = InformToken.ARROWARROW;
                        tokenContent = ">>";
                        break;
                    case '=':
                        ++endPos;
                        tokenType = InformToken.ARROWEQUAL;
                        tokenContent = ">=";
                        break;
                    default:
                        tokenType = InformToken.ARROW;
                        break;
                }
                break;
            case '+':
                if (source[endPos] == '+') {
                    ++endPos;
                    tokenType = InformToken.PLUSPLUS;
                    tokenContent = "++";
                } else {
                    tokenType = InformToken.PLUS;
                }
                break;
            case '&':
                if (source[endPos] == '&') {
                    ++endPos;
                    tokenType = InformToken.ANDAND;
                    tokenContent = "&&";
                } else {
                    tokenType = InformToken.AND;
                }
                break;
            case '|':
                if (source[endPos] == '|') {
                    ++endPos;
                    tokenType = InformToken.OROR;
                    tokenContent = "||";
                } else {
                    tokenType = InformToken.OR;
                }
                break;
            case ':':
                if (source[endPos] == ':') {
                    ++endPos;
                    tokenType = InformToken.COLONCOLON;
                    tokenContent = "::";
                } else {
                    tokenType = InformToken.COLON;
                }
                break;
            case ';':
                tokenType = InformToken.SEMICOLON;
                break;
            case ',':
                tokenType = InformToken.COMMA;
                break;
            case '@':
                tokenType = InformToken.AMPERSAND;
                break;
            case '*':
                tokenType = InformToken.TIMES;
                break;
            case '/':
                tokenType = InformToken.DIVIDE;
                break;
            case '%':
                tokenType = InformToken.REMAINDER;
                break;
            case '{':
                tokenType = InformToken.OPENBRACE;
                break;
            case '(':
                tokenType = InformToken.OPENBRACKET;
                break;
            case '[':
                tokenType = InformToken.OPENROUTINE;
                break;
            case '}':
                tokenType = InformToken.CLOSEBRACE;
                break;
            case ')':
                tokenType = InformToken.CLOSEBRACKET;
                break;
            case ']':
                tokenType = InformToken.CLOSEROUTINE;
                break;
            case '\n':
                tokenType = InformToken.NEWLINE;
                break;
            case endOfSourceGuard:
                tokenType = InformToken.EOS;
                break;
            default:
                tokenType = InformToken.INVALID;
                break;
        }

        return new InformToken(tokenType, startPos + offset, endPos + offset,
                tokenContent);

    }

    /**
     * Set the position within the Inform source code where the following
     * nextToken, nextMarkup, nextElement or nextBracket method call will start.
     * 
     * @param newPos
     *            The position in the source code string where following next
     *            methods will calls will start.
     */
    public void setPosition(int newPos) {

        endPos = newPos;

    }

    private boolean isWhiteSpace(char c) {

        if (c == ' ' || c == '\t') {
            return true;
        } else {
            return false;
        }

    }

    private boolean isBinary(char c) {

        if (c == '0' || c == '1') {
            return true;
        } else {
            return false;
        }

    }

    private boolean isDecimal(char c) {

        if (c >= '0' && c <= '9') {
            return true;
        } else {
            return false;
        }

    }

    private boolean isHexidecimal(char c) {

        if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f')
                || (c >= 'A' && c <= 'F')) {
            return true;
        } else {
            return false;
        }

    }

    private boolean isIdentifier(char c) {

        if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z') || (c == '_')) {
            return true;
        } else {
            return false;
        }

    }
}