package it.schillaci.jif.gui;

/*
 * JifTextPane.java
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
import it.schillaci.jif.core.Constants;
import it.schillaci.jif.core.HighlightBookmark;
import it.schillaci.jif.core.HighlightText;
import it.schillaci.jif.core.JifDAO;
import it.schillaci.jif.core.JifDocument;
import it.schillaci.jif.core.JifEditorKit;
import it.schillaci.jif.core.JifFileName;
import it.schillaci.jif.core.TranslatedString;
import it.schillaci.jif.core.Utils;
import it.schillaci.jif.inform.InformContext;
import it.schillaci.jif.inform.InformSyntax;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.Utilities;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * This is a sub-class of JTextPane, with the Inform source management.
 *
 * @author Alessandro Schillaci
 * @author Peter Piggott
 * version 2.0
 */
public class JifTextPane extends JTextPane {

    private static final long serialVersionUID = 1475021670099346825L;
    private UndoManager undoF;
    private jFrame jframe;
    private Element el;
    private MouseListener popupListener;
    private String pathfile;
    private String subPath;
    private HighlightText hlighterJumpTo;
    private HighlightText hlighterBrackets;
    private HighlightText hlighterErrors;
    private HighlightText hlighterWarnings;
    private HighlightBookmark hlighterBookmarks;
    private List bookmarks = new ArrayList();
    private JifDocument doc;
    private JifEditorKit editorKit;
    private boolean wrap;

    /**
     * Creates a new instance of JifTextPane
     *
     * @param parent The instance of main jFrame
     * @param fileName Name of the file to be load into JifTextPane.
     */
    public JifTextPane(jFrame parent, JifFileName fileName, File file, InformContext context) {
        jframe = parent;
        subPath = fileName.getTabTitle();
        popupListener = new PopupListener(jframe, this);
        initHighlighters(context);
        initView(context);
        
        editorKit = (jframe.isSyntaxHighlighting()) ?
            fileName.createEditorKit() :
            new JifEditorKit();

        initDocument(file, context);

        initUndoManager();
        initActions();
        
        // Editor key adapter is only used with inform content
        if (fileName.getContentType().equals(JifFileName.INFORM)) {
            addKeyListener(new EditorKeyAdapter(jframe, this));
        }

        // Add Mouse Listener for the right-mouse popup
        addMouseListener(popupListener);

        setEditorKit(editorKit);
        setDocument(doc);
    }

    /**
     * This method is called from within the constructor to initialise the
     * document content for the text pane.
     */
    private void initDocument(File file, InformContext context) {
        doc = editorKit.createDefaultDocument(context);

        long tempo1=System.currentTimeMillis();
        try {
            if (file != null) {
                editorKit.read(file, doc, 0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("Tempo impiegato= "+(System.currentTimeMillis()-tempo1));
    }

    /**
     * This method is called from within the constructor to initialise the undo
     * manager for the text pane.
     */
    private void initUndoManager() {
        undoF = new UndoManager();
        undoF.setLimit(5000);

        doc.addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent evt) {
                undoF.addEdit(evt.getEdit());
                // adding a "*" to the file name, when the file has changed but not saved
                if (jframe.getFileTabCount() != 0 && jframe.getSelectedPath().indexOf("*") == -1) {
                    jframe.setSelectedTitle(subPath + "*");
                    jframe.setTitle(jframe.getJifVersion() + " - " + jframe.getSelectedPath());
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialise the text
     * pane actions.
     */
    private void initActions() {
        
        getActionMap().put("Undo", new AbstractAction("Undo") {
            private static final long serialVersionUID = 4366132315214562191L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undoF.canUndo()) {
                        while (undoF.getUndoPresentationName().equals(java.util.ResourceBundle.getBundle("JIF").getString("JFRAME_STYLE_CHANGED_UNDO"))) {
                            undoF.undo();
                        }
                        undoF.undo();
                        // adding a "*" to the file name, when the file has changed but not saved
                        if (jframe.getFileTabCount() != 0 && jframe.getSelectedPath().indexOf("*") == -1) {
                            jframe.setSelectedTitle(subPath + "*");
                            jframe.setTitle(jframe.getJifVersion() + " - " + jframe.getSelectedPath());
                        }
                    }
                } catch (CannotUndoException e) {
                    System.err.println("Unable to undo: " + e.getMessage());
                }
            }
        });

       getActionMap().put("Redo", new AbstractAction("Redo") {
            private static final long serialVersionUID = 3720633173380513902L;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (undoF.canRedo()) {
                        while (undoF.getRedoPresentationName().equals(java.util.ResourceBundle.getBundle("JIF").getString("JFRAME_STYLE_CHANGED_REDO"))) {
                            undoF.redo();
                        }
                        undoF.redo();
                        // adding a "*" to the file name, when the file has changed but not saved
                        if (jframe.getFileTabCount() != 0 && jframe.getSelectedPath().indexOf("*") == -1) {
                            jframe.setSelectedTitle(subPath + "*");
                            jframe.setTitle(jframe.getJifVersion() + " - " + jframe.getSelectedPath());
                        }
                    }
                } catch (CannotRedoException e) {
                    System.err.println("Unable to redo: " + e.getMessage());
                }
            }
        });

        // JifTextPane actions
        getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

        // JifEditorKit actions
        getInputMap().put(KeyStroke.getKeyStroke("TAB"), JifEditorKit.tabAction);
        getInputMap().put(KeyStroke.getKeyStroke("alt RIGHT"), JifEditorKit.tabRightAction);
        getInputMap().put(KeyStroke.getKeyStroke("alt LEFT"), JifEditorKit.tabLeftAction);
        getInputMap().put(KeyStroke.getKeyStroke("shift TAB"), JifEditorKit.tabLeftAction);
        
    }
    
