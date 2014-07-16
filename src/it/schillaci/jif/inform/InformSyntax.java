package it.schillaci.jif.inform;

/*
 * InformSyntax.java
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

import java.awt.Color;

/**
 * InformSyntax: Type safe enumeration class for the categories of Inform
 * language lexical tokens for the purposes of JIF syntax highlighting.
 * 
 * @author Peter Piggott
 * @version 1.0
 * @since JIF 3.2
 */

public class InformSyntax {

    //Name of the syntax type  
    private String name;
    
    //Whether this syntax type is shown in bold
    private boolean bold;
    
    //Whether this syntax type is shown in italic
    private boolean italic;
    
    //The default light colour for this syntax type
    private Color light;

    //The default dark colour for this syntax type
    private Color dark;
    
    /**
     * Constructor to create a new Inform syntax type.
     *
     * @param name 
     *            The name of the syntax type.
     * @param bold
     *            Whether this syntax type is shown in bold.
     * @param italic
     *            Whether this syntax type is shown in italics.
     * @param light
     *            The default light color for the syntax type.
     * @param dark
     *            The default dark color for the syntax type.
     */
    private InformSyntax(String name, 
            boolean bold,
            boolean italic, 
            Color light,
            Color dark) {
        
        this.name = name;
        this.bold = bold;
        this.italic = italic;
        this.light = light;
        this.dark = dark;
    }

    /**
     * Constructor to create a new Inform syntax type without italics.
     *
     * @param name 
     *            The name of the syntax type.
     * @param bold
     *            Whether this syntax type is shown in bold.
     * @param light
     *            The default light color for the syntax type.
     * @param dark
     *            The default dark color for the syntax type.
     */
    private InformSyntax(String name, boolean bold, Color light, Color dark) {
        this(name, bold, false, light, dark);
    }
        
    /**
     * Constructor to create a new Inform syntax type without bold or italics.
     *
     * @param name 
     *            The name of the syntax type.
     * @param light
     *            The default light color for the syntax type.
     * @param dark
     *            The default dark color for the syntax type.
     */
    private InformSyntax(String name, Color light, Color dark) {
        this(name, false, false, light, dark);
    }

    public boolean isBold() {
        return bold;
    }

    public boolean isItalic() {
        return italic;
    }

    public String getName() {
        return name;
    }
    
    public String getNameX() {
        return name+"x";
    }
    
    public Color getDarkColor() {
        return dark;
    }

    public Color getLightColor() {
        return light;
    }
    
    // --- Object methods ------------------------------------------

    public String toString() {
        return "InformSyntax[Name: " + name + 
                ", Bold: " + bold + 
                ", Italic: " + italic +
                ", Light: " + light +
                ", Dark: " + dark +
                "]";
    }

    //--- Class variables ------------------------------------------------------
    
    // Names for common background for syntax highlighting styles 
    public static final InformSyntax Background =
        new InformSyntax("background", new Color(255,255,255), new Color(0,0,0));

    // Names for syntax highlighting styles
    public static final InformSyntax Attribute =
        new InformSyntax("attribute", true, new Color(153,0,153), new Color(230,46,230));

    public static final InformSyntax Comment =
        new InformSyntax("comment", true, true, new Color(153,153,153), new Color(153,153,153));

    public static final InformSyntax Keyword =
        new InformSyntax("keyword", true, new Color(29,59,150), new Color(102,141,255));

    public static final InformSyntax Normal =
        new InformSyntax("normal", new Color(0,0,0), new Color(255,255,255));

    public static final InformSyntax Number =
        new InformSyntax("number", new Color(153,0,0), new Color(255,102,102));

    public static final InformSyntax Property =
        new InformSyntax("property", true, new Color(37,158,33), new Color(46,230,68));

    public static final InformSyntax String =
        new InformSyntax("string", new Color(153,102,0), new Color(255,221,51));

    public static final InformSyntax Verb =
        new InformSyntax("verb", true, new Color(0,153,153), new Color(46,230,216));

    public static final InformSyntax White =
        new InformSyntax("white", new Color(255,0,0), new Color(255,0,0));

    public static final InformSyntax Word =
        new InformSyntax("word", true, new Color(153,102,0), new Color(255,221,51));
    
    // Names for selection highlighting styles
    public static final InformSyntax Bookmarks = 
        new InformSyntax("bookmarks", new Color(51,100,255), new Color(29,59,150));

    public static final InformSyntax Brackets = 
        new InformSyntax("brackets", new Color(255,153,50), new Color(255,153,50));

    public static final InformSyntax Errors = 
        new InformSyntax("errors", new Color(255,102,102), new Color(255,102,102));

    public static final InformSyntax JumpTo = 
        new InformSyntax("jumopto", new Color(102,153,255), new Color(102,153,255));

    public static final InformSyntax Warnings = 
        new InformSyntax("warnings", new Color(102,153,255), new Color(102,153,255));

    // Background style
    public static final InformSyntax[] backgrounds = {
        Background
    };

    // Syntax highlighting styles
    public static final InformSyntax[] styles = {
        Attribute,
        Comment,
        Keyword,
        Normal,
        Number,
        Property,
        String,
        Verb,
        White,
        Word
    };
    
    // Selection highlighting styles
    public static final InformSyntax[] selections = {
        Bookmarks,
        Brackets,
        Errors,
        JumpTo,
        Warnings
    };
}