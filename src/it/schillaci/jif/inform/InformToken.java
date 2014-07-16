package it.schillaci.jif.inform;

/*
 * InformToken.java - Lexical token for the inform language
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
 * InformToken: Stores the information about a lexical token
 *
 * @author Peter Piggott
 * @version 2.0
 * @since 3.1
 */

public class InformToken {
    
    // Token lexeme values
    
    public static final Lexeme EOS = new Lexeme("end-of-file");
    public static final Lexeme AMPERSAND = new Lexeme("ampersand");
    public static final Lexeme AND = new Lexeme("and");
    public static final Lexeme ANDAND = new Lexeme("and-and");
    public static final Lexeme ARROW = new Lexeme("arrow");
    public static final Lexeme ARROWARROW = new Lexeme("arrow-arrow");
    public static final Lexeme ARROWEQUAL = new Lexeme("arrow-equal");
    public static final Lexeme BINARY = new Lexeme("binary");
    public static final Lexeme CLOSEBRACE = new Lexeme("close-brace");
    public static final Lexeme CLOSEBRACKET = new Lexeme("close-bracket");
    public static final Lexeme CLOSEROUTINE = new Lexeme("close-routine");
    public static final Lexeme COLON = new Lexeme("colon");
    public static final Lexeme COLONCOLON = new Lexeme("colon-colon");
    public static final Lexeme COMMA = new Lexeme("comma");
    public static final Lexeme COMMENT = new Lexeme("comment");
    public static final Lexeme DIVIDE = new Lexeme("divide");
    public static final Lexeme DOT = new Lexeme("dot");
    public static final Lexeme DOTAMPERSAND = new Lexeme("dot-ampersand");
    public static final Lexeme DOTHASH = new Lexeme("dot-hash");
    public static final Lexeme DOTDOT = new Lexeme("dot-dot");
    public static final Lexeme DOTDOTAMPERSAND = new Lexeme("dot-dot-ampersand");
    public static final Lexeme DOTDOTHASH = new Lexeme("dot-dot-hash");
    public static final Lexeme EQUAL = new Lexeme("equal");
    public static final Lexeme EQUALARROW = new Lexeme("equal-arrow");
    public static final Lexeme EQUALEQUAL = new Lexeme("equal-equal");
    public static final Lexeme HASH = new Lexeme("hash");
    public static final Lexeme HASHHASH = new Lexeme("hash-hash");
    public static final Lexeme HASHADOLLAR = new Lexeme("hash-a-dollar");
    public static final Lexeme HASHNDOLLAR = new Lexeme("hash-n-dollar");
    public static final Lexeme HASHRDOLLAR = new Lexeme("hash-r-dollar");
    public static final Lexeme HASHWDOLLAR = new Lexeme("hash-w-dollar");
    public static final Lexeme HEXIDECIMAL = new Lexeme("hexidecimal");
    public static final Lexeme INVALID = new Lexeme("invalid");
    public static final Lexeme LESS = new Lexeme("less");
    public static final Lexeme LESSEQUAL = new Lexeme("less-equal");
    public static final Lexeme LESSLESS = new Lexeme("less-less");
    public static final Lexeme MINUS = new Lexeme("minus");
    public static final Lexeme MINUSARROW = new Lexeme("minus-arrow");
    public static final Lexeme MINUSMINUS = new Lexeme("minus-minus");
    public static final Lexeme MINUSMINUSARROW = new Lexeme("minus-minus-arrow");
    public static final Lexeme NEWLINE = new Lexeme("newline");
    public static final Lexeme NOT = new Lexeme("not");
    public static final Lexeme NOTEQUAL = new Lexeme("not-equal");
    public static final Lexeme NOTNOT = new Lexeme("not-not");
    public static final Lexeme NUMBER = new Lexeme("number");
    public static final Lexeme OPENBRACE = new Lexeme("open-brace");
    public static final Lexeme OPENBRACKET = new Lexeme("open-bracket");
    public static final Lexeme OPENROUTINE = new Lexeme("open-routine");
    public static final Lexeme OR = new Lexeme("or");
    public static final Lexeme OROR = new Lexeme("or-or");
    public static final Lexeme PLUS = new Lexeme("plus");
    public static final Lexeme PLUSPLUS = new Lexeme("plus-plus");
    public static final Lexeme REMAINDER = new Lexeme("remainder");
    public static final Lexeme SEMICOLON = new Lexeme("semicolon");
    public static final Lexeme STRING = new Lexeme("string");
    public static final Lexeme SYMBOL = new Lexeme("symbol");
    public static final Lexeme TIMES = new Lexeme("times");
    public static final Lexeme WHITESPACE = new Lexeme("whitespace");
    public static final Lexeme WORD = new Lexeme("word");
    
