package it.schillaci.jif.inform;

/*
 * InformContext.java
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

import java.awt.Color;
import java.awt.Font;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 * Style context class for syntax highlighting an <code>InformDocument</code>
 * 
 * @author Peter Piggott
 * @version 1.0
 * @since JIF 3.2
 */
public class InformContext extends StyleContext {

    private static final long serialVersionUID = -2558885962805711954L;

    // Default syntax highlighting font
    public static final String defaultFontName = "Courier New";
    public static final int defaultFontSize = 12;
    
    // Default tab size
    public static final int defaultTabSize = 4;

    /**
     * Constructs an <code>InformContext</code> with a set of syntax
     * highlighting styles for Inform source code.
     * <p>
     * <b>Note</b> The style hierarchy for the syntax highlighting is three
     * tier. The base is the default style which holds font information common
     * to all the styles. The middle tier holds colour and style information for
     * each syntax style. The final tier consists of the syntax highlighting
     * styles that can be used to apply syntax highlighting to an
     * <code>InformDocument</code>.
     * <p>
     * This means that no colour, font or style attributes are directly attached
     * to elements within a document using these third tier styles. Instead
     * these attributes are all dynamically looked up via the style hierarchy
     * when an element is displayed by a view. Hence updates to the colour, font
     * or style attributes are reflected immediately without need to update any
     * attributes previously applied to elements in a document.
     */
    public InformContext() {
        super();
        
        // Create default style as base of style hierarchy
        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setBackground(defaultStyle, InformSyntax.Background.getLightColor());
        StyleConstants.setFontFamily(defaultStyle, defaultFontName);
        StyleConstants.setFontSize(defaultStyle, defaultFontSize);

        // Syntax highlighting styles
        for (int i=0; i <InformSyntax.styles.length; i++) {
            Style tier2 = addStyle(InformSyntax.styles[i].getNameX(), defaultStyle);
            StyleConstants.setBold(tier2, InformSyntax.styles[i].isBold());
            StyleConstants.setItalic(tier2,InformSyntax.styles[i].isItalic());
            StyleConstants.setForeground(tier2, InformSyntax.styles[i].getLightColor());
            Style tier3 = addStyle(InformSyntax.styles[i].getName(), tier2);
        }

        // Selection highlighting styles
        for (int i=0; i <InformSyntax.selections.length; i++) {
            Style tier2 = addStyle(InformSyntax.selections[i].getNameX(), defaultStyle);
            StyleConstants.setBold(tier2, InformSyntax.selections[i].isBold());
            StyleConstants.setItalic(tier2,InformSyntax.selections[i].isItalic());
            StyleConstants.setForeground(tier2, InformSyntax.selections[i].getLightColor());
            Style tier3 = addStyle(InformSyntax.selections[i].getName(), tier2);
        }
    }
    
    public void replaceStyles(InformContext styles) {
        
        // Replace font
        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(defaultStyle, styles.getFontName());
        StyleConstants.setFontSize(defaultStyle, styles.getFontSize());
        
        // Replace background
        setBackground(styles.getBackground());

        // Replace syntax highlighting colours
        for (int i=0; i <InformSyntax.styles.length; i++) {
            Color color = styles.getForeground(InformSyntax.styles[i]);
            setForeground(InformSyntax.styles[i], color);
        }

        // Replace selection highlighting colours
        for (int i=0; i <InformSyntax.selections.length; i++) {
            Color color = styles.getForeground(InformSyntax.selections[i]);
            setForeground(InformSyntax.selections[i], color);
        }        
    }
    
    /**
     * Changes the syntax highlighting styles to the default colors for a dark
     * background.
     */
    public void defaultDarkColors() {

        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setBackground(defaultStyle, InformSyntax.Background.getDarkColor());

        for (int i=0; i<InformSyntax.styles.length; i++){
            Style tier2 = getStyle(InformSyntax.styles[i].getNameX());
            StyleConstants.setForeground(tier2, InformSyntax.styles[i].getDarkColor());
        }
    }

    /**
     * Changes the selection styles to the default colors for a dark background.
     */
    public void defaultDarkSelections() {

        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setBackground(defaultStyle, InformSyntax.Background.getDarkColor());

        for (int i=0; i<InformSyntax.selections.length; i++){
            Style tier2 = getStyle(InformSyntax.selections[i].getNameX());
            StyleConstants.setForeground(tier2, InformSyntax.selections[i].getDarkColor());
        }
    }

    /**
     * Changes the syntax highlighting styles to the default colors for a light
     * background.
     */
    public void defaultLightColors() {

        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setBackground(defaultStyle, InformSyntax.Background.getLightColor());

        for (int i=0; i<InformSyntax.styles.length; i++){
            Style tier2 = getStyle(InformSyntax.styles[i].getNameX());
            StyleConstants.setForeground(tier2, InformSyntax.styles[i].getLightColor());
        }
    }
    
    /**
     * Changes the selection highlighting styles to the default colors for a 
     * light background.
     */
    public void defaultLightSelections() {

        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setBackground(defaultStyle, InformSyntax.Background.getLightColor());

        for (int i=0; i<InformSyntax.selections.length; i++){
            Style tier2 = getStyle(InformSyntax.selections[i].getNameX());
            StyleConstants.setForeground(tier2, InformSyntax.selections[i].getLightColor());
        }
    }
    
