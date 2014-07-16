package it.schillaci.jif.resource;

/*
 * Resource.java
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

import java.util.HashMap;
import java.util.Map;

/**
 * Constants used in the <code>ResourceDocument</code>. These are basically
 * Resource keyword definitions.
 * 
 * @author  Peter Piggott
 * @version 1.0
 * @since   JIF 3.2
 */
public class Resource {

    /**
     * Type safe enumeration for Resource keywords
     * 
     */
    public static class Keyword {
        /**
         * Creates a new <code>Keyword</code> with the specified name.
         * 
         * @param name
         *            the name of the keyword
         */
        protected Keyword(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String toString() {
            return "Resource.Keyword[Name: " + name + "]";
        }

        // Resource keyword name
        private String name;

        // --- Resource Keyords ------------------------------------------------
        
        public static final Keyword Code = new Keyword("code");
        public static final Keyword Picture = new Keyword("picture");
        public static final Keyword Sound = new Keyword("sound");

        // Resource keywords (not case sensitive)
        static final Keyword[] keywords = {
            Code,
            Picture,
            Sound
        };

        static {
            // Force Resource's static initialise to be loaded.
            boolean temp = isKeyword("code");
        }
    }

    // Map to hold Resource keyords
    private static final Map keywords = new HashMap();

    // Initialisation
    static {
        // Resource keywords
        for (int i = 0; i < Keyword.keywords.length; i++) {
            keywords.put(Keyword.keywords[i].getName(), Keyword.keywords[i]);
        }
    }

    /**
     * Returns <tt>true</tt> if the specified symbol is a resource keyword
     * 
     * @param symbol
     *            Symbol to be tested as a Resource keyword
     * @return <tt>true</tt> if this is an Resource keyword
     */
    public static boolean isKeyword(String symbol) {
        return keywords.containsKey(symbol.toLowerCase());
    }

}