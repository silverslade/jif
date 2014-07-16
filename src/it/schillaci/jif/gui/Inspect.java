package it.schillaci.jif.gui;

/*
 * Inspect.java
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
 * A representation of inform source code symbols in the Object Tree
 * 
 * @author Alessandro Schillaci
 * @author Peter Piggott
 * @version 2.0
 */
public class Inspect implements Comparable {

    // The label to display for a symbol in the object tree
    private String label;
    
    // Absolute path of the file containing the symbol
    private String path;
    
    // Initial position of the symbol in the file
    private int position;

    // --- Constructors --------------------------------------------------------
    
    /**
     * Constructor to create a new Inspect from a label and its location in a
     * file expressed as a file path and offset position within the file.
     * 
     * @param path
     *              The absolute path of the inform document file which
     *              contains the symbol
     * @param label
     *              The label to use for the symbol
     * @param position
     *              The offset position of keyword in the inform document
     */
    public Inspect(String label, String path, int position) {
        this.label = label;
        this.path = path;
        this.position = position;
    }
    
    /**
     * Constructor to create a new Inspect from a symbol and its location in
     * a file expressed as a offset position.
     *
     * @param label
     *              The label to use for the symbol
     * @param position
     *              The position of the symbol in the inform document
     */
    public Inspect(String label, int position) {
        this(label, null, position);
    }
    
    /**
     * Constructor to create a new Inspect from just a symbol.
     *
     * @param label
     *              The label to use for the symbol
     */
    public Inspect(String label) {
        this(label, null, -1);
    }
    
    // --- Accessor methods ----------------------------------------------------
    
    public String getLabel() {
        return label;
    }
    
    public String getPath() {
        return path;
    }
    
    public int getPosition() {
        return this.position;
    }

    // --- Object methods ------------------------------------------------------
    
    @Override
    public String toString() {
        return label;
    }
    
    // --- Comparable implementation -------------------------------------------
    
    @Override
    public int compareTo(Object o) {
        Inspect i = (Inspect) o;
        
        // Compare symbols
        int compare = label.compareTo(i.getLabel());
        
        if (compare != 0) {
            return compare;
        }
        
        // Names are equal, compare file paths
        compare = path.compareTo(i.getPath());
        
        if (compare != 0) {
            return compare;
        }
        
        // File paths are equal compare positions
        return position - i.getPosition();
    }

}