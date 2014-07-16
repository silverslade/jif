package it.schillaci.jif.gui;

/*
 * LineNumber.java
 *
 * This projectFile is part of JIF.
 *
 * Jif is substantially an editor entirely written in java that allows the
 * projectFile management for the creation of text-adventures based on Graham
 * Nelson's Inform standard [a programming language for Interactive Fiction].
 * With Jif, it's possible to edit, compile and run a Text Adventure in
 * Inform format.
 *
 * Copyright (C) 2003-2013  Alessandro Schillaci
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;


public class LineNumber extends JComponent {

    /**
     *
     */
    private static final long serialVersionUID = 2905458151135695364L;
    private final static Color DEFAULT_BACKGROUND = new Color(220, 220, 220);
    private final static Color DEFAULT_FOREGROUND = Color.black;
    private final static Font DEFAULT_FONT = new Font("monospaced", Font.PLAIN, 12);
    //  LineNumber height (abends when I use MAX_VALUE)
    private final static int DEFAULT_HEIGHT = Integer.MAX_VALUE - 1000000;
    //  Set right/left margin
    private final static int MARGIN = 5;
    //  Variables for this LineNumber component
    private FontMetrics fontMetrics;
    private int lineHeight;
    private int currentDigits;
    //  Metrics of the component used in the constructor
    private JComponent component;
    private int componentFontHeight;
    private int componentFontAscent;

    /**
     * Convenience constructor for Text Components
     */
    public LineNumber(JComponent component) {
        if (component == null) {
            setFont(DEFAULT_FONT);
            this.component = this;
        } else {
            setFont(component.getFont());
            this.component = component;
        }

        setBackground(DEFAULT_BACKGROUND);
        setForeground(DEFAULT_FOREGROUND);
        setPreferredWidth(99);
    }

    /**
     * Calculate the width needed to display the maximum line number
     */
    public void setPreferredWidth(int lines) {
        int digits = String.valueOf(lines).length();

        //  Update sizes when number of digits in the line number changes

        if (digits != currentDigits && digits > 1) {
            currentDigits = digits;
            int width = fontMetrics.charWidth('0') * digits;
            Dimension d = getPreferredSize();
            d.setSize(2 * MARGIN + width, DEFAULT_HEIGHT);
            setPreferredSize(d);
            setSize(d);
        }
    }

    /**
     * Reset variables that are dependent on the font.
     */
    public void setFont(Font font) {
        super.setFont(font);
        fontMetrics = getFontMetrics(getFont());
        componentFontHeight = fontMetrics.getHeight();
        componentFontAscent = fontMetrics.getAscent();
    }

    /**
     * The line height defaults to the line height of the font for this
     * component.
     */
    public int getLineHeight() {
        if (lineHeight == 0) {
            return componentFontHeight;
        } else {
            return lineHeight;
        }
    }

    /**
     * Override the default line height with a positive value. For example, when
     * you want line numbers for a JTable you could use the JTable row height.
     */
    public void setLineHeight(int lineHeight) {
        if (lineHeight > 0) {
            this.lineHeight = lineHeight;
        }
    }

    public int getStartOffset() {
        return component.getInsets().top + componentFontAscent;
    }

    @Override
    public void paintComponent(Graphics g) {
        int lineHeight = getLineHeight();
        int startOffset = getStartOffset();
        Rectangle drawHere = g.getClipBounds();

        // Paint the background

        g.setColor(getBackground());
        g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

        //  Determine the number of lines to draw in the foreground.

        g.setColor(getForeground());
        int startLineNumber = (drawHere.y / lineHeight) + 1;
        int endLineNumber = startLineNumber + (drawHere.height / lineHeight);

        int start = (drawHere.y / lineHeight) * lineHeight + startOffset;

        for (int i = startLineNumber; i <= endLineNumber; i++) {
            String lineNumber = String.valueOf(i);
            int stringWidth = fontMetrics.stringWidth(lineNumber);
            int rowWidth = getSize().width;
            g.drawString(lineNumber, rowWidth - stringWidth - MARGIN, start);
            start += lineHeight;
        }

        int rows = component.getSize().height / componentFontHeight;
        setPreferredWidth(rows);
    }
}