package it.schillaci.jif.gui;

/*
 * InformTreeNode.java
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

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Node for the Inform code tree.
 * 
 * @author  Peter Piggott
 * @version 2.0
 * @since   JIF 3.2
 */
public class InformTreeNode extends DefaultMutableTreeNode
        implements Comparable {

    /**
     * Creates an Inform tree node with no parent, no children, initialised with
     * the specified user object, and that allows children only if specified.
     *
     * @param userObject an Object provided by the user that constitutes the
     * node's data
     * @param allowsChildren if <code>true</code>, the node is allowed to have
     * child nodes -- otherwise, it is always a leaf node
     */
    public InformTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    /**
     * Creates an Inform tree node with no parent, no children, but which allows
     * children, and initialises it with the specified user object.
     *
     * @param userObject an Object provided by the user that constitutes the
     * node's data
     */
    public InformTreeNode(Object userObject) {
        super(userObject);
    }

    /**
     * Creates an Inform tree node with no parent, no children, but which allows
     * children.
     */
    public InformTreeNode() {
        super();
    }

    /**
     * Recursive search of this tree node's children in depth first order for a
     * child with a user object with the given label.
     *
     * @param label The label to find
     * @return The label location
     */
    public Inspect search(String label) {
        for (Enumeration e = depthFirstEnumeration(); e.hasMoreElements();) {
            Object o = ((InformTreeNode) e.nextElement()).getUserObject();
            if (o instanceof Inspect) {
                Inspect ins = (Inspect) o;
                if (ins.getLabel().equalsIgnoreCase(label)
                        || ins.getLabel().equalsIgnoreCase(label + "sub")) {
                    return ins;
                }
            }
        }
        return new Inspect(label);
    }

    /**
     * Replace all of this Inform tree node's children in alphabetical order
     * based on a list of label locations.
     *
     * @param list The new children (label locations) for the node
     */
    public void replaceChildren(List list) {
        Collections.sort(list);
        removeAllChildren();
        for (int i = 0, size = list.size(); i < size; i++) {
            add(new InformTreeNode((Inspect) list.get(i)));
        }
    }

    /**
     * Recursive sort of this Inform tree node's children in alphabetical order.
     */
    public void sort() {
        if (children == null) {
            return;
        }
        Collections.sort(children);

        for (int i = 0; i < children.size(); i++) {
            Object child = children.get(i);
            if (child instanceof InformTreeNode) {
                ((InformTreeNode) child).sort();
            }
        }
    }

    /**
     * Insert new child in alphabetical order.
     *
     * @param newChild The InformTreeNode to insert under this node
     * @exception    ArrayIndexOutOfBoundsException    if <code>childIndex</code> is
     * out of bounds
     * @exception    IllegalArgumentException    if <code>newChild</code> is null or
     * is an ancestor of this node
     * @exception    IllegalStateException    if this node does not allow children
     */
    public void insert(InformTreeNode newChild) {
        if (!allowsChildren) {
            throw new IllegalStateException("node does not allow children");
        } else if (newChild == null) {
            throw new IllegalArgumentException("new child is null");
        } else if (isNodeAncestor(newChild)) {
            throw new IllegalArgumentException("new child is an ancestor");
        }

        int index = (children == null)
                ? -1
                : Collections.binarySearch(children, newChild);

        if (index < 0) {
            insert(newChild, -index - 1);
        }
    }

    // --- Comparable implementation -------------------------------------------
    @Override
    public int compareTo(Object o) {
        InformTreeNode node = (InformTreeNode) o;

        // Compare node userObjects
        return toString().compareTo(node.toString());
    }
}