package it.schillaci.jif.gui;

/*
 * JifNoWrapTextPane.java
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
import it.schillaci.jif.inform.InformContext;
import java.awt.Dimension;
import java.io.File;

/**
 * Version of JifTextPane with text wrapping
 * 
 * @author Alessandro Schillaci
 * @author Peter Piggott
 * @version 1.0
 * @since JIF 3.2
 */
public class JifNoWrapTextPane extends JifTextPane {

    private static final long serialVersionUID = -7406567099225980317L;

    public JifNoWrapTextPane(jFrame parent, JifFileName fileName, File file, InformContext context) {
        super(parent, fileName, file, context);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        if (getSize().width < getParent().getSize().width) {
            return true;
        }
        return false;
    }

    @Override
    public void setSize(Dimension d) {
        if (d.width < getParent().getSize().width) {
            d.width = getParent().getSize().width;
        }
        super.setSize(d);
    }

}