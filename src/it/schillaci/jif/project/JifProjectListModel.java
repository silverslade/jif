package it.schillaci.jif.project;

/*
 * JifProjectListModel.java
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

import it.schillaci.jif.core.JifFileName;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.DefaultListModel;

/**
 * Extends <code>Default List Mode</code> to fire update events when notified 
 * of a status change to a file contained in the project list.
 * 
 * @author Peter Piggott
 * @version 1.0
 * @since Jif 3.3
 */
public class JifProjectListModel extends DefaultListModel {
    
    /**
     * Creates a new instance of JifProjectListModel
     */
    public JifProjectListModel() {
    }
    
    /**
     * Add a collection of project files to the project file list
     *
     * @param c
     *              The collection of project files to add to the list
     */
    public void addAll(Collection c) {
        for (Iterator i = c.iterator(); i.hasNext(); ) {
            JifFileName element = (JifFileName) i.next();
            addElement(element);
        }
    }

    /**
     * Replace the current project file list with a new project file list
     *
     * @param list
     *              The new project file list
     */
    public void replaceList(Vector list) {
        clear();
        addAll(list);
    }

    /**
     * Update the display for the specified file
     *
     * @param file
     *              The file whose status has changed
     */
    public void updateFile(JifFileName file) {
        if (contains(file)) {
            int index = indexOf(file);
            fireContentsChanged(this, index, index);
        }
    }
    
}
