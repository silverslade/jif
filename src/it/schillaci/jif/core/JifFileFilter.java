package it.schillaci.jif.core;

/*
 * JifFileFilter.java
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


import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.filechooser.FileFilter;



/**
 * Class for the file filter
 */
public class JifFileFilter extends FileFilter {

    private Hashtable filters = null;
    private String description = null;
    private String fullDescription = null;
    private boolean useExtensionsInDescription = true;

    /**
     * Creates a new JifFileFilter for JIF files
     */
    public JifFileFilter() {
        this.filters = new Hashtable();
    }
    /**
     * Creates a new JifFileFilter for JIF
     * @param extension File extension (i.e. ".inf")
     * @param description File description
     */
    public JifFileFilter(String extension, String description) {
        this();
        if(extension!=null) {
            addExtension(extension);
        }
        if(description!=null) {
            setDescription(description);
        }
    }

    /**
     * Accepts only files with some extension
     * @param f The file to open
     * @return If the file is openable
     */
    @Override
    public boolean accept(File f) {
        if (f != null) {
            if (f.isDirectory()) {
                return true;
            }
            String extension = getExtension(f);
            if (extension != null && filters.get(getExtension(f)) != null) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns the file extension
     * @return The extension
     * @param f The file to open
     */
    private String getExtension(File f) {
        if(f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if(i>0 && i<filename.length()-1) {
                return filename.substring(i+1);
            }
        }
        return null;
    }


    /**
     * Add File extension to the file filter
     * @param extension The extension
     */
    public void addExtension(String extension) {
        if(filters == null) {
            filters = new Hashtable(5);
        }
        filters.put(extension.toLowerCase(), this);
        fullDescription = null;
    }

    /**
     * Returns file description
     * @return the description
     */
    @Override
    public String getDescription() {
        if(fullDescription == null) {
            if(description == null || isExtensionListInDescription()) {
            fullDescription = description==null ? "(" : description + " (";
            // build the description from the extension list
            Enumeration extensions = filters.keys();
            if(extensions != null) {
                fullDescription += "." + (String) extensions.nextElement();
                while (extensions.hasMoreElements()) {
                fullDescription += ", " + (String) extensions.nextElement();
                }
            }
            fullDescription += ")";
            } else {
            fullDescription = description;
            }
        }
        return fullDescription;
    }

    /**
     * Set the file description
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
        fullDescription = null;
    }

    /**
     * Set extension list
     * @param b boolean value
     */
    public void setExtensionListInDescription(boolean b) {
        useExtensionsInDescription = b;
        fullDescription = null;
    }

    /**
     * Check the extension in the list
     * @return The boolean value
     */
    public boolean isExtensionListInDescription() {
        return useExtensionsInDescription;
    }
}