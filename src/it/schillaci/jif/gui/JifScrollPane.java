package it.schillaci.jif.gui;

/*
 * JifScrollPane.java
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

import it.schillaci.jif.core.JifFileName;
import java.awt.Font;
import javax.swing.JScrollPane; 

/**
 * Jif scroll pane with line number support
 * 
 * @author Alessandro Schillaci
 * @author Peter Piggott
 * @version 2.0
 * @since JIF 3.2
 */
public class JifScrollPane extends JScrollPane {

    private static final long serialVersionUID = -4897501931771671145L;
    private JifFileName file;
    
    // --- Constructors --------------------------------------------------------
    
    /**
     * Creates a new instance of JifScrollPane
     *
     * @param view
     *              The editor component to display in the scrollpane's viewport
     * @param file
     *              The path of the file being edited by the component
     */
    public JifScrollPane(JifTextPane view, JifFileName file) {
        super(view);
        this.setToolTipText(file.getPath());
        this.file = file;
    }
    // --- Methods -------------------------------------------------------------
    
    /**
     * Adds or removes line numbers from this component
     *
     * @param show
     *              if <code>true</code> display line numbers; otherwise,
     *              remove the line numbers
     */
    void setLineNumbers(boolean show) {
        
        if (getRowHeader() == null) {
            if (show) {
                setRowHeaderView(new LineNumber(getTextPane()));
            }
        } else {
            if (!show) {
                setRowHeader(null);
            }
        }
    }
    
    /**
     * Set the font used for any line numbers being displayed for this component
     *
     * @param font
     *              The font to use for the component line numbers
     */
    void setLineNumberFont(Font font) {
        
        if (getRowHeader() == null) {
            return;
        }
        
        if (getRowHeader().getView() == null) {
            return;
        }
        
        getRowHeader().getView().setFont(font);
    }

    // --- Accessor methods ----------------------------------------------------
    
    JifFileName getFileName() {
        return file;
    }
    
    void setFileName(JifFileName file) {
        this.file = file;
    }
    
    String getPath() {
        return file.getPath();
    }
    
    void setPath(String path) {
        this.file = new JifFileName(path);
    }

    // --- Helper methods ------------------------------------------------------
    
    JifTextPane getTextPane() {
        return (JifTextPane) getViewport().getComponent(0);
    }
    
}