    /**
     * Changes the syntax highlighting styles to the default font.
     */
    public void defaultFont() {
        
        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(defaultStyle, defaultFontName);
        StyleConstants.setFontSize(defaultStyle, defaultFontSize);
        
    }
    
    // --- Accessor methods ----------------------------------------------------
    
    /**
     * Fetches the default background color used for syntax highlighting
     * 
     * @return default background color for syntax highlighting
     */
    public Color getBackground() {
        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        return StyleConstants.getBackground(defaultStyle);
    }
    
    /**
     * Gets the RBB string representation of the default background color
     * used for syntax highlighting
     * 
     * @return RGB string of default background color for syntax highlighting
     */
    public String getBackgroundRGB() {
        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        Color color = StyleConstants.getBackground(defaultStyle);
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }
    
    /**
     * Sets the background color used for all syntax highlighting
     * 
     * @param color
     *            the common background color for syntax highlighting
     */
    public void setBackground(Color color) {
        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setBackground(defaultStyle, color);
    }
    
    /**
     * Gets the font used for a syntax highlighted Inform document.
     * 
     * @return font
     */
    public Font getFont() {
        Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        String syntaxFontFamily = StyleConstants.getFontFamily(defaultStyle);
        int syntaxFontSize = StyleConstants.getFontSize(defaultStyle);
        return new Font(syntaxFontFamily, Font.PLAIN, syntaxFontSize);
    }
    
    /**
     * Gets the name style size string representation of the font used for 
     * syntax highlighting
     *
     * @return name, style, size string of font 
     */
    public String getFontNameStyleSize() {
        Font font = getFont();
        return font.getName() + "," + font.getStyle() + "," + font.getSize();
    }

    /**
     * Sets the font used for a syntax highlighted Inform document.
     * 
     * @param font
     *            the Font for Inform documents
     */
    public void setFont(Font font) {
        Style syntaxStyle = getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(syntaxStyle, font.getName());
        StyleConstants.setFontSize(syntaxStyle, font.getSize());
    }

    /**
     * Gets the font name used for a syntax highlighted Inform document.
     * 
     * @return font family name
     */
    public String getFontName() {
        Style syntaxStyle = getStyle(StyleContext.DEFAULT_STYLE);
        return StyleConstants.getFontFamily(syntaxStyle);
    }

    /**
     * Sets the font name used for a syntax highlighted Inform document.
     * 
     * @param name
     *            the font family name for Inform documents
     */
    public void setFontName(String name) {
        Style syntaxStyle = getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(syntaxStyle, name);
    }

    /**
     * Gets the font size used for a syntax highlighted Inform document.
     * 
     * @return font size
     */
    public int getFontSize() {
        Style syntaxStyle = getStyle(StyleContext.DEFAULT_STYLE);
        return StyleConstants.getFontSize(syntaxStyle);
    }

    /**
     * Sets the font size used for a syntax highlighted Inform document.
     * 
     * @param size
     *            the font size for Inform documents
     */
    public void setFontSize(int size) {
        Style syntaxStyle = getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontSize(syntaxStyle, size);
    }

    /**
     * Gets the foreground color used for syntax highlighting the specified
     * syntax highlighting style.
     * 
     * @param syntax
     *            the name for a syntax highlight style
     * @return foreground color of the syntax highlighting style
     */
    public Color getForeground(InformSyntax syntax) {
        Style syntaxStyle = getStyle(syntax.getNameX());
        return StyleConstants.getForeground(syntaxStyle);
    }

    /**
     * Gets the RBB string representation of the foreground color used for
     * syntax highlighting the specified syntax highlighting style.
     * 
     * @param syntax
     *            the name for a syntax highlight style
     * @return RGB string of foreground color of the syntax highlighting style
     */
    public String getForegroundRGB(InformSyntax syntax) {
        Style syntaxStyle = getStyle(syntax.getNameX());
        Color color = StyleConstants.getForeground(syntaxStyle);
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    /**
     * Sets the foreground color used for syntax highlighting the specified
     * sytax highlighting style.
     * 
     * @param syntax
     *            the name of a syntax highlight style
     * @param color
     *            foreground color for syntax highlight style
     */
    public void setForeground(InformSyntax syntax, Color color) {
        Style syntaxStyle = getStyle(syntax.getNameX());
        StyleConstants.setForeground(syntaxStyle, color);
    }

    /**
     * Gets the background color used for syntax highlighting the specified
     * syntax highlighting style.
     * 
     * @param syntax
     *            the name of a syntax highlight style
     * @return foreground color of the syntax highlighting style
     */
    public Color getBackground(InformSyntax syntax) {
        Style syntaxStyle = getStyle(syntax.getNameX());
        return StyleConstants.getBackground(syntaxStyle);
    }

    /**
     * Sets the background color used for syntax highlighting the specified
     * syntax highlighting style.
     * 
     * @param syntax
     *            the name for a syntax highlight style
     * @param color
     *            background color for syntax highlighting style
     */
    public void setBackground(InformSyntax syntax, Color color) {
        Style syntaxStyle = getStyle(syntax.getNameX());
        StyleConstants.setBackground(syntaxStyle, color);
    }
}