    /**
     * This method is called from within the constructor to initialise the text
     * pane high lighters based on configuration settings.
     */
    private void initHighlighters(InformContext context) {

        hlighterBookmarks = new HighlightBookmark(
                this,
                context.getForeground(InformSyntax.Bookmarks));
        hlighterBrackets = new HighlightText(
                this,
                context.getForeground(InformSyntax.Brackets));
        hlighterErrors = new HighlightText(
                this,
                context.getForeground(InformSyntax.Errors));
        hlighterJumpTo = new HighlightText(
                this,
                context.getForeground(InformSyntax.JumpTo));
        hlighterWarnings = new HighlightText(
                this,
                context.getForeground(InformSyntax.Warnings));
    }

    /**
     * This method is called from within the constructor to initialise the
     * text pane based on configuration view setting
     * 
     * @param context 
     */
    private void initView(InformContext context) {

        setDoubleBuffered(true);
        setEditable(true);
        setFont(context.getFont());
        setBackground(context.getBackground());
        setCaretColor(context.getForeground(InformSyntax.Normal));
        getCaret().setBlinkRate(200);

    }

    /**
     * Get the current text in this <code>TextComponent</code> wrapped in
     * a <code>CharBuffer</code>
     * @return 
     */
    public CharBuffer getCharBuffer() {

        CharBuffer cb = null;
        try {
            cb = JifDAO.buffer(getText());
        } catch (CharacterCodingException ex) {
            System.err.println("Unable to encode text in getCharBuffer:" + ex.getMessage());
        } finally {
            return cb;
        }
    }

    /**
     * Returns the current row (as a String) from the current caret position
     *
     * @return the text of the current caret row
     */
    public String getCurrentRow() {
        String lastRow = null;
        try {
            Element el = getElementAt(getCaretPosition() - 1);
            lastRow = getText(el.getStartOffset(), el.getEndOffset() - el.getStartOffset());
        } catch (BadLocationException e) {
            System.err.println("Unable to get current row:" + e.getMessage());
        }
        return lastRow;
    }

    /**
     * Returns the current row (as a String) from a given Caret position
     *
     * @param posizione The position of Caret
     * @return text of the row at the specified position
     */
    public String getRowAt(int posizione) {
        String lastRow = null;
        try {
            Element el = getElementAt(posizione);
            lastRow = getText(el.getStartOffset(), el.getEndOffset() - el.getStartOffset());
        } catch (BadLocationException e) {
            System.err.println("Unable to get row at " + posizione + ":" + e.getMessage());
        }
        return lastRow;
    }

    /**
     * Returns the current row (as a String to UPPER CASE) from a given Caret
     * position
     *
     * @param posizione The position of Caret
     * @return The row at the position
     */
    public String getUpperRowAt(int posizione) {
        String lastRow = null;
        try {
            Element el = getElementAt(posizione);
            lastRow = getText(el.getStartOffset(), el.getEndOffset() - el.getStartOffset());
        } catch (BadLocationException e) {
            System.err.println("Unable to get row at " + posizione + ":" + posizione);
        }
        return lastRow.toUpperCase();
    }

