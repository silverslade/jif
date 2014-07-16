package it.schillaci.jif.gui;

/*
 * PopupListener.java
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


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 * Pop up listener class
 * @author Alessandro Schillaci
 * @author Peter Piggott
 * @version 2.0
 */
public  class PopupListener extends MouseAdapter {
    JPopupMenu jpopupmenu;
    JifTextPane jif;
    jFrame jframe;

    /**
     * Creates a new PopupListener for right mouse click menu
     * 
     * 
     * 
     * @param jif
     *          The instance ofJifTextPanee
     * @param jframe
     *          The Instance of Main jFrame
     */
    public PopupListener(jFrame jframe, JifTextPane jif) {
        this.jpopupmenu = jframe.filePopupMenu;
        this.jframe = jframe;
        this.jif = jif;
    }


    /**
     * Mouse pressed event
     *
     * @param e 
     *          mouse event
     */
    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
        // Removing all the eventual highlights
        jif.removeHighlighter();
        jif.removeHighlighterBrackets();
        
        // hiding JWindowSymbols
        if (jframe.isSymbolsVisible()) {
            jframe.symbolsHide();
        }

        // if the JtextAreaOutput is hidden
        if (!jframe.isOutputVisible()) {
            jframe.outputHide();
        }
    }

    /**
     * mouse released event
     * @param e mouse event
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            jpopupmenu.show(
                    e.getComponent(),
                    e.getX(),
                    e.getY());
        }
    }
}