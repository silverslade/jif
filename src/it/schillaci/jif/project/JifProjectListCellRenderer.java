package it.schillaci.jif.project;

/*
 * JifProjectListCellRenderer.java
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

import it.schillaci.jif.core.JifFileName;
import it.schillaci.jif.gui.jFrame;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.UIManager;

/**
 * List cell renderer to display Jif files in a Jif project list
 * This displays the file using <code>getName()</code> rather than 
 * <code>toString()</code>.
 * 
 * @author Peter Piggott
 * @version 2.0
 * @since JIF 3.2
 */
public class JifProjectListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 6230808318112643998L;
    // Back regerence to Jif user interface
    private static jFrame view;
    // Default list bullet icon
    private static Icon defaultIcon;
    // List bullet icon for a closed project file
    private static Icon fileClosedIcon;
    // List bullet icon for an open project file
    private static Icon fileOpenIcon;
    // List bullet icon for a closed project main file
    private static Icon mainClosedIcon;
    // List bullet icon for an open project main file
    private static Icon mainOpenIcon;

    public JifProjectListCellRenderer(jFrame view) {
        super();
        this.view = view;
        defaultIcon = new ImageIcon(getClass().getResource("/images/TREE_objects.png"));
        fileClosedIcon = new ImageIcon(getClass().getResource("/images/LIST_file_closed.png"));
        fileOpenIcon = new ImageIcon(getClass().getResource("/images/LIST_file_open.png"));
        mainClosedIcon = new ImageIcon(getClass().getResource("/images/LIST_main_closed.png"));
        mainOpenIcon = new ImageIcon(getClass().getResource("/images/LIST_main_open.png"));
    }

    // --- DefaultListCellRenderer methods -------------------------------------
    /**
     * Overridden to use
     * <code>getName()</code> rather than
     * <code>toString()</code>
     */
    @Override
    public Component getListCellRendererComponent(JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        setComponentOrientation(list.getComponentOrientation());

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        if (value instanceof JifFileName) {
            JifFileName fileName = (JifFileName) value;
            if (view.isOpen(fileName)) {
                if (view.isMain(fileName)) {
                    setIcon(mainOpenIcon);
                } else {
                    setIcon(fileOpenIcon);
                }
            } else {
                if (view.isMain(fileName)) {
                    setIcon(mainClosedIcon);
                } else {
                    setIcon(fileClosedIcon);
                }
            }
            setText((value == null) ? "" : fileName.getName());
        } else {
            setIcon(defaultIcon);
            setText((value == null) ? "" : value.toString());
        }

        setEnabled(list.isEnabled());
        setFont(list.getFont());
        setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

        return this;
    }
}