    /**
     * Returns the position of the close bracket matching the given open bracket
     * searching from the position given.
     *
     * @param open
     *              Open bracket to match
     * @param close
     *              Close bracket to find
     * @param pos
     *              The position to start the search
     * @return The position of the close bracket or -1 if none found
     */
    
    int findCloseBracket(String open, String close, int position) {
        int c = position + 1;
        int opened = 0;  // number of opened brackets
        try {
            while (c < getLength()) {
                // matching close bracket found
                if (getText(c, 1).equals(close) && opened==0) {
                    return c;
                }
                // if an open bracket is found, opened++
                if (getText(c, 1).equals(open)) {
                    opened++;
                }
                // if a close bracket is found, opened--
                if (getText(c, 1).equals(close) && opened!=0) {
                    opened--;
                }
                c++;
            }
            return -1;  // no matching bracket found
            
        } catch (Exception ex) {
            System.err.println("Find close bracket: "+ close + " position: " + position + " c: " + c);
            return -1;  // an error occured so no matching bracket found
        }
    }
    
    /**
     * Returns the position of the open bracket matching the given close bracket
     * searching from the position given.
     *
     * @param open
     *              Open bracket to find
     * @param close
     *              Close bracket to match
     * @param pos
     *              The position to start the search
     * @return The position of the close bracket or -1 if none found
     */

    int findOpenBracket(String open, String close, int position) {
        int c = position - 1;
        int closed = 0;  // number of opened brackets
        try {
            while (c > 0) {
                // matching open bracket found
                if (getText(c, 1).equals(open) && closed==0) {
                    return c;
                }
                // if an open bracket is found, closed++
                if (getText(c, 1).equals(close)) {
                    closed++;
                }
                // if a close bracket is found, closed--
                if (getText(c, 1).equals(open) && closed!=0) {
                    closed--;
                }
                c--;
            }
            return -1;  // no matching open bracket found
            
        } catch (Exception ex) {
            System.err.println("Find open bracket: " + open + " position: " + position + " c: " + c);
            return -1;  // an error occured so no open matching bracket found
        }
    }
    
    // Highlight matching close bracket (if any)
    void highlightCloseBracket(String open, String close) {
        int c = findCloseBracket(open, close, getCaretPosition()-1);
        if (c != -1) {
            hlighterBrackets.highlightFromTo(this, c, c+1);
        }
    }
    
    // Highlight matching open bracket (if any)
    void highlightOpenBracket(String open, String close) {
        int c = findOpenBracket(open, close, getCaretPosition()-1);
        if (c != -1) {
            hlighterBrackets.highlightFromTo(this, c, c+1);
        }
    }
    
    // TODO
    // Some bracket nesting errors do not highlight all errors.
    // e.g. [{(]}) shows the errors for brackets 1,3,4,6 not 1,2,3,4,5,6
    // This happens whenever equal numbers of open and close nesting errors
    // occur within a bracket pair.
    // TODO
    // Nesting of routine brackets is not allowed in Inform
    // e.g. [[]]
    /**
     * Performs a brackets validation check. If this finds an incomplete bracket
     * (either not opened or not closed), the bracket will be highlighted.
     *
     * @return <code>true</code> if all brackets are valid
     */
    boolean checkBrackets() {
        int errors = 0;
        int first = -1;
        
        for (Iterator i=getJifDocument().bracketErrors(); i.hasNext(); ) {
            Integer value = (Integer) i.next();
            int position = value.intValue();
            hlighterBrackets.highlightFromTo(this, position, position+1);
            if (first == -1) {
                first = position;
            }
            errors++;
        }
        if (first != -1) {
            setCaretPosition(first);
        }
        return errors==0;
    }
    
    /**
     * Finds a String in the JifTextPane and highlight it.
     * The target String to be found is taken from the Search TextField.
     */
    public void findString(jFrame parent) {
        int pos = getCaretPosition();
        String pattern = parent.getFindText();
        
        String text = getText();
        boolean found = false;
        
        //while ( ( (pos = text.indexOf(pattern, pos)) >= 0) && (!found)) {
        while ((pos = Utils.IgnoreCaseIndexOf(text, pattern, pos)) >= 0  && !found) {
            //hlighterJumpTo.highlightFromTo(this, pos, pos + pattern.length());
            // Bug #4416
            this.setSelectionStart(pos);
            this.setSelectionEnd(pos + pattern.length());
            // Bug #4416
            pos += pattern.length();
            //setCaretPosition(pos);
            found = true;
            this.requestFocus();
        }
        
        // If the string not found, JIF will move the Caret position to 0 (zero)
        if (!found) {
            // if at least one string is found
            if (Utils.IgnoreCaseIndexOf(text, pattern, 0) != -1) {
                // append a message in the outputwindow
                parent.outputSetText(java.util.ResourceBundle.getBundle("JIF").getString("JIF_END_OF_FILE"));
                setCaretPosition(0);
                findString(parent);
            } else {
                // if there aren't any occurences of the string
                // append a message in the outputwindow
                parent.outputSetText("String \"" + parent.getFindText() + "\" not found");
            }
        }
    }
    
