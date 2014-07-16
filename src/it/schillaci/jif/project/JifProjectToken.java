package it.schillaci.jif.project;

/*
 * JifProjectToken.java
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


/**
 * JifProjectToken: Stores the information about a lexical token
 *
 * @author Peter Piggott
 * @version 1.0
 * @since 3.2
 */

public class JifProjectToken {

    // Token lexeme values
    public static final Lexeme EOS = new Lexeme("end-of-file");
    public static final Lexeme COMMENT = new Lexeme("comment");
    public static final Lexeme DELIMITER = new Lexeme("delimiter");
    public static final Lexeme KEYWORD = new Lexeme("keyword");
    public static final Lexeme STRING = new Lexeme("string");
    public static final Lexeme SYMBOL = new Lexeme("symbol");
    public static final Lexeme WHITESPACE = new Lexeme("whitespace");

    // Lexical token types
    private static final Lexeme types[] = {
        EOS,
        COMMENT,
        DELIMITER,
        KEYWORD,
        STRING,
        SYMBOL,
        WHITESPACE
    };

    public static class Lexeme {

        private static String name;

        Lexeme(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Lexeme[name: " + name + "]";
        }
    }
    // Class variables
    public Lexeme id;
    public int startPosition;
    public int endPos;
    public String content;

// Class constructors
    /**
     * Creates a new JifProject token object identifying a lexical token within
     * the JifProject file.
     *
     * @param id A lexeme identifying the type of token.
     * @param startPos The position of the start of the token text within the
     * Jif Configuration file.
     * @param endPos The position of the end of the token text within the Jif
     * Configuration file.
     * @param content The source text of the Configuration lexical token.
     */
    public JifProjectToken(Lexeme id, int startPos, int endPos, String content) {

        this(id, startPos, endPos);
        this.content = content;

    }

    /**
     * Creates a new Jif Configuration token object identifying a lexical token
     * within the Jif Configuration file.
     *
     * @param id A lexeme identifying the type of token.
     * @param startPos The position of the start of the token text within the
     * Jif Configuration file.
     * @param endPos The position of the end of the token text within the Jif
     * Configuration file.
     */
    public JifProjectToken(Lexeme id, int startPos, int endPos) {

        this.id = id;
        this.startPosition = startPos;
        this.endPos = endPos;

    }

// Class methods
    
    /**
     * Gets the name of the Configuration lexical token.
     */
    public String getName() {

        return id.getName();
    }

    /**
     * Sets the lexeme representing the type of the Configuration lexical token.
     *
     * @param id a lexeme identifying The type of token.
     */
    public void setType(Lexeme id) {

        this.id = id;
    }

    /**
     * Gets a lexeme representing the type of the Configuration lexical token.
     */
    public Lexeme getType() {

        return id;
    }

    /**
     * Gets the start position of the Configuration lexical token.
     */
    public int getStartPosition() {

        return startPosition;
    }

    /**
     * Gets the end position of the Configuration lexical token.
     */
    public int getEndPosition() {

        return endPos;
    }

    /**
     * Gets the source of the Configuration lexical token.
     */
    public String getContent() {

        return content;
    }

    /**
     * Converts the Configuration token into a string representation.
     */
    @Override
    public String toString() {

        return "JifProjectToken[id: " + id.getName()
                + ", start: " + startPosition
                + ", end: " + endPos
                + ", content: " + content
                + "]";
    }
}