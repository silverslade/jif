package it.schillaci.jif.core;

/*
 * JifEditorKit.java
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

import it.schillaci.jif.inform.InformContext;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TextAction;
import javax.swing.text.Utilities;

/**
 * JifEditorKit: Editor kit for text documents within JIF
 * 
 * @author Peter Piggott
 * @version 1.0
 * @since JIF 3.2
 * 
 */
public class JifEditorKit extends StyledEditorKit {

    private static final long serialVersionUID = -3980786464262375494L;
    private static int tabSize = 4;
    private static String commentString = "!";
    private static String tabString = Utils.spacesForTab(tabSize);

    /**
     * When reading a document if a TAB is encountered a property
     * with this name is added and the value will be "true". Otherwise a
     * property with this name is added and the value will be "false". 
     */
    public static final String TabConversionProperty = "__TabConversion__";


    public JifEditorKit() {
        super();
    }
    
    // -------------------------------------------------------------------------
    
    /**
     * Inserts content from the given file which is expected 
     * to be in a format appropriate for this kind of content
     * handler.
     * 
     * @param in  The file to read from
     * @param doc The destination for the insertion.
     * @param pos The location in the document to place the
     *   content >= 0.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document.
     */
    public void read(File in, Document doc, int pos) 
        throws IOException, BadLocationException {

        read(new FileInputStream(in), doc, pos);
    }
    
    /**
     * Inserts content from the given stream which is expected 
     * to be in a format appropriate for this kind of content
     * handler.
     * 
     * @param in  The stream to read from
     * @param doc The destination for the insertion.
     * @param pos The location in the document to place the
     *   content >= 0.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document.
     */
    @Override
    public void read(InputStream in, Document doc, int pos) 
        throws IOException, BadLocationException {

        read(new InputStreamReader(in, Constants.fileFormat), doc, pos);
    }

    /**
     * Inserts content from the given buffered reader, which will be
     * treated as plain text.
     *
     * @param in  The buffered stream to read from
     * @param doc The destination for the insertion.
     * @param pos The location in the document to place the
     *   content >= 0.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document.
     */
    @Override
    public void read(Reader in, Document doc, int pos)
            throws IOException, BadLocationException {
     
        BufferedReader br = new BufferedReader(in);
        SimpleAttributeSet sas = new SimpleAttributeSet();
        StringBuilder sb = new StringBuilder();
        String line = "";
        
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        
        br.close();

        String text = sb.toString();
                
        // Check for tab characters
        if (text.indexOf("\t")!=-1) {
            doc.putProperty(TabConversionProperty, "true");
            doc.insertString(
                    0,
                    Utils.replace(text, "\t", JifEditorKit.getTabString()),
                    sas);
        } else {
            doc.putProperty(TabConversionProperty, "false");
            doc.insertString(0, text, sas);
        }
    }
    
    /**
     * Writes content from a document to the given stream
     * in a format appropriate for this kind of content handler.
     * 
     * @param out The file to write to
     * @param doc The source for the write.
     * @param pos The location in the document to fetch the
     *   content >= 0.
     * @param len The amount to write out >= 0.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document.
     */
    public void write(File out, Document doc, int pos, int len) 
        throws IOException, BadLocationException {
        
        write(new FileOutputStream(out), doc, pos, len);
    }

    // --- EditorKit methods ---------------------------------------------------

    /**
     * Fetches the command list for the editor. This is the list of commands
     * supported by the superclass augmented by the collection of commands
     * defined locally for Jif text editor operations.
     * 
     * @return the command list
     */
    @Override
    public Action[] getActions() {
        return TextAction.augmentList(super.getActions(), JifActions);
    }
    
    /**
     * Creates an uninitialised text storage model (<code>JifDocument</code>)
     * that is appropriate for this type of editor.
     * 
     * @return the model
     */
    @Override
    public Document createDefaultDocument() {
        return new JifDocument();
    }
    
    public JifDocument createDefaultDocument(InformContext context) {
        return new JifDocument(context);
    }
    
    // --- Class methods -------------------------------------------------------

    /**
     * Get the comment string for commenting lines in Jif documents
     *
     */
    public static String getCommentString() {
        return commentString;
    }
    
    /**
     * Get the tab string for indenting Jif documents
     *
     */
    public static String getTabString() {
        return tabString;
    }
    
    /**
     * Get the tab size for indenting Jif documents
     * 
     * 
     * @return
     *            The number of spaces in one level of indenting
     */
    public static int getTabSize() {
        return tabSize;
    }

    /**
     * Set the tabString size for indenting Jif documents
     * 
     * 
     * @param tabSize
     *            The number of spaces in one level of indenting
     */
    public static void setTabSize(int tabSize) {
        JifEditorKit.tabSize = tabSize;
        JifEditorKit.tabString = Utils.spacesForTab(tabSize);
    }
    