    public void setError(int offset) {
        
    }
    
    public void setWarning(int offset) {
        
    }
    
    public void highlightErrors(int start, int end) {
        hlighterErrors.highlightFromTo(this, start, end);
    }
    
    public void highlightWarnings(int start, int end) {
        hlighterWarnings.highlightFromTo(this, start, end);
    }
    
    public void highlightFromTo(int start, int end) {
        hlighterJumpTo.highlightFromTo(this, start, end);
    }
    
    /**
     * Remove current highlighter from the JifTextPane
     */
    public void removeHighlighter() {
        if (hlighterJumpTo == null) {
            return;
        }
        hlighterJumpTo.removeHighlights(this);
    }
    
    /**
     * Remove all the check-brackets highlighters from
     * current JifTextPane
     */
    public void removeHighlighterBrackets() {
        if (hlighterBrackets == null) {
            return;
        }
        hlighterBrackets.removeHighlights(this);
    }
    
    /**
     * Remove current highlighterErrors from the JifTextPane
     */
    public void removeHighlighterErrors() {
        if (hlighterErrors == null) {
            return;
        }
        hlighterErrors.removeHighlights(this);
    }
    
    /**
     * Remove current highlighterWarnings from the JifTextPane
     */
    public void removeHighlighterWarnings() {
        if (hlighterWarnings == null) {
            return;
        }
        hlighterWarnings.removeHighlights(this);
    }
    
    /**
    /**
     * This method extracts all strings from source Inform code and saves them
     * into a new file ("translate.txt")
     * <br><br>
     * The format is:<br>
     * <br>
     * <code>*****<br>
     * STRING1 ===== STRING1 TRANSLATION<br>
     * <br>
     * *****<br>
     * STRING2 ===== STRING2 TRANSLATION<br>
     * <br>
     * ...</code>
     * <br>
     * This file will be used by the "insertTranslate()" method to rescue the
     * translations from translate.txt file and merge into the current file to
     * create a new file with the translation ("translated.inf")
     *
     * @param file The Output File (i.e. "translate.txt")
     */
    public void extractTranslate(File file) {
        StringBuilder translate = new StringBuilder();
        String appoggio;
        String testo = getText();

        // controllo che non ci siano parentesi opened-closed
        testo = Utils.replace(testo, "\"\"", "\" \"");

        StringTokenizer stok = new StringTokenizer(testo, "\"\"");
        stok.nextToken();
        while (stok.hasMoreTokens()) {
            appoggio = stok.nextToken();
            // To eliminate cases like: ".", "   "
            if (!(appoggio.trim().equals(""))
                    && (appoggio.length() > 1)) {
                translate.append("*****\n");
                translate.append(appoggio);
                translate.append("\n=====\n");
                translate.append(appoggio);
                translate.append("\n");
            }
            if (stok.hasMoreTokens()) {
                stok.nextToken();
            }
        }
        try {
            JifDAO.save(file, translate.toString());
            okMessage();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void okMessage() {
        JOptionPane.showMessageDialog(
                null,
                "OK",
                "Message",
                JOptionPane.INFORMATION_MESSAGE);
    }
    /**
     * This method rescues the translations from translate.txt file and merges
     * into the current file. This creates a new file with translated text
     * called ("translated.inf")
     *
     *
     * @param file Current file in the JifTextPane
     * @param fileout The output file (i.e. "translated.inf")
     */
    public void insertTranslate(File file, File fileout) {
        String testo = getText();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Constants.fileFormat));
            boolean chiave = false;
            String key = null;
            String obj = null;
            String riga;
            Vector<TranslatedString> strings = new Vector<TranslatedString>();

            while ((riga = br.readLine()) != null) {
                if (riga.startsWith("*****")) {
                    chiave = true;
                    if (obj != null) {
                        // serve come entry nel caso della prima key
                        // inserisco la chiave al passo precedente
                        if (key.startsWith("\n")) {
                            key = key.substring(1);
                        }
                        if (obj.startsWith("\n")) {
                            obj = obj.substring(1);
                        }
                        //testo = Utils.replace(testo, key , obj);
                        strings.add(new TranslatedString(key, obj));
                        //strings.put(key,obj);
                        key = "";
                        obj = "";
                    }
                } else if (riga.startsWith("=====")) {
                    chiave = false;
                } else if (chiave == true) {
                    // sto leggendo una chiave
                    if (key != null) {
                        key = key + "\n" + riga;
                    } else {
                        key = riga;
                    }
                } else if (chiave == false) {
                    // sto leggendo una chiave
                    if (obj != null) {
                        obj = obj + "\n" + riga;
                    } else {
                        obj = riga;
                    }
                }
            }
            br.close();

            // FIX: sort HashMap by length of strings
            // Before JIF translates the longer strings and then the shorter ones
            Collections.sort(new ArrayList<TranslatedString>(strings), new Comparator() {
                public int compare(Object a, Object b) {
                    String id1 = ((TranslatedString) a).getSource();
                    String id2 = ((TranslatedString) b).getResult();
                    return (id2.length() - id1.length());
                }
            });

            // Execute the translation using the sorted strings
            for (Iterator i = strings.iterator(); i.hasNext();) {
                TranslatedString ts = (TranslatedString) i.next();
                testo = Utils.replace(testo, "\"" + ts.getSource() + "\"", "\"" + ts.getResult() + "\"");
            }
            // FIX

        } catch (IOException e) {
            System.err.println("Unable to read translate.txt: " + e.getMessage());
        }

