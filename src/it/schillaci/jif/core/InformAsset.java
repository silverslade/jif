package it.schillaci.jif.core;

/*
 * InformAsset.java
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
 * WeB   : http://www.slade.altervista.org/JIF/
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

import it.schillaci.jif.gui.Inspect;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Information about a class found in some inform source code
 *
 * @author Peter Piggott
 * @version 1.0
 * @since Jif 3.6
 */
public class InformAsset {

    // location of source definition
    private Inspect location      = null;
    // parent class
    private String parent         = null;
    // can have children
    private boolean allowChildren = true;
    // classes and objects with this class as a parent
    private TreeSet children      = new TreeSet(); 
    
    /**
     * Constructor to create a new InformAsset from a code name and the
     * location of its definition in a file of Inform source code expressed as 
     * a file path and offset within the file.
     *
     * @param path
     *              The path of the inform source file containing the asset
     * @param name
     *              The code name of the asset
     * @param position
     *              The position of the asset in the inform source file
     */
    public InformAsset(String path, String name, int position) {
        location = new Inspect(name, path, position);
    }
    
    public boolean hasChildren() {
        return !children.isEmpty();
    }
    
    public boolean hasParent() {
        return parent != null;
    }
    
    public boolean isInformObject() {
        return !allowChildren; 
    }
    
    // --- Accessor methods ----------------------------------------------------
    
    public boolean getAllowChildren() {
        return allowChildren;
    }
    
    public void setAllowChildren(boolean allowChldren) {
        this.allowChildren = allowChildren;
    }
    
    // ---
    
    public void addChild(String childName) {
        children.add(childName);
    }
    
    public Set getChildren() {
        return Collections.unmodifiableSet(children);
    }
    
    public Iterator iterator() {
        return getChildren().iterator();
    }
    
    public void setChildren(TreeSet children) {
        this.children = children;
    }
    
    // ---
    
    public Inspect getLocation() {
        return location;
    }
    
    public void setLocation(Inspect location) {
        this.location = location;
    }
    
    // ---
    
    public String getName() {
        return location.getLabel();
    }
    
    // ---
    
    public String getParent() {
        return parent;
    }
    
    public void setParent(String parent) {
        this.parent = parent;
    }
    
    // ---
    
    public String getPath() {
        return location.getPath();
    }
   
    // ---
    
    public int getPosition() {
        return location.getPosition();
    }
    
    // --- object methods ------------------------------------------------------
    
    @Override
    public String toString() {
        return location.toString() + "^"+parent+":" + children.toString();
    }
    
}