    // --- Names of Jif actions ------------------------------------------------
    
    /**
     * Name of the <code>Action</code> to apply comments to a selected block
     * of paragraphs in a Jif document (or subclass).
     */
    public static final String commentAction = "comment-line";
    
   /**
     * Name of the <code>Action</code> to apply a tab key to a Jif document
     * (or subclass).
     */
    public static final String tabAction = "tab-key";
    
    /**
     * Name of the <code>Action</code> to apply an indent to a selected block
     * of paragraphs in a Jif document (or subclass).
     */
    public static final String tabRightAction = "tab-right-line";
    
    /**
     * Name of the <code>Action</code> to remove an indent from a selected block
     * of paragraphs in a Jif document (or subclass).
     */
    public static final String tabLeftAction = "tab-left-line";
    
    /**
     * Name of the <code>Action</code> to remove comments from a selected block
     * of paragraphs in an Jif document (or subclass).
     */
    public static final String uncommentAction = "uncomment-line";

    // --- Action implementations ----------------------------------------------

    private static final Action[] JifActions = {
        new CommentAction(),
        new TabAction(),
        new TabRightAction(),
        new TabLeftAction(),
        new UncommentAction()
        };
  
    /**
     * An action that assumes it's being fired on a JEditorPane (or subclass)
     * with a JifEditorKit (or subclass) installed. This has some convenience
     * methods for causing syntax level changes. The methods will throw an
     * IllegalArgumentException if the assumption of a JifDocument, a
     * JEditorPane, or an JifEditorKit fail to be true.
     * <p>
     * The component that gets acted upon by the action will be the source of
     * the ActionEvent if the source can be narrowed to a JEditorPane type. If
     * the source can't be narrowed, the most recently focused text component is
     * changed. If neither of these are the case, the action cannot be
     * performed.
     */
    public abstract static class JifTextAction extends StyledTextAction {
        
        /**
         * Creates a new JifTextAction from a string action name.
         *
         * @param nm
         *            the name of the action
         */
        public JifTextAction(String nm) {
            super(nm);
        }
        
        /**
         * Gets the document associated with an editor pane.
         *
         * @param editor
         *            the editor
         * @return the document
         * @exception IllegalArgumentException
         *                for the wrong document type
         */
        protected final JifDocument getJifDocument(JEditorPane editor) {
            Document doc = editor.getDocument();
            if (doc instanceof JifDocument) {
                return (JifDocument) doc;
            }
            throw new IllegalArgumentException(
                    "document must be JifDocument");
        }

        /**
         * Gets the editor kit associated with an editor pane.
         *
         * @param editor
         *            the editor pane
         * @return the kit
         * @exception IllegalArgumentException
         *                for the wrong editor kit
         */
        protected final JifEditorKit getJifEditorKit(JEditorPane editor) {
            EditorKit kit = editor.getEditorKit();
            if (kit instanceof JifEditorKit) {
                return (JifEditorKit) kit;
            }
            throw new IllegalArgumentException(
                    "EditorKit must be JifEditorKit");
        }

        /*
         * Add a prefix each paragraph in a block with a string prefix
         *
         * @param editor
         *            the editor pane
         * @param string 
         *            the string to prefix to the start of each paragraph
         */
        protected final void addPrefixBlock(JEditorPane editor, String prefix)
                throws BadLocationException {
            
            StringBuilder output = new StringBuilder();
            int p0 = editor.getSelectionStart();
            int p1 = editor.getSelectionEnd();
            JifDocument doc = getJifDocument(editor);
            Element root = doc.getDefaultRootElement();
            int startIndex = root.getElementIndex(p0);
            int endIndex = root.getElementIndex(p1);
            
            for (int i = startIndex; i < endIndex; i++) {
                Element line = root.getElement(i);
                String lineString = doc.getText(line.getStartOffset(),
                        line.getEndOffset() - line.getStartOffset());
                output.append(prefix).append(lineString);
            }
            
            editor.replaceSelection(output.toString());
            setSelection(editor, p0, output.length());
        }
        
        /*
         * Remove a prefix from each paragraph in a block with a string prefix
         *
         * @param editor
         *            the editor pane
         * @param string 
         *            the string to prefix to the start of each paragraph
         */
        protected final void removePrefixBlock(JEditorPane editor, String prefix)
                throws BadLocationException {

            StringBuilder output = new StringBuilder();
            int p0 = editor.getSelectionStart();
            int p1 = editor.getSelectionEnd();
            JifDocument doc = getJifDocument(editor);
            Element root = doc.getDefaultRootElement();
            int startIndex = root.getElementIndex(p0);
            int endIndex = root.getElementIndex(p1);

            for (int i = startIndex; i < endIndex; i++) {
                Element line = root.getElement(i);

                String lineString = doc.getText(line.getStartOffset(),
                        line.getEndOffset() - line.getStartOffset());

                if (lineString.startsWith(prefix)) {
                    output.append(
                            lineString.substring(
                            prefix.length(),
                            lineString.length()));
                } else {
                    output.append(lineString);
                }
            }

            editor.replaceSelection(output.toString());
            setSelection(editor, p0, output.length());

        }
        
