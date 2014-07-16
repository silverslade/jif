package it.schillaci.jif.gui;

/*
 * JifTreeCellRenderer.java
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

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Tree cell renderer to display Inform source code artifacts in a tree. This
 * displays the file using
 * <code>getName()</code> rather than
 * <code>toString()</code>. It uses different icons for code artifacts in the
 * current open file, unselected or closed files and for class place holders
 * which have no identified file.
 *
 * @author Peter Piggott
 * @version 1.0
 * @since JIF 3.6
 */
public class JifTreeCellRenderer extends DefaultTreeCellRenderer {

    // Back reference to Jif user interface
    private static jFrame view;
    // Icon for leaf nodes when their location is not the current file
    private static Icon fileClosedIcon;
    // Icon for leaf nodes when their location is the current file
    private static Icon fileOpenIcon;
    // Icon for leaf nodes when their is no known file location
    private static Icon fileNoneIcon;

    /**
     * Creates a new instance of JifTreeCellRenderer
     */
    public JifTreeCellRenderer(jFrame view) {
        super();
        this.view = view;
        fileClosedIcon = new ImageIcon(getClass().getResource("/images/TREE_file_closed.png"));
        fileOpenIcon = new ImageIcon(getClass().getResource("/images/TREE_file_open.png"));
        fileNoneIcon = new ImageIcon(getClass().getResource("/images/TREE_file_none.png"));
    }

    /**
     * Configures the renderer based on the passed in components. The value is
     * set from messaging the tree with
     * <code>convertValueToText</code>, which ultimately invokes
     * <code>toString</code> on
     * <code>value</code>. The foreground color is set based on the selection
     * and the icon is set based on on leaf, expanded and whether the source is
     * located in the current file.
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        String stringValue = tree.convertValueToText(
                value, sel, expanded, leaf, row, hasFocus);

        setText(stringValue);

        if (sel) {
            setForeground(getTextSelectionColor());
        } else {
            setForeground(getTextNonSelectionColor());
        }

        InformTreeNode node = (InformTreeNode) value;
        Object o = node.getUserObject();
        if (!tree.isEnabled()) {
            setEnabled(false);
            setDisabledIcon(selectDisabledIcon(leaf, expanded));
        } else {
            setEnabled(true);
            setIcon(selectEnabledIcon(leaf, o, expanded));
        }

        setComponentOrientation(tree.getComponentOrientation());

        selected = sel;

        return this;
    }

    private Icon selectDisabledIcon(final boolean leaf, final boolean expanded) {
        if (leaf) {
            return getFileClosedIcon();
        } else {
            if (expanded) {
                return getOpenIcon();
            } else {
                return getClosedIcon();
            }
        }
    }

    private Icon selectEnabledIcon(final boolean leaf, final Object o, final boolean expanded) {

        if (o instanceof Inspect) {
            Inspect location = (Inspect) o;
            return selectAssetIcon(location);
        }

        if (o instanceof String) {
            String name = (String) o;
            if (name.endsWith("  ")) {
                // Constant, Global etc. heading
                return selectHeadingIcon();
            } else {
                // Top
                return selectTopIcon(expanded);
            }
        }
        return getFileClosedIcon();
    }

    private Icon selectAssetIcon(final Inspect location) {
        if (location.getPath() == null) {
            return getFileNoneIcon();
        } else {
            if (view.isSelected(location.getPath())) {
                return getFileOpenIcon();
            } else {
                return getFileClosedIcon();
            }
        }
    }

    private Icon selectTopIcon(boolean expanded) {
        if (expanded) {
            return getOpenIcon();
        } else {
            return getClosedIcon();
        }
    }

    private Icon selectHeadingIcon() {
        return getFileClosedIcon();
    }

    // --- Accessor methods ----------------------------------------------------
    /**
     * Returns the icon used to represent leaf nodes when their location is not
     * known.
     */
    public Icon getFileNoneIcon() {
        return fileNoneIcon;
    }

    /**
     * Sets the icon used to represent leaf nodes when their location is not
     * known.
     */
    public void setFileNoneIcon(Icon fileNoneIcon) {
        this.fileNoneIcon = fileNoneIcon;
    }

    /**
     * Returns the icon used to represent leaf nodes when their location is the
     * current file.
     */
    public Icon getFileOpenIcon() {
        return fileOpenIcon;
    }

    /**
     * Sets the icon used to represent leaf nodes when their location is the
     * current file.
     */
    public void setFileOpenIcon(Icon fileOpenIcon) {
        this.fileOpenIcon = fileOpenIcon;
    }

    /**
     * Returns the icon used to represent leaf nodes when their location is not
     * the current file.
     */
    public Icon getFileClosedIcon() {
        return fileClosedIcon;
    }

    /**
     * Sets the icon used to represent leaf nodes when their location is not the
     * current file.
     */
    public void setFileClosedIcon(Icon fileClosedIcon) {
        this.fileClosedIcon = fileClosedIcon;
    }
}