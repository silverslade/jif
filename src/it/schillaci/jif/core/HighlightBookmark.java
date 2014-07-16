package it.schillaci.jif.core;

/*
 * HighlightBookmark.java
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

import java.awt.Color;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;


/**
 * Class for Highlighting text in a JTextComponent
 * @author Alessandro Schillaci
 * @version 1.0
 */
public class HighlightBookmark extends DefaultHighlighter.DefaultHighlightPainter {

    JTextComponent jif;

    /**
     * Creates an Highlighter Object for a JTextComponent
     *
     * @param jif The instance of JTextComponent to apply the highlighting
     * @param color Color of highlighting
     */
    public HighlightBookmark(JTextComponent jif, Color color) {
        super(color);
        this.jif = jif;
    }

    /**
     * Creates an Highlighter Object for a generic JTextComponent
     *
     * @param color Color of highlighting
     */
    public HighlightBookmark(Color color) {
        super(color);
        this.jif = null;
    }

    /**
     * Highlight a string in a JifTextComponent
     *
     * @param component Instance of JTextComponent
     * @param pattern String to be highlighted
     */
    public void highlight(JTextComponent component, String pattern) {
        try {
            Highlighter hilite = component.getHighlighter();
            String text = jif.getText();
            int pos = 0;
            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                hilite.addHighlight(pos, pos + pattern.length(), this);
                pos += pattern.length();
            }
        } catch (BadLocationException e) {
            System.out.println(e.getMessage());
            System.err.println(e.getMessage());
        }
    }

    // si puo' applicare a qlc JTextComponent
    /**
     * Highlight a string in a JTextComponent
     *
     * @param component Instance of JTextComponent
     * @param start Start position to be highlighted
     * @param end End position to be highlighted
     */
    public void highlightFromTo(JTextComponent component, int start, int end) {
        try {
            Highlighter hilite = component.getHighlighter();
            hilite.addHighlight(start, end, this);
        } catch (BadLocationException e) {
            System.out.println(e.getMessage());
            System.err.println(e.getMessage());
        }
    }

    /**
     * Remove all the Highlights from a JTextComponent
     *
     * @param component Instance of JTextComponent
     */
    public void removeHighlights(JTextComponent component) {
        Highlighter hilite = component.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();
        for (int i = 0; i < hilites.length; i++) {
            if (hilites[i].getPainter() instanceof HighlightBookmark) {
                hilite.removeHighlight(hilites[i]);
            }
        }
    }
}