        /*
         * Extend the selection to the include the complete block of paragraphs
         * around the current selection
         */
        protected final void selectBlock(JEditorPane editor) {
            selectBeginBlock(editor);
            selectEndBlock(editor);
        }
        
        /**
         * Extend the selection to include the start of the first paragraph in
         * the current selection
         */
        protected final void selectBeginBlock(JEditorPane editor) {
            editor.setSelectionStart(Utilities.getParagraphElement(editor,
                    editor.getSelectionStart()).getStartOffset());
        }
        
        /**
         * Extend the selection to include the end of the last paragraph in the
         * current selection
         */
        protected final void selectEndBlock(JEditorPane editor) {
            editor.setSelectionEnd(Utilities.getParagraphElement(editor,
                    editor.getSelectionEnd()).getEndOffset());
        }
        
        /**
         * Set the selection in the editor
         */
        protected final void setSelection(JEditorPane editor, int start, int length) {
            editor.requestFocus();
            editor.setSelectionStart(start);
            editor.setSelectionEnd(start + length - 1);
        }
    }

    /**
     * Comment the block of paragraphs around the current selection.
     */
    public static class CommentAction extends JifTextAction {

        private static final long serialVersionUID = -3022913883687591007L;

        /**
         * Constructs a new commentAction.
         */
        public CommentAction() {
            super(commentAction);
        }

        /**
         * Comment the current selection.
         * 
         * @param e
         *            the action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor == null) {
                return;
            }    
            selectBlock(editor);
            try {
                addPrefixBlock(editor, JifEditorKit.getCommentString());
            } catch (BadLocationException ble) {
                ble.printStackTrace();
            }
        }
        
    }
    
    /**
     * Insert spaces at the caret instead of a tab character or indent if there 
     * is a current selection.
     */
    private static class TabAction extends JifTextAction {
        
        private static final long serialVersionUID = -1;
        
        /**
         * Constructs a new tab action.
         */
        public TabAction() {
            super(tabAction);
        }
                
        /**
         * Indent the lines in the current selection.
         * 
         * @param e
         *            the action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor == null) {
                return;
            }
            if (editor.getSelectionStart() == editor.getSelectionEnd()) {
                editor.replaceSelection(JifEditorKit.getTabString());
            } else {
                selectBlock(editor);
                try {
                    addPrefixBlock(editor, JifEditorKit.getTabString());
                } catch (BadLocationException ble) {
                    ble.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Indent the block of paragraphs around the current selection.
     */
    public static class TabRightAction extends JifTextAction {

        private static final long serialVersionUID = 7413532495343267493L;

        /**
         * Constructs a new tabRightAction.
         */
        public TabRightAction() {
            super(tabRightAction);
        }

        /**
         * Indent the lines in the current selection.
         * 
         * @param e
         *            the action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor == null) {
                return;
            }    
            selectBlock(editor);
            try {
                addPrefixBlock(editor, JifEditorKit.getTabString());
            } catch (BadLocationException ble) {
                ble.printStackTrace();
            }
        }
        
    }

    /**
     * Remove an indent from the block of paragraphs around the current selection.
     */
    public static class TabLeftAction extends JifTextAction {

        private static final long serialVersionUID = -9092156320494006077L;

        /**
         * Constructs a new tabLeftAction.
         */
        public TabLeftAction() {
            super(tabLeftAction);
        }

        /**
         * Remove an indent from the lines of the current selection.
         * 
         * @param e
         *            the action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor == null) {
                return;
            }    
            selectBlock(editor);
            try {
                removePrefixBlock(editor, JifEditorKit.getTabString());
            } catch (BadLocationException ble) {
                ble.printStackTrace();
            }
        }
        
    }
    
    /**
     * Remove comments from the block of paragraphs around the current selection.
     */
    public static class UncommentAction extends JifTextAction {

        private static final long serialVersionUID = 3737383110193686233L;

        /**
         * Constructs a new commentAction.
         */
        public UncommentAction() {
            super(uncommentAction);
        }

        /**
         * Remove comments from the lines in the current selection.
         * 
         * @param e
         *            the action event
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor == null) {
                return;
            }    
            selectBlock(editor);
            try {
                removePrefixBlock(editor, JifEditorKit.getCommentString());
            } catch (BadLocationException ble) {
                ble.printStackTrace();
            }
        }
       
    }
    
}