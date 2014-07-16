package it.schillaci.jif.inform;

/*
 * InformEditorKit.java
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

import it.schillaci.jif.core.JifDocument;
import it.schillaci.jif.core.JifEditorKit;
import javax.swing.JEditorPane;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;

/**
 * InformEditorKit: Editor kit for Inform source code documents
 * 
 * @author Peter Piggott
 * @version 1.0
 * @since JIF 3.2
 * 
 */
public class InformEditorKit extends JifEditorKit {

    private static final long serialVersionUID = 4455270922791207490L;

    public InformEditorKit() {
        super();
    }

    // --- EditorKit methods ---------------------------

    /**
     * Gets the MIME type of the data that this kit represents support for. The
     * default is <code>text/inform</code>.
     * 
     * @return the type
     */
    @Override
    public String getContentType() {
        return "text/inform";
    }
    
    /**
     * Creates an uninitialised text storage model (<code>InformDocument</code>)
     * that is appropriate for this type of editor.
     * 
     * @return the model
     */
    @Override
    public Document createDefaultDocument() {
        return new InformDocument();
    }
    
    @Override
    public JifDocument createDefaultDocument(InformContext context) {
        return new InformDocument(context);
    }
    
    // --- Action implementations ----------------------------------------------

    /**
     * An action that assumes it's being fired on a JEditorPane (or subclass)
     * with an InformEditorKit (or subclass) installed. This has some
     * convenience methods for causing Inform syntax level changes. The methods
     * will throw an IllegalArgumentException if the assumption of an
     * InformDocument, a JEditorPane, or an InformEditorKit fail to be true.
     * <p>
     * The component that gets acted upon by the action will be the source of
     * the ActionEvent if the source can be narrowed to a JEditorPane type. If
     * the source can't be narrowed, the most recently focused text component is
     * changed. If neither of these are the case, the action cannot be
     * performed.
     */
    public abstract static class InformTextAction extends StyledTextAction {

        /**
         * Creates a new InformTextAction from a string action name.
         * 
         * @param nm
         *            the name of the action
         */
        public InformTextAction(String nm) {
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
        protected final InformDocument getInformDocument(JEditorPane editor) {
            Document doc = editor.getDocument();
            if (doc instanceof InformDocument) {
                return (InformDocument) doc;
            }
            throw new IllegalArgumentException(
                    "document must be InformDocument");
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
        protected final InformEditorKit getInformEditorKit(JEditorPane editor) {
            EditorKit kit = editor.getEditorKit();
            if (kit instanceof InformEditorKit) {
                return (InformEditorKit) kit;
            }
            throw new IllegalArgumentException(
                    "EditorKit must be InformEditorKit");
        }
    }
}