        // Saving the translation to the output file
        try {
            JifDAO.save(fileout, testo);
            okMessage();
        } catch (IOException e) {
            System.err.println("Unable to save translate.inf:" + e.getMessage());
        }
    }

    public void setBookmark() {
        updateBookmark(new Integer(getCaretIndex()));
    }

    public void nextBookmark() {
        // position = actual row
        int jumpto = 0;

        if (bookmarks.size() == 0) {
            return;
        }

        int row = getCaretIndex();

        for (Iterator i = bookmarks.iterator(); i.hasNext();) {
            Integer ind = (Integer) i.next();
            if (row < ind.intValue()) {
                jumpto = ind.intValue();
                break;
            }
        }

        // last bookmark
        if (jumpto == 0 && bookmarks.size() != 0) {
            jumpto = ((Integer) bookmarks.get(0)).intValue();
        }

        // Jump to the bookmark row
        jumpToIndex(jumpto);
    }

    public void applyBookmarks() {
        // Remove the hlighterBookmarks
        if (hlighterBookmarks != null) {
            hlighterBookmarks.removeHighlights(this);
        }
        // Repaint all the highlights
        Element element;
        for (Iterator i = bookmarks.iterator(); i.hasNext();) {
            Integer ind = (Integer) i.next();
            element = getDocument().getDefaultRootElement().getElement(ind.intValue());
            hlighterBookmarks.highlightFromTo(this, element.getStartOffset(), element.getEndOffset());
        }
    }

    /**
     * Toggle bookmark on current line
     *
     * @param line
     */
    public void updateBookmark(Integer line) {
        if (bookmarks.contains(line)) {
            bookmarks.remove(line);
        } else {
            bookmarks.add(line);
        }
        applyBookmarks();
    }

    // returns the current Word using the caret Position
    public String getCurrentWord() throws BadLocationException {
        int start = Utilities.getWordStart(this, getSelectionStart());
        int end = Utilities.getWordEnd(this, getSelectionStart());
        String word = getDocument().getText(start, end - start);
        if (word.indexOf(".") != -1) {
            word = word.substring(0, word.lastIndexOf("."));
        }
        //System.out.println( "Selected word: " + word );
        return word;
    }

    void jumpToError(int index) {
        try {
            Element el = getElement(index);
            highlightErrors(el.getStartOffset(), el.getEndOffset());
            scrollRectToVisible(modelToView(getLength()));
            scrollRectToVisible(modelToView(el.getStartOffset()));
            setCaretPosition(el.getStartOffset());
        } catch (BadLocationException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    void jumpToIndex(int index) {
        try {
            Element el = getElement(index);
            scrollRectToVisible(modelToView(getLength()));
            scrollRectToVisible(modelToView(el.getStartOffset()));
            setCaretPosition(el.getStartOffset());
        } catch (BadLocationException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    void jumpToPosition(int offset) {
        try {
            Element el = getElementAt(offset);
            highlightFromTo(el.getStartOffset(), el.getEndOffset());
            scrollRectToVisible(modelToView(getLength()));
            scrollRectToVisible(modelToView(offset));
            setCaretPosition(offset);
        } catch (BadLocationException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    void jumpToText(int index) {
        try {
            Element el = getElement(index);
            removeHighlighter();
            highlightFromTo(el.getStartOffset(), el.getEndOffset());
            scrollRectToVisible(modelToView(getLength()));
            scrollRectToVisible(modelToView(el.getStartOffset()));
            setCaretPosition(el.getStartOffset());
        } catch (BadLocationException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    void jumpToWarning(int index) {
        try {
            Element el = getElement(index);
            highlightWarnings(el.getStartOffset(), el.getEndOffset());
            scrollRectToVisible(modelToView(getLength()));
            scrollRectToVisible(modelToView(el.getStartOffset()));
            setCaretPosition(el.getStartOffset());
        } catch (BadLocationException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // --- JEditorPane Methods -------------------------------------------------
    
    /**
     * Creates the <code>EditorKit</code> to use by default. This is
     * implemented to return <code>JifEditorKit</code>.
     */
    @Override
    protected EditorKit createDefaultEditorKit() {
        return new JifEditorKit();
    }
    
    /**
     * Controls whether the scrollable tracks viewport width based on the editor
     * line wrapping configuration.
     * @see setWrap
     */
    public boolean getScrollableTracksViewportWidth() {
        
        if (wrap) {
            return super.getScrollableTracksViewportWidth();
        }
        
        // Otherwise prevent line wrapping
        return getSize().width < getParent().getSize().width;
    }
    
    /**
     * Controls the editor size based on the editor line wrapping configuration
     */
    @Override
    public void setSize(Dimension d) {
        
        if (!wrap && d.width < getParent().getSize().width) {
            d.width = getParent().getSize().width;
        }
        
        super.setSize(d);
    }
    
    // --- Accessor methods ----------------------------------------------------

    /**
     * Returns the current HighlighterErrors
     *
     * @return the current HighlighterErrors
     */
    public HighlightText getHlighterErrors() {
        return hlighterErrors;
    }

    /**
     * Returns the current HighlighterJumpTo
     *
     * @return the current HighlighterJumpTo
     */
    public HighlightText getHlighter() {
        return hlighterJumpTo;
    }

    /**
     * Returns the current HighlighterWarnings
     *
     * @return the current HighlighterWarnings
     */
    public HighlightText getHlighterWarnings() {
        return hlighterWarnings;
    }

    /**
     * Associates the editor with a text document.
     * The currently registered factory is used to build a view for
     * the document, which gets displayed by the editor.
     *
     * @param doc  the document to display/edit
     */
    public void setJifDocument(JifDocument doc) {
        super.setDocument(doc);
    }
    
    /**
     * Fetches the model associated with the editor.
     *
     * @return the model
     */
    public JifDocument getJifDocument() {
        return (JifDocument) getDocument();
    }
    
    public void setPaths(String aString) {
        this.pathfile = aString;
        if (pathfile.length() > 20) {
            subPath = pathfile.substring(0, 10)
                    + "..."
                    + pathfile.substring(pathfile.length() - 20, pathfile.length());
        } else {
            subPath = pathfile;
        }
    }

    public String getSubPath() {
        return subPath;
    }

    public void setSubPath(String subPath) {
        this.subPath = subPath;
    }
    
    void setWrap(boolean b) {
        this.wrap = wrap;
    }
    
    // --- Helper methods ------------------------------------------------------
    
    Element getCaretElement() {
        return getElement(getCaretIndex());
    }
    
    int getCaretIndex() {
        return getElementIndex(getCaretPosition());
    }
    
    Element getDefaultRootElement() {
        return getDocument().getDefaultRootElement();
    }
    
    Element getElement(int index) {
        return getDefaultRootElement().getElement(index);
    }
    
    Element getElementAt(int offset) {
        return getElement(getElementIndex(offset));
    }
    
    int getElementIndex(int offset) {
        return getDefaultRootElement().getElementIndex(offset);
    }
    
    int getLength() {
        return getDocument().getLength();
    }
    
}