   // Lexical token types

    private static final Lexeme types[] =
        {EOS,
         AMPERSAND,
         AND,
         ANDAND,
         ARROW,
         ARROWARROW,
         ARROWEQUAL,
         BINARY,
         CLOSEBRACE,
         CLOSEBRACKET,
         CLOSEROUTINE,
         COLON,
         COLONCOLON,
         COMMA,
         COMMENT,
         DIVIDE,
         DOT,
         DOTAMPERSAND,
         DOTHASH,
         DOTDOT,
         DOTDOTAMPERSAND,
         DOTDOTHASH,
         EQUAL,
         EQUALARROW,
         EQUALEQUAL,
         HASH,
         HASHHASH,
         HASHADOLLAR,
         HASHNDOLLAR,
         HASHRDOLLAR,
         HASHWDOLLAR,
         HEXIDECIMAL,
         INVALID,
         LESS,
         LESSEQUAL,
         LESSLESS,
         MINUS,
         MINUSARROW,
         MINUSMINUS,
         MINUSMINUSARROW,
         NEWLINE,
         NOT,
         NOTEQUAL,
         NOTNOT,
         NUMBER,
         OPENBRACE,
         OPENBRACKET,
         OPENROUTINE,
         OR,
         OROR,
         PLUS,
         PLUSPLUS,
         REMAINDER,
         SEMICOLON,
         STRING,
         SYMBOL,
         TIMES,
         WHITESPACE,
         WORD
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
   public int endPosition;
   public String content;


// Class constructors

   /**
    * Creates a new Inform token object identifying a lexical token within the
    * Inform source code.
    *
    * @param id
    *           A number identifying the type of token.
    * @param startPos 
    *           The position of the start of the token text within the Inform
    *           source code.
    * @param endPos
    *           The position of the end of the token text within the Inform
    *           source code.
    * @param content
    *           The source text of the Inform lexical token.
    */
   public InformToken(Lexeme id, int startPos, int endPos, String content) {

     this(id, startPos, endPos);
     this.content = content;

   }

   /**
    * Creates a new Inform token object identifying a lexical token within the
    * Inform source code.
    *
    * @param id
    *           A number identifying the type of token.
    * @param startPos
    *           The position of the start of the token text within the Inform
    *           source code.
    * @param endPos
    *           The position of the end of the token text within the Inform
    *           source code.
    */
   public InformToken(Lexeme id, int startPos, int endPos) {

     this.id = id;
     this.startPosition = startPos;
     this.endPosition = endPos;

   }
// Class methods

   /**
    * Gets the name of the Inform lexical token.
    */
   public String getName() {

      return id.getName();
   }

   /**
    * Sets the number representing the type of the Inform lexical token.
    *
    * @param id 
    *           A lexeme identifying the type of token.
    */
   public void setType(Lexeme id) {

         this.id = id;
   }

   /**
    * Gets a lexeme representing the type of the Inform lexical token.
    */
   public Lexeme getType() {

      return id;
   }

   /**
    * Gets the start position of the Inform lexical token.
    */
   public int getStartPosition() {

      return startPosition;
   }

   /**
    * Gets the end position of the Inform lexical token.
    */
   public int getEndPosition() {

      return endPosition;
   }

   /**
    * Gets the source of the Inform lexical token.
    */
   public String getContent() {

      return content;
   }

   /**
    * Converts the Inform token into a string representation.
    */
    @Override
   public String toString() {

      return "InformToken[id: " + id.getName() + 
              ", start: " + startPosition +
              ", end: " + endPosition +
              ", content: " + content + 
              "]";
   }
}