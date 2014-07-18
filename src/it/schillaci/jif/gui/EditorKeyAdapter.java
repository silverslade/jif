package it.schillaci.jif.gui;

/*
 * EditorKeyAdapter.java
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
import it.schillaci.jif.core.Utils;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;

/**
 * EditorKeyAdapter for the JifTextPane
 * 
 * @author Alessandro Schillaci
 * @Peter Piggott
 * @version 2.0
 */
public class EditorKeyAdapter extends KeyAdapter {

    jFrame jframe;
    JifTextPane jif;
    Element el;
    String ultima;
    String ultima_word;
    Process proc;
    String comando;

    /**
     * Creates a new EditorKeyAdapter
     * 
     * @param parent The jframe main instance
     * @param jif Current JifTextPane
     */
    public EditorKeyAdapter(jFrame parent, JifTextPane jif){
        this.jframe = parent;
        this.jif = jif;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
    }

    /**
     * keyTyped method for the Mapping characters Management.
     * When you type a character which has to be "mapped" into
     * ZSCII code, this method rescue the correct character to replace.
     * Example:
     * If user digits "è" character, this will be tranformed into the ZSCII format
     * "@`e"
     * @param ke The key typed by the user
     */
    @Override
    public void keyTyped(KeyEvent ke) {
        if (jframe.isMapping("" + ke.getKeyChar())) {
            //TODO This fails if the comment starts after the insertion point
            // If the current row is a comment, skip
            if (jif.getCurrentRow().indexOf("!") == -1) {
                jif.replaceSelection(jframe.getMapping("" + ke.getKeyChar()));
                ke.consume();
            }
        }
    }
    /**
     *
     * @param ke The key released by the user
     */
    @Override
    public void keyReleased(KeyEvent ke) {
        try {

            // Keyboard Mapping ALT
            if (ke.isAltDown()) {
                if (jframe.isAltKey("" + ke.getKeyChar())) {
                    MutableAttributeSet attr = new SimpleAttributeSet();
                    jif.getDocument().insertString(
                            jif.getCaretPosition(),
                            jframe.getAltKey("" + ke.getKeyChar()),
                            attr);
                }

                // Commands to run
                if (jframe.isExecuteCommand("" + ke.getKeyChar())) {
                    Runtime.getRuntime().exec(jframe.getExecuteCommand("" + ke.getKeyChar()));
                }
            }


            // Automatic JUMP to object, if present in the object tree
            if (ke.getKeyCode() == KeyEvent.VK_J && ke.isControlDown()) {
                jframe.checkDefinition(jif.getCurrentWord());
            }


            // Automatic right shifting
            if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                Element e = jif.getElementAt(jif.getCaretPosition() - 1);
                String tmp;
                try {
                    String s = jif.getDocument().getText(e.getStartOffset(), e.getEndOffset() - e.getStartOffset());
                    int i;
                    int length = s.length();
                    for (i = 0; i < length; i++) {
                        if (!Character.isWhitespace(s.charAt(i))) {
                            break;
                        }
                    }
                    tmp = s.substring(0, i);
                    if (tmp.endsWith("\n")) {
                        tmp = tmp.substring(0, tmp.length() - 1);
                    }
                    jif.getDocument().insertString(jif.getCaretPosition(), tmp, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.err.println(ex.getMessage());
                }
            }

            // Ignored keys
            if ((ke.getKeyCode() == KeyEvent.VK_DOWN)
                    || (ke.getKeyCode() == KeyEvent.VK_UP)
                    || (ke.getKeyCode() == KeyEvent.VK_RIGHT)
                    || (ke.getKeyCode() == KeyEvent.VK_LEFT)
                    || (ke.getKeyCode() == KeyEvent.VK_SHIFT)
                    || (ke.getKeyCode() == KeyEvent.VK_END)
                    || (ke.getKeyCode() == KeyEvent.VK_HOME)) {

                jif.removeHighlighterBrackets();

                if (jif.getCaretPosition()==0) {
                    return;
                }
                
                // Search for open parenthesis
                if (jif.getText(jif.getCaretPosition() - 1, 1).equals("{")) {
                    jif.highlightCloseBracket("{", "}");
                }
                if (jif.getText(jif.getCaretPosition() - 1, 1).equals("[")) {
                    jif.highlightCloseBracket("[", "]");
                }
                if (jif.getText(jif.getCaretPosition() - 1, 1).equals("(")) {
                    jif.highlightCloseBracket("(", ")");
                }

                // Search for closed parenthesis
                if (jif.getText(jif.getCaretPosition() - 1, 1).equals("}")) {
                    jif.highlightOpenBracket("{", "}");
                }
                if (jif.getText(jif.getCaretPosition() - 1, 1).equals("]")) {
                    jif.highlightOpenBracket("[", "]");
                }
                if (jif.getText(jif.getCaretPosition() - 1, 1).equals(")")) {
                    jif.highlightOpenBracket("(", ")");
                }
                return;
            }

            // TODO This only works at the end of the line, needs to be changed
            // so it works anywhere in line (before space)
            // Assistant code
            if (jframe.isHelpCodeLive() && (ke.getKeyCode() == KeyEvent.VK_SPACE && ke.isControlDown())) {
                el = jif.getCaretElement();
                ultima = jif.getText(el.getStartOffset(), el.getEndOffset() - el.getStartOffset() - 1);
                int position = jif.getCaretPosition();

//                System.out.println("Element: " + el.getName() + " count: " + el.getElementCount());
//                System.out.println("Help Code [1]: [" + ultima + "]");
                
                if (ultima.indexOf(" ") != -1) {
                    ultima_word = ultima.substring(ultima.lastIndexOf(" ")).trim();
                } else {
                    //TODO : calcolo il numero di spazi o di tab prima della word
                    ultima_word = ultima;
                }
                ultima_word = ultima_word.trim();
//                System.out.println("Help Code [2]: [" + ultima_word + "]");
                comando = jframe.getHelpCode(ultima_word);
                int positionCaret = comando.indexOf("@");
                comando = Utils.assistCode(comando);

                if (comando != null) {
                    jif.setSelectionStart(jif.getCaretPosition() - ultima_word.trim().length());
                    jif.setSelectionEnd(jif.getCaretPosition());
                    jif.replaceSelection(comando);

                    // updates the caret position
                    if (positionCaret > 0) {
                        jif.setCaretPosition(position - ultima_word.trim().length() + positionCaret);
                    }
                }
            }

            if ((ke.getKeyCode() == KeyEvent.VK_F) && (ke.isControlDown())) {
                String selezione = jif.getSelectedText();
                if (selezione != null) {
                    jframe.setFindText(selezione);
                }
                jif.findString(jframe);
            } else if ((ke.getKeyCode() == KeyEvent.VK_INSERT) && (ke.isControlDown())) {
                // CTRL+INS for "copy" command
                jframe.copyToClipBoard();
            }
            // CTRL+INS for "copy" command
//            else if( (ke.getKeyCode() == KeyEvent.VK_INSERT)&&(ke.isControlDown()) ) {
//                jframe.copyToClipBoard();
//            }
            // SHIFT+INS for "paste" command
//            else if( (ke.getKeyCode() == KeyEvent.VK_INSERT)&&(ke.isShiftDown()) ){
//                String paste = jFrame.getClipboard();
//                if (paste!=null){
//                    MutableAttributeSet attr = new SimpleAttributeSet();
//                    jif.getDocument().insertString(jif.getCaretPosition() , paste , attr);
//                }
//            }

        } catch (Exception ble) {
            System.out.println(ble.getMessage());
        }
    }
}