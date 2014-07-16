package it.schillaci.jif.gui;

/*
 * jFrame.java
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

import it.schillaci.jif.configuration.JifConfiguration;
import it.schillaci.jif.configuration.JifConfigurationDAO;
import it.schillaci.jif.configuration.JifConfigurationException;
import it.schillaci.jif.configuration.JifConfigurationObserver;
import it.schillaci.jif.core.Blc;
import it.schillaci.jif.core.Bres;
import it.schillaci.jif.core.Constants;
import it.schillaci.jif.core.GamePathMissingException;
import it.schillaci.jif.core.HighlightText;
import it.schillaci.jif.core.InformAsset;
import it.schillaci.jif.core.Interpreter;
import it.schillaci.jif.core.JifDAO;
import it.schillaci.jif.core.JifDocument;
import it.schillaci.jif.core.JifEditorKit;
import it.schillaci.jif.core.JifFileFilter;
import it.schillaci.jif.core.JifFileName;
import it.schillaci.jif.core.ProgramMissingException;
import it.schillaci.jif.core.Utils;
import it.schillaci.jif.inform.Inform;
import it.schillaci.jif.inform.InformContext;
import it.schillaci.jif.inform.InformDocument;
import it.schillaci.jif.inform.InformEditorKit;
import it.schillaci.jif.inform.InformSyntax;
import it.schillaci.jif.project.JifProject;
import it.schillaci.jif.project.JifProjectDAO;
import it.schillaci.jif.project.JifProjectException;
import it.schillaci.jif.project.JifProjectListCellRenderer;
import it.schillaci.jif.project.JifProjectListModel;
import it.schillaci.jif.project.JifProjectObserver;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/*
 * jFrame.java
 *
 * Created on 28 luglio 2003, 9.58
 */
/**
 * Main Class for Jif application.
 * Jif is a Java Editor for Inform
 * @author Alessandro Schillaci
 * @author Peter Piggott
 * @version 3.6
 */
public class jFrame extends JFrame implements JifConfigurationObserver, JifProjectObserver {

    private static final long serialVersionUID = 7544939067324000307L;

    public jFrame() {

        initComponents();

        initConfiguration();
        initHighlighters();
        initFrame();
        initView();
        
        config.registerObserver(this);
        project.registerObserver(this);
        
//        updateBuffer();
        updateConfiguration();
        updateProject();
    }

    /**
     * This method is called from within the constructor to initialise the
     * configuration. By default the configuration file "jif.cfg" is loaded from
     * the same directory as the Jif jar file. To force another location for the
     * configuration file specify a jif.configuration property like this:<br>
     * <code>java.exe -Duser.language=en -Duser.region=US -Djif.configuration=[NEWPATH] -cp . -jar Jif.jar</code><br>
     * Where <code>[NEWPATH]</code> is the "jif.cfg" configuration file path.
     */
    private void initConfiguration() {
        
        String directory = System.getProperty("user.dir");
        String file;
        
        // Try to locate the configuration file by property
        // or as default use the user.dir property
        if (System.getProperty("jif.configuration") == null) {
            file = directory + File.separator + Constants.configFileName;
        } else {
            System.out.println("Loading configuration file: " + System.getProperty("jif.configuration"));
            file = System.getProperty("jif.configuration");
        }

        // Check whether the jif.cfg exists. If not, create a default one.
        try {
            File configFile = new File(file);
            if (!configFile.exists()) {
                configurationMissingMessage();
                JifConfigurationDAO.create(configFile);
            }

            config = JifConfigurationDAO.load(configFile);
        } catch (JifConfigurationException ex) {
            System.err.println("Configuration error: " + ex.getMessage());
            ex.printStackTrace();
        }

        config.setWorkingDirectory(directory);

    }
    
    private void configurationMissingMessage() {
        JOptionPane.showMessageDialog(
                this,
                java.util.ResourceBundle.getBundle("JIF").getString("JIF_CONFIG_NOT_EXITS"),
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }
    
    private void initFrame() {
        int horizontalBorder = 100;
        int verticalBorder = 70;
        
        // Restore frame dimensions from configuration or set initial dimensions
        if (config.isInitial()) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setSize(screenSize.width-2*horizontalBorder, screenSize.height-2*verticalBorder);
            setLocation(horizontalBorder, verticalBorder);
        } else {
            setSize(config.getFrameWidth(), config.getFrameHeight());
            setLocation(config.getFrameX(), config.getFrameY());
        }
        
        // Set frame icon
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/runInterpreter.png")));
        
    }
    
    /**
     * This method is called from within the constructor to initialise the
     * output pane high-lighters based on the configuration settings
     */
    private void initHighlighters() {
        
        hlighterOutputErrors   = new HighlightText(
                config.getForeground(InformSyntax.Errors));
        hlighterOutputWarnings = new HighlightText(
                config.getForeground(InformSyntax.Warnings));
    }

    /**
     * This method is called from within the constructor to initialise the view
     * to display the last file opened and/or a new file based on the
     * configuration settings.
     */
    private void initView() {
        
        // Open the last file when Jif is loaded
        if (config.isOpenLastFile()) {
            // Check the file still exists
            File test = new File(config.getLastFilePath());
            if (test.exists()) {
                fileOpen(config.getLastFilePath());
            }
        }

        //  Creates a new file when Jif is loaded
        if (config.getCreateNewFile()) {
            fileNew();
        }
        
        // resize the splitpanel
        adjustSplit();
    }
    
    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        filePopupMenu = new javax.swing.JPopupMenu();
        insertNewMenu = new javax.swing.JMenu();
        insertSymbolPopupMenuItem = new javax.swing.JMenuItem();
        insertFilePopupMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        cutPopupMenuItem = new javax.swing.JMenuItem();
        copyPopupMenuItem = new javax.swing.JMenuItem();
        pastePopupMenu = new javax.swing.JMenu();
        clearPopupMenuItem = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        printPopupMenuItem = new javax.swing.JMenuItem();
        closePopupMenuItem = new javax.swing.JMenuItem();
        closeAllPopupMenuItem = new javax.swing.JMenuItem();
        jumpToSourceMenuItem = new javax.swing.JMenuItem();
        projectPopupMenu = new javax.swing.JPopupMenu();
        newProjectPopupMenuItem = new javax.swing.JMenuItem();
        openProjectPopupMenuItem = new javax.swing.JMenuItem();
        saveProjectPopupMenuItem = new javax.swing.JMenuItem();
        closeProjectPopupMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        addNewToProjectPopupMenuItem = new javax.swing.JMenuItem();
        addFileToProjectPopupMenuItem = new javax.swing.JMenuItem();
        removeFromProjectPopupMenuItem = new javax.swing.JMenuItem();
        openSelectedFilesPopupMenuItem = new javax.swing.JMenuItem();
        jSeparator19 = new javax.swing.JSeparator();
        setMainPopupMenuItem = new javax.swing.JMenuItem();
        removeMainPopupMenuItem = new javax.swing.JMenuItem();
        aboutDialog = new JDialog (this, "", true);
        aboutTabbedPane = new javax.swing.JTabbedPane();
        aboutLabel = new javax.swing.JLabel();
        creditsScrollPane = new javax.swing.JScrollPane();
        creditsTextArea = new javax.swing.JTextArea();
        donate = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        aboutControlPanel = new javax.swing.JPanel();
        aboutOKButton = new javax.swing.JButton();
        configDialog = new JDialog (this, "", false);
        configLabelPanel = new javax.swing.JPanel();
        configLabel = new javax.swing.JLabel();
        configScrollPane = new javax.swing.JScrollPane();
        configTextArea = new javax.swing.JTextArea();
        configControlPanel = new javax.swing.JPanel();
        configSaveButton = new javax.swing.JButton();
        configCloseButton = new javax.swing.JButton();
        infoDialog = new JDialog (this, "", false);
        infoScrollPane = new javax.swing.JScrollPane();
        infoTextArea = new javax.swing.JTextArea();
        infoControlPanel = new javax.swing.JPanel();
        infoCloseButton = new javax.swing.JButton();
        optionDialog = new JDialog (this, "", false);
        optionTabbedPane = new javax.swing.JTabbedPane();
        generalPanel = new javax.swing.JPanel();
        openLastFileCheckBox = new javax.swing.JCheckBox();
        createNewFileCheckBox = new javax.swing.JCheckBox();
        mappingLiveCheckBox = new javax.swing.JCheckBox();
        helpedCodeCheckBox = new javax.swing.JCheckBox();
        syntaxCheckBox = new javax.swing.JCheckBox();
        numberLinesCheckBox = new javax.swing.JCheckBox();
        scanProjectFilesCheckBox = new javax.swing.JCheckBox();
        wrapLinesCheckBox = new javax.swing.JCheckBox();
        projectOpenAllFilesCheckBox = new javax.swing.JCheckBox();
        makeResourceCheckBox = new javax.swing.JCheckBox();
        adventInLibCheckBox = new javax.swing.JCheckBox();
        colorFontPanel = new javax.swing.JPanel();
        colorEditorPane = new javax.swing.JEditorPane();
        colorPanel = new javax.swing.JPanel();
        backgroundColorPanel = new javax.swing.JPanel();
        backgroundColorLabel = new javax.swing.JLabel();
        backgroundColorButton = new javax.swing.JButton();
        attributeColorjPanel = new javax.swing.JPanel();
        attributeColorLabel = new javax.swing.JLabel();
        attributeColorButton = new javax.swing.JButton();
        commentColorPanel = new javax.swing.JPanel();
        commentColorLabel = new javax.swing.JLabel();
        commentColorButton = new javax.swing.JButton();
        keywordColorPanel = new javax.swing.JPanel();
        keywordColorLabel = new javax.swing.JLabel();
        keywordColorButton = new javax.swing.JButton();
        normalColorPanel = new javax.swing.JPanel();
        normalColorLabel = new javax.swing.JLabel();
        normalColorButton = new javax.swing.JButton();
        numberColorPanel = new javax.swing.JPanel();
        numberColorLabel = new javax.swing.JLabel();
        numberColorButton = new javax.swing.JButton();
        propertyColorPanel = new javax.swing.JPanel();
        propertyColorLabel = new javax.swing.JLabel();
        propertyColorButton = new javax.swing.JButton();
        stringColorPanel = new javax.swing.JPanel();
        stringColorLabel = new javax.swing.JLabel();
        stringColorButton = new javax.swing.JButton();
        verbColorPanel = new javax.swing.JPanel();
        verbColorLabel = new javax.swing.JLabel();
        verbColorButton = new javax.swing.JButton();
        wordColorPanel = new javax.swing.JPanel();
        wordColorLabel = new javax.swing.JLabel();
        wordColorButton = new javax.swing.JButton();
        fontPanel = new javax.swing.JPanel();
        fontLabel = new javax.swing.JLabel();
        fontNameComboBox = new javax.swing.JComboBox();
        fontNameComboBox.addItem("Arial");
        fontNameComboBox.addItem("Book Antiqua");
        fontNameComboBox.addItem("Comic Sans MS");
        fontNameComboBox.addItem("Courier New");
        fontNameComboBox.addItem("Dialog");
        fontNameComboBox.addItem("Georgia");
        fontNameComboBox.addItem("Lucida Console");
        fontNameComboBox.addItem("Lucida Bright");
        fontNameComboBox.addItem("Lucida Sans");
        fontNameComboBox.addItem("Monospaced");
        fontNameComboBox.addItem("Thaoma");
        fontNameComboBox.addItem("Times New Roman");
        fontNameComboBox.addItem("Verdana");
        fontSizeComboBox = new javax.swing.JComboBox();
        fontSizeComboBox.addItem("9");
        fontSizeComboBox.addItem("10");
        fontSizeComboBox.addItem("11");
        fontSizeComboBox.addItem("12");
        fontSizeComboBox.addItem("13");
        fontSizeComboBox.addItem("14");
        fontSizeComboBox.addItem("15");
        fontSizeComboBox.addItem("16");
        tabSizePanel = new javax.swing.JPanel();
        tabSizeLabel = new javax.swing.JLabel();
        tabSizeTextField = new javax.swing.JTextField();
        defaultColorPanel = new javax.swing.JPanel();
        defaultDarkColorPanel = new javax.swing.JPanel();
        defaultDarkColorLabel = new javax.swing.JLabel();
        defaultDarkColorButton = new javax.swing.JButton();
        defaultLightColorPanel = new javax.swing.JPanel();
        defaultLightColorLabel = new javax.swing.JLabel();
        defaultLightColorButton = new javax.swing.JButton();
        colorHighlightPanel = new javax.swing.JPanel();
        highlightEditorPane = new javax.swing.JEditorPane();
        highlightSelectedPanel = new javax.swing.JPanel();
        highlightSelectedLabel = new javax.swing.JLabel();
        highlightSelectedComboBox = new javax.swing.JComboBox();
        highlightSelectedComboBox.addItem("Bookmark");
        highlightSelectedComboBox.addItem("Bracket");
        highlightSelectedComboBox.addItem("Error");
        highlightSelectedComboBox.addItem("JumpTo");
        highlightSelectedComboBox.addItem("Warning");
        highlightPanel = new javax.swing.JPanel();
        bookmarkColorPanel = new javax.swing.JPanel();
        bookmarkColorLabel = new javax.swing.JLabel();
        bookmarkColorButton = new javax.swing.JButton();
        bracketColorPanel = new javax.swing.JPanel();
        bracketColorLabel = new javax.swing.JLabel();
        bracketColorButton = new javax.swing.JButton();
        errorColorPanel = new javax.swing.JPanel();
        errorColorLabel = new javax.swing.JLabel();
        errorColorButton = new javax.swing.JButton();
        jumpToColorPanel = new javax.swing.JPanel();
        jumpToColorLabel = new javax.swing.JLabel();
        jumpToColorButton = new javax.swing.JButton();
        warningColorPanel = new javax.swing.JPanel();
        warningColorLabel = new javax.swing.JLabel();
        warningColorButton = new javax.swing.JButton();
        defaultHighlightPanel = new javax.swing.JPanel();
        defaultDarkhighlightPanel = new javax.swing.JPanel();
        defaultDarkHighlightLabel = new javax.swing.JLabel();
        defaultDarkHighlightButton = new javax.swing.JButton();
        defaultLightHighlightPanel = new javax.swing.JPanel();
        defaultLightHighlightLabel = new javax.swing.JLabel();
        defaultLightHighlightButton = new javax.swing.JButton();
        complierPanel = new javax.swing.JPanel();
        gamePathPanel = new javax.swing.JPanel();
        gamePathLabel = new javax.swing.JLabel();
        gamePathTextField = new javax.swing.JTextField();
        gamePathButton = new javax.swing.JButton();
        compilerPathPanel = new javax.swing.JPanel();
        compilerPathLabel = new javax.swing.JLabel();
        compilerPathTextField = new javax.swing.JTextField();
        compilerPathButton = new javax.swing.JButton();
        interpreterPathPanel = new javax.swing.JPanel();
        interpreterPathLabel = new javax.swing.JLabel();
        interpreterPathTextField = new javax.swing.JTextField();
        interpreterPathButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        libraryPanel = new javax.swing.JPanel();
        libraryPath0Panel = new javax.swing.JPanel();
        libraryPath0Label = new javax.swing.JLabel();
        libraryPath0TextField = new javax.swing.JTextField();
        libraryPath0Button = new javax.swing.JButton();
        libraryPath1Panel = new javax.swing.JPanel();
        libraryPath1Label = new javax.swing.JLabel();
        libraryPath1TextField = new javax.swing.JTextField();
        libraryPath1Button = new javax.swing.JButton();
        libraryPath2Panel = new javax.swing.JPanel();
        libraryPath2Label = new javax.swing.JLabel();
        libraryPath2TextField = new javax.swing.JTextField();
        libraryPath2Button = new javax.swing.JButton();
        libraryPath3Panel = new javax.swing.JPanel();
        libraryPath3Label = new javax.swing.JLabel();
        libraryPath3TextField = new javax.swing.JTextField();
        libraryPath3Button = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        glulxPanel = new javax.swing.JPanel();
        glulxPathPanel = new javax.swing.JPanel();
        glulxPathLabel = new javax.swing.JLabel();
        glulxPathTextField = new javax.swing.JTextField();
        glulxPathButton = new javax.swing.JButton();
        bresPathPanel = new javax.swing.JPanel();
        bresPathLabel = new javax.swing.JLabel();
        bresPathTextField = new javax.swing.JTextField();
        bresPathButton = new javax.swing.JButton();
        blcPathPanel = new javax.swing.JPanel();
        blcPathLabel = new javax.swing.JLabel();
        blcPathTextField = new javax.swing.JTextField();
        blcPathButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        optionControlPanel = new javax.swing.JPanel();
        optionSaveButton = new javax.swing.JButton();
        optionDefaultButton = new javax.swing.JButton();
        optionCancelButton = new javax.swing.JButton();
        projectSwitchesDialog = new javax.swing.JDialog();
        projectSwitchesPanel = new javax.swing.JPanel();
        projectSwitchesControlPanel = new javax.swing.JPanel();
        projectSwitchesSaveButton = new javax.swing.JButton();
        projectSwitchesCloseButton = new javax.swing.JButton();
        projectPropertiesDialog = new javax.swing.JDialog();
        projectPropertiesScrollPane = new javax.swing.JScrollPane();
        projectPropertiesTextArea = new javax.swing.JTextArea();
        projectPropertiesControlPanel = new javax.swing.JPanel();
        projectPropertiesSaveButton = new javax.swing.JButton();
        projectPropertiesCloseButton = new javax.swing.JButton();
        replaceDialog = new javax.swing.JDialog();
        replacePanel = new javax.swing.JPanel();
        replaceFindLabel = new javax.swing.JLabel();
        replaceFindTextField = new javax.swing.JTextField();
        replaceReplaceLabel = new javax.swing.JLabel();
        replaceReplaceTextField = new javax.swing.JTextField();
        replaceControlPanel = new javax.swing.JPanel();
        replaceFindButton = new javax.swing.JButton();
        replaceReplaceButton = new javax.swing.JButton();
        replaceAllButton = new javax.swing.JButton();
        replaceCloseButton = new javax.swing.JButton();
        switchesDialog = new JDialog (this, "", false);
        switchesPanel = new javax.swing.JPanel();
        switchesUpperPanel = new javax.swing.JPanel();
        switchesLowerPanel = new javax.swing.JPanel();
        switchesControlPanel = new javax.swing.JPanel();
        switchesSaveButton = new javax.swing.JButton();
        switchesCloseButton = new javax.swing.JButton();
        symbolDialog = new javax.swing.JDialog();
        symbolScrollPane = new javax.swing.JScrollPane();
        symbolList = new javax.swing.JList();
        textDialog = new JDialog (this, "", false);
        textLabel = new javax.swing.JLabel();
        textScrollPane = new javax.swing.JScrollPane();
        textTextArea = new javax.swing.JTextArea();
        textControlPanel = new javax.swing.JPanel();
        textCloseButton = new javax.swing.JButton();
        tutorialDialog = new JDialog (this, "", false);
        tutorialLabel = new javax.swing.JLabel();
        tutorialScrollPane = new javax.swing.JScrollPane();
        tutorialEditorPane = new javax.swing.JEditorPane();
        tutorialControlPanel = new javax.swing.JPanel();
        tutorialOKButton = new javax.swing.JButton();
        tutorialPrintButton = new javax.swing.JButton();
        toolbarPanel = new javax.swing.JPanel();
        newButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        saveAllButton = new javax.swing.JButton();
        saveAsButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        closeAllButton = new javax.swing.JButton();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        commentButton = new javax.swing.JButton();
        uncommentButton = new javax.swing.JButton();
        tabLeftButton = new javax.swing.JButton();
        tabRightButton = new javax.swing.JButton();
        bracketCheckButton = new javax.swing.JButton();
        buildAllButton = new javax.swing.JButton();
        runButton = new javax.swing.JButton();
        insertSymbolButton = new javax.swing.JButton();
        interpreterButton = new javax.swing.JButton();
        switchManagerButton = new javax.swing.JButton();
        settingsButton = new javax.swing.JButton();
        findTextField = new javax.swing.JTextField();
        findButton = new javax.swing.JButton();
        replaceButton = new javax.swing.JButton();
        mainSplitPane = new javax.swing.JSplitPane();
        upperSplitPane = new javax.swing.JSplitPane();
        leftTabbedPane = new javax.swing.JTabbedPane();
        treePanel = new javax.swing.JPanel();
        treeScrollPane = new javax.swing.JScrollPane();

        JifTreeCellRenderer renderer = new JifTreeCellRenderer(this);
        renderer.setLeafIcon(new ImageIcon(getClass().getResource("/images/TREE_objects.png")));
        codeTree = new javax.swing.JTree();
        // Create initial root and inform assets nodes for code tree
        top = new InformTreeNode("Inspect");
        globalTree = new InformTreeNode("Globals    ");
        top.add(globalTree);
        constantTree = new InformTreeNode("Constants    ");
        top.add(constantTree);
        objectTree = new InformTreeNode("Objects    ");
        top.add(objectTree);
        functionTree = new InformTreeNode("Functions    ");
        top.add(functionTree);
        classTree = new InformTreeNode("Classes    ");
        top.add(classTree);

        //Create a tree that allows one selection at a time.
        treeModel = new DefaultTreeModel(top);
        codeTree = new JTree(treeModel);

        codeTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        codeTree.setCellRenderer(renderer);
        projectPanel = new javax.swing.JPanel();
        projectScrollPane = new javax.swing.JScrollPane();
        projectModel = new JifProjectListModel();
        projectList = new javax.swing.JList();
        projectList.setCellRenderer(new JifProjectListCellRenderer(this));
        projectList.setModel(projectModel);
        projectList.addMouseListener(popupListenerProject);
        mainFileLabel = new javax.swing.JLabel();
        searchPanel = new javax.swing.JPanel();
        searchProjectPanel = new javax.swing.JPanel();
        searchProjectTextField = new javax.swing.JTextField();
        searchProjectButton = new javax.swing.JButton();
        definitionPanel = new javax.swing.JPanel();
        definitionTextField = new javax.swing.JTextField();
        definitionButton = new javax.swing.JButton();
        filePanel = new javax.swing.JPanel();
        outputScrollPane = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();
        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        saveAllMenuItem = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JSeparator();
        closeMenuItem = new javax.swing.JMenuItem();
        closeAllMenuItem = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        recentFilesMenu = new javax.swing.JMenu();
        clearRecentFilesMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        printMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JSeparator();
        searchMenuItem = new javax.swing.JMenuItem();
        searchAllMenuItem = new javax.swing.JMenuItem();
        replaceMenuItem = new javax.swing.JMenuItem();
        selectAllMenuItem = new javax.swing.JMenuItem();
        clearAllMenuItem = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JSeparator();
        commentSelectionMenuItem = new javax.swing.JMenuItem();
        uncommentSelectionMenuItem = new javax.swing.JMenuItem();
        tabRightMenuItem = new javax.swing.JMenuItem();
        tabLeftMenuItem = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JSeparator();
        insertFileMenuItem = new javax.swing.JMenuItem();
        insertSymbolMenuItem = new javax.swing.JMenuItem();
        setBookmarkMenuItem = new javax.swing.JMenuItem();
        nextBookmarkMenuItem = new javax.swing.JMenuItem();
        extractStringsMenuItem = new javax.swing.JMenuItem();
        translateMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        outputCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        toolbarCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        treeCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        toggleFullscreenCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        projectMenu = new javax.swing.JMenu();
        newProjectMenuItem = new javax.swing.JMenuItem();
        openProjectMenuItem = new javax.swing.JMenuItem();
        saveProjectMenuItem = new javax.swing.JMenuItem();
        closeProjectMenuItem = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JSeparator();
        addNewToProjectMenuItem = new javax.swing.JMenuItem();
        addFileToProjectMenuItem = new javax.swing.JMenuItem();
        removeFromProjectMenuItem = new javax.swing.JMenuItem();
        projectPropertiesMenuItem = new javax.swing.JMenuItem();
        projectSwitchesMenuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        lastProjectMenuItem = new javax.swing.JMenuItem();
        modeMenu = new javax.swing.JMenu();
        informModeCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        glulxModeCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        buildMenu = new javax.swing.JMenu();
        buildAllMenuItem = new javax.swing.JMenuItem();
        switchesMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        runMenuItem = new javax.swing.JMenuItem();
        glulxMenu = new javax.swing.JMenu();
        buildAllGlulxMenuItem = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JSeparator();
        makeResourceMenuItem = new javax.swing.JMenuItem();
        compileMenuItem = new javax.swing.JMenuItem();
        makeBlbMenuItem = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JSeparator();
        runUlxMenuItem = new javax.swing.JMenuItem();
        runBlbMenuItem = new javax.swing.JMenuItem();
        optionsMenu = new javax.swing.JMenu();
        configFileMenuItem = new javax.swing.JMenuItem();
        settingsMenuItem = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JSeparator();
        garbageCollectionMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        readMeMenuItem = new javax.swing.JMenuItem();
        changelogMenuItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        aboutMenuItem = new javax.swing.JMenuItem();

        filePopupMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("JIF"); // NOI18N
        insertNewMenu.setText(bundle.getString("POPUPMENU_MENU_NEW")); // NOI18N
        insertNewMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        filePopupMenu.add(insertNewMenu);

        insertSymbolPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        insertSymbolPopupMenuItem.setText(bundle.getString("JFRAME_INSERT_SYMBOL")); // NOI18N
        insertSymbolPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertSymbolActionPerformed(evt);
            }
        });
        filePopupMenu.add(insertSymbolPopupMenuItem);

        insertFilePopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        insertFilePopupMenuItem.setText(bundle.getString("JFRAME_INSERT_FROM_FILE")); // NOI18N
        insertFilePopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertFileActionPerformed(evt);
            }
        });
        filePopupMenu.add(insertFilePopupMenuItem);
        filePopupMenu.add(jSeparator3);

        cutPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cutPopupMenuItem.setText(bundle.getString("JFRAME_EDIT_CUT")); // NOI18N
        cutPopupMenuItem.setActionCommand("KEY JFRAME_EDIT_CUT : RB JIF");
        cutPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutActionPerformed(evt);
            }
        });
        filePopupMenu.add(cutPopupMenuItem);

        copyPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        copyPopupMenuItem.setText(bundle.getString("POPUPMENU_MENUITEM_COPY")); // NOI18N
        copyPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyActionPerformed(evt);
            }
        });
        filePopupMenu.add(copyPopupMenuItem);

        pastePopupMenu.setText(bundle.getString("POPUPMENU_MENU_PASTE")); // NOI18N
        pastePopupMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        filePopupMenu.add(pastePopupMenu);

        clearPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        clearPopupMenuItem.setText(bundle.getString("POPUPMENU_MENUITEM_CLEAR")); // NOI18N
        clearPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearPopupActionPerformed(evt);
            }
        });
        filePopupMenu.add(clearPopupMenuItem);
        filePopupMenu.add(jSeparator13);

        printPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        printPopupMenuItem.setText(bundle.getString("MENUITEM_PRINT")); // NOI18N
        printPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printPopupMenuItemActionPerformed(evt);
            }
        });
        filePopupMenu.add(printPopupMenuItem);

        closePopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        closePopupMenuItem.setText(bundle.getString("MENUITEM_CLOSE")); // NOI18N
        closePopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });
        filePopupMenu.add(closePopupMenuItem);

        closeAllPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        closeAllPopupMenuItem.setText(bundle.getString("MENUITEM_CLOSEALL")); // NOI18N
        closeAllPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllActionPerformed(evt);
            }
        });
        filePopupMenu.add(closeAllPopupMenuItem);

        jumpToSourceMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jumpToSourceMenuItem.setText(bundle.getString("MENU_JUMP_TO_SOURCE")); // NOI18N
        jumpToSourceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jumpToSourceActionPerformed(evt);
            }
        });
        filePopupMenu.add(jumpToSourceMenuItem);

        projectPopupMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        newProjectPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        newProjectPopupMenuItem.setText(bundle.getString("PROJECT_NEW_PROJECT")); // NOI18N
        newProjectPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectActionPerformed(evt);
            }
        });
        projectPopupMenu.add(newProjectPopupMenuItem);

        openProjectPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        openProjectPopupMenuItem.setText(bundle.getString("PROJECT_OPEN_PROJECT")); // NOI18N
        openProjectPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openProjectActionPerformed(evt);
            }
        });
        projectPopupMenu.add(openProjectPopupMenuItem);

        saveProjectPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        saveProjectPopupMenuItem.setText(bundle.getString("PROJECT_SAVE_PROJECT")); // NOI18N
        saveProjectPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProjectActionPerformed(evt);
            }
        });
        projectPopupMenu.add(saveProjectPopupMenuItem);

        closeProjectPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        closeProjectPopupMenuItem.setText(bundle.getString("PROJECT_CLOSE_PROJECT")); // NOI18N
        closeProjectPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeProjectActionPerformed(evt);
            }
        });
        projectPopupMenu.add(closeProjectPopupMenuItem);
        projectPopupMenu.add(jSeparator6);

        addNewToProjectPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        addNewToProjectPopupMenuItem.setText(bundle.getString("PROJECT_ADD_NEWFILE_TO_PROJECT")); // NOI18N
        addNewToProjectPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewToProjectActionPerformed(evt);
            }
        });
        projectPopupMenu.add(addNewToProjectPopupMenuItem);

        addFileToProjectPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        addFileToProjectPopupMenuItem.setText(bundle.getString("PROJECT_ADD_FILE_TO_PROJECT")); // NOI18N
        addFileToProjectPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFileToProjectActionPerformed(evt);
            }
        });
        projectPopupMenu.add(addFileToProjectPopupMenuItem);

        removeFromProjectPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        removeFromProjectPopupMenuItem.setText(bundle.getString("PROJECT_POPUP_REMOVE")); // NOI18N
        removeFromProjectPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFromProjectActionPerformed(evt);
            }
        });
        projectPopupMenu.add(removeFromProjectPopupMenuItem);

        openSelectedFilesPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        openSelectedFilesPopupMenuItem.setText(bundle.getString("PROJECT_OPEN_SELECTED_FILES")); // NOI18N
        openSelectedFilesPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSelectedFilesActionPerformed(evt);
            }
        });
        projectPopupMenu.add(openSelectedFilesPopupMenuItem);
        projectPopupMenu.add(jSeparator19);

        setMainPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        setMainPopupMenuItem.setText(bundle.getString("PROJECT_SET_AS_MAIN_FILE")); // NOI18N
        setMainPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setMainActionPerformed(evt);
            }
        });
        projectPopupMenu.add(setMainPopupMenuItem);

        removeMainPopupMenuItem.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        removeMainPopupMenuItem.setText(bundle.getString("PROJECT_REMOVE_MAIN_FILE")); // NOI18N
        removeMainPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeMainActionPerformed(evt);
            }
        });
        projectPopupMenu.add(removeMainPopupMenuItem);

        aboutDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        aboutDialog.setTitle(bundle.getString("JFRAME_ABOUT_JIF")); // NOI18N
        aboutDialog.setFocusCycleRoot(false);
        aboutDialog.setModal(true);
        aboutDialog.setResizable(false);

        aboutLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/about.png"))); // NOI18N
        aboutTabbedPane.addTab("About", aboutLabel);

        creditsScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));

        creditsTextArea.setEditable(false);
        creditsTextArea.setColumns(20);
        creditsTextArea.setFont(new java.awt.Font("MonoSpaced", 0, 11)); // NOI18N
        creditsTextArea.setRows(5);
        creditsTextArea.setText("JIF, a java editor for Inform\nby Alessandro Schillaci\nhttp://www.slade.altervista.org/\n\nDevelopment: \n- Alessandro Schillaci\n- Peter F. Piggott\n- Luis Fernandez\n\nContributors:\nPaolo Lucchesi\nVincenzo Scarpa\nBaltasar Garcia Perez-Schofield\nChristof Menear\nGiles Boutel\nJavier San José\nDavid Moreno\nEric Forgeot\nMax Kalus\nAdrien Saurat\nAlex V Flinsch\nDaryl McCullough\nGiancarlo Niccolai\nIgnazio di Napoli\nJoerg Rosenbauer\nMatteo De Simone\nTommaso Caldarola");
        creditsTextArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 5, 3, 3));
        creditsScrollPane.setViewportView(creditsTextArea);

        aboutTabbedPane.addTab("Credits", creditsScrollPane);

        donate.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setEnabled(false);

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jTextArea1.setRows(5);
        jTextArea1.setText("Jif is an open source software.\nIf you want to support it, please \nconsider to donate by PayPal \nto silver.slade@tiscali.it\n\nThanks");
        jScrollPane1.setViewportView(jTextArea1);

        donate.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/donate.png"))); // NOI18N
        jPanel2.add(jLabel4);

        donate.add(jPanel2, java.awt.BorderLayout.SOUTH);

        aboutTabbedPane.addTab("Donate", donate);

        aboutDialog.getContentPane().add(aboutTabbedPane, java.awt.BorderLayout.NORTH);

        aboutOKButton.setText(bundle.getString("MESSAGE_OK")); // NOI18N
        aboutOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutOKActionPerformed(evt);
            }
        });
        aboutControlPanel.add(aboutOKButton);

        aboutDialog.getContentPane().add(aboutControlPanel, java.awt.BorderLayout.SOUTH);

        aboutDialog.getAccessibleContext().setAccessibleParent(this);

        configDialog.setTitle(bundle.getString("JDIALOG_CONFIGFILES_TITLE")); // NOI18N
        configDialog.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N

        configLabelPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        configLabelPanel.setLayout(new java.awt.GridLayout(1, 0));

        configLabel.setText("configuration");
        configLabelPanel.add(configLabel);

        configDialog.getContentPane().add(configLabelPanel, java.awt.BorderLayout.NORTH);

        configTextArea.setTabSize(4);
        configTextArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5));
        configScrollPane.setViewportView(configTextArea);

        configDialog.getContentPane().add(configScrollPane, java.awt.BorderLayout.CENTER);

        configControlPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        configSaveButton.setText(bundle.getString("MESSAGE_SAVE")); // NOI18N
        configSaveButton.setMaximumSize(new java.awt.Dimension(73, 25));
        configSaveButton.setMinimumSize(new java.awt.Dimension(73, 25));
        configSaveButton.setPreferredSize(new java.awt.Dimension(73, 25));
        configSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configSaveActionPerformed(evt);
            }
        });
        configControlPanel.add(configSaveButton);

        configCloseButton.setText(bundle.getString("MESSAGE_CLOSE")); // NOI18N
        configCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configCloseActionPerformed(evt);
            }
        });
        configControlPanel.add(configCloseButton);

        configDialog.getContentPane().add(configControlPanel, java.awt.BorderLayout.SOUTH);

        infoDialog.setModal(true);

        infoScrollPane.setAutoscrolls(true);

        infoTextArea.setBackground(new java.awt.Color(204, 204, 204));
        infoTextArea.setEditable(false);
        infoTextArea.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        infoTextArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5));
        infoTextArea.setMaximumSize(new java.awt.Dimension(0, 0));
        infoTextArea.setMinimumSize(new java.awt.Dimension(0, 0));
        infoTextArea.setPreferredSize(new java.awt.Dimension(0, 0));
        infoScrollPane.setViewportView(infoTextArea);

        infoDialog.getContentPane().add(infoScrollPane, java.awt.BorderLayout.CENTER);

        infoCloseButton.setText(bundle.getString("MESSAGE_CLOSE")); // NOI18N
        infoCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoCloseActionPerformed(evt);
            }
        });
        infoControlPanel.add(infoCloseButton);

        infoDialog.getContentPane().add(infoControlPanel, java.awt.BorderLayout.SOUTH);

        optionDialog.setTitle(bundle.getString("JFRAME_SETTING")); // NOI18N
        optionDialog.setModal(true);

        optionTabbedPane.setMinimumSize(new java.awt.Dimension(100, 100));
        optionTabbedPane.setPreferredSize(new java.awt.Dimension(500, 450));

        generalPanel.setMinimumSize(new java.awt.Dimension(277, 800));
        generalPanel.setPreferredSize(new java.awt.Dimension(192, 138));
        generalPanel.setLayout(new java.awt.GridBagLayout());

        openLastFileCheckBox.setText(bundle.getString("PROJECT_OPEN_LAST_OPEN_FILE")); // NOI18N
        openLastFileCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openLastFileCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        generalPanel.add(openLastFileCheckBox, gridBagConstraints);

        createNewFileCheckBox.setText(bundle.getString("OPTION_CREATE_A_NEW_FILE")); // NOI18N
        createNewFileCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewFileCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        generalPanel.add(createNewFileCheckBox, gridBagConstraints);

        mappingLiveCheckBox.setText(bundle.getString("CHECKBOX_MAPPINGLIVE")); // NOI18N
        mappingLiveCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mappingLiveCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        generalPanel.add(mappingLiveCheckBox, gridBagConstraints);

        helpedCodeCheckBox.setSelected(true);
        helpedCodeCheckBox.setText(bundle.getString("CHECKBOX_HELPEDCODE")); // NOI18N
        helpedCodeCheckBox.setToolTipText(bundle.getString("CHECKBOX_HELPEDCODE_TOOLTIP")); // NOI18N
        helpedCodeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpedCodeCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        generalPanel.add(helpedCodeCheckBox, gridBagConstraints);

        syntaxCheckBox.setSelected(true);
        syntaxCheckBox.setText(bundle.getString("CHECKBOX_SYNTAX")); // NOI18N
        syntaxCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                syntaxCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        generalPanel.add(syntaxCheckBox, gridBagConstraints);

        numberLinesCheckBox.setText(bundle.getString("CHECKBOX_NUMBEROFLINES")); // NOI18N
        numberLinesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numberLinesCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        generalPanel.add(numberLinesCheckBox, gridBagConstraints);

        scanProjectFilesCheckBox.setText(bundle.getString("CHECKBOX_SCAN_PROJECT")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        generalPanel.add(scanProjectFilesCheckBox, gridBagConstraints);

        wrapLinesCheckBox.setText("Wrap Lines");
        wrapLinesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wrapLinesCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        generalPanel.add(wrapLinesCheckBox, gridBagConstraints);

        projectOpenAllFilesCheckBox.setText(bundle.getString("PROJECT_OPEN_ALL_FILES")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        generalPanel.add(projectOpenAllFilesCheckBox, gridBagConstraints);

        makeResourceCheckBox.setText(bundle.getString("GLULX_MAKE_RESOURCE_WHEN_BUILD_ALL")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        generalPanel.add(makeResourceCheckBox, gridBagConstraints);

        adventInLibCheckBox.setText(bundle.getString("JOPTION_ADVENT_IN_LIB")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        generalPanel.add(adventInLibCheckBox, gridBagConstraints);

        optionTabbedPane.addTab("General", generalPanel);

        colorFontPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Color and Font"));
        colorFontPanel.setMinimumSize(new java.awt.Dimension(277, 260));
        colorFontPanel.setPreferredSize(new java.awt.Dimension(277, 260));
        colorFontPanel.setLayout(new java.awt.GridBagLayout());

        colorEditorPane.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        colorEditorPane.setEditable(false);
        colorEditorPane.setMaximumSize(new java.awt.Dimension(50, 50));
        colorEditorPane.setMinimumSize(new java.awt.Dimension(50, 50));
        colorEditorPane.setPreferredSize(new java.awt.Dimension(50, 180));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        colorFontPanel.add(colorEditorPane, gridBagConstraints);

        colorPanel.setLayout(new java.awt.GridBagLayout());

        backgroundColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        backgroundColorLabel.setText("Background");
        backgroundColorPanel.add(backgroundColorLabel);

        backgroundColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        backgroundColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        backgroundColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        backgroundColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backgroundColorActionPerformed(evt);
            }
        });
        backgroundColorPanel.add(backgroundColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        colorPanel.add(backgroundColorPanel, gridBagConstraints);

        attributeColorjPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        attributeColorLabel.setText("Attribute");
        attributeColorjPanel.add(attributeColorLabel);

        attributeColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        attributeColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        attributeColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        attributeColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attributeColorActionPerformed(evt);
            }
        });
        attributeColorjPanel.add(attributeColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        colorPanel.add(attributeColorjPanel, gridBagConstraints);

        commentColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        commentColorLabel.setText("Comment");
        commentColorPanel.add(commentColorLabel);

        commentColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        commentColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        commentColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        commentColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commentColorActionPerformed(evt);
            }
        });
        commentColorPanel.add(commentColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        colorPanel.add(commentColorPanel, gridBagConstraints);

        keywordColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        keywordColorLabel.setText("Keyword");
        keywordColorPanel.add(keywordColorLabel);

        keywordColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        keywordColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        keywordColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        keywordColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keywordColorActionPerformed(evt);
            }
        });
        keywordColorPanel.add(keywordColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        colorPanel.add(keywordColorPanel, gridBagConstraints);

        normalColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        normalColorLabel.setText("Normal");
        normalColorPanel.add(normalColorLabel);

        normalColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        normalColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        normalColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        normalColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normalColorActionPerformed(evt);
            }
        });
        normalColorPanel.add(normalColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        colorPanel.add(normalColorPanel, gridBagConstraints);

        numberColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        numberColorLabel.setText("Number");
        numberColorPanel.add(numberColorLabel);

        numberColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        numberColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        numberColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        numberColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numberColorActionPerformed(evt);
            }
        });
        numberColorPanel.add(numberColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        colorPanel.add(numberColorPanel, gridBagConstraints);

        propertyColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        propertyColorLabel.setText("Property");
        propertyColorPanel.add(propertyColorLabel);

        propertyColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        propertyColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        propertyColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        propertyColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propertyColorActionPerformed(evt);
            }
        });
        propertyColorPanel.add(propertyColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        colorPanel.add(propertyColorPanel, gridBagConstraints);

        stringColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        stringColorLabel.setText("String");
        stringColorPanel.add(stringColorLabel);

        stringColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        stringColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        stringColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        stringColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stringColorActionPerformed(evt);
            }
        });
        stringColorPanel.add(stringColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        colorPanel.add(stringColorPanel, gridBagConstraints);

        verbColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        verbColorLabel.setText("Verb");
        verbColorPanel.add(verbColorLabel);

        verbColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        verbColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        verbColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        verbColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verbColorActionPerformed(evt);
            }
        });
        verbColorPanel.add(verbColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        colorPanel.add(verbColorPanel, gridBagConstraints);

        wordColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        wordColorLabel.setText("Word");
        wordColorPanel.add(wordColorLabel);

        wordColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        wordColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        wordColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        wordColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wordColorActionPerformed(evt);
            }
        });
        wordColorPanel.add(wordColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        colorPanel.add(wordColorPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        colorFontPanel.add(colorPanel, gridBagConstraints);

        fontLabel.setText("Font");
        fontPanel.add(fontLabel);

        fontNameComboBox.setMaximumRowCount(10);
        fontNameComboBox.setMinimumSize(new java.awt.Dimension(100, 21));
        fontNameComboBox.setPreferredSize(new java.awt.Dimension(120, 21));
        fontNameComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontNameComboBoxActionPerformed(evt);
            }
        });
        fontPanel.add(fontNameComboBox);

        fontSizeComboBox.setMinimumSize(new java.awt.Dimension(100, 21));
        fontSizeComboBox.setPreferredSize(new java.awt.Dimension(120, 21));
        fontSizeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontSizeComboBoxActionPerformed(evt);
            }
        });
        fontPanel.add(fontSizeComboBox);

        tabSizeLabel.setText("TAB size");
        tabSizePanel.add(tabSizeLabel);

        tabSizeTextField.setColumns(2);
        tabSizeTextField.setText("4");
        tabSizeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabSizeTextFieldActionPerformed(evt);
            }
        });
        tabSizePanel.add(tabSizeTextField);

        fontPanel.add(tabSizePanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        colorFontPanel.add(fontPanel, gridBagConstraints);

        defaultDarkColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        defaultDarkColorLabel.setText("Black setting");
        defaultDarkColorPanel.add(defaultDarkColorLabel);

        defaultDarkColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        defaultDarkColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        defaultDarkColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        defaultDarkColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultDarkActionPerformed(evt);
            }
        });
        defaultDarkColorPanel.add(defaultDarkColorButton);

        defaultColorPanel.add(defaultDarkColorPanel);

        defaultLightColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        defaultLightColorLabel.setText("White setting");
        defaultLightColorPanel.add(defaultLightColorLabel);

        defaultLightColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        defaultLightColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        defaultLightColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        defaultLightColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultLightActionPerformed(evt);
            }
        });
        defaultLightColorPanel.add(defaultLightColorButton);

        defaultColorPanel.add(defaultLightColorPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        colorFontPanel.add(defaultColorPanel, gridBagConstraints);

        optionTabbedPane.addTab("Colors", colorFontPanel);

        colorHighlightPanel.setLayout(new java.awt.GridBagLayout());

        highlightEditorPane.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        highlightEditorPane.setEditable(false);
        highlightEditorPane.setMaximumSize(new java.awt.Dimension(50, 50));
        highlightEditorPane.setMinimumSize(new java.awt.Dimension(50, 50));
        highlightEditorPane.setPreferredSize(new java.awt.Dimension(50, 180));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        colorHighlightPanel.add(highlightEditorPane, gridBagConstraints);

        highlightSelectedLabel.setText("Selected highlighting");
        highlightSelectedPanel.add(highlightSelectedLabel);

        highlightSelectedComboBox.setMaximumRowCount(10);
        highlightSelectedComboBox.setMinimumSize(new java.awt.Dimension(100, 21));
        highlightSelectedComboBox.setPreferredSize(new java.awt.Dimension(120, 21));
        highlightSelectedComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highlightSelectedComboBoxActionPerformed(evt);
            }
        });
        highlightSelectedPanel.add(highlightSelectedComboBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        colorHighlightPanel.add(highlightSelectedPanel, gridBagConstraints);

        highlightPanel.setLayout(new java.awt.GridBagLayout());

        bookmarkColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        bookmarkColorLabel.setText("Bookmarks");
        bookmarkColorPanel.add(bookmarkColorLabel);

        bookmarkColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        bookmarkColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        bookmarkColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        bookmarkColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookmarkColorButtonattributeColorActionPerformed(evt);
            }
        });
        bookmarkColorPanel.add(bookmarkColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        highlightPanel.add(bookmarkColorPanel, gridBagConstraints);

        bracketColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        bracketColorLabel.setText("Brackets");
        bracketColorPanel.add(bracketColorLabel);

        bracketColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        bracketColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        bracketColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        bracketColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bracketColorButtonbackgroundColorActionPerformed(evt);
            }
        });
        bracketColorPanel.add(bracketColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        highlightPanel.add(bracketColorPanel, gridBagConstraints);

        errorColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        errorColorLabel.setText("Errors");
        errorColorPanel.add(errorColorLabel);

        errorColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        errorColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        errorColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        errorColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errorColorButtoncommentColorActionPerformed(evt);
            }
        });
        errorColorPanel.add(errorColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        highlightPanel.add(errorColorPanel, gridBagConstraints);

        jumpToColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jumpToColorLabel.setText("Jump To");
        jumpToColorPanel.add(jumpToColorLabel);

        jumpToColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        jumpToColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        jumpToColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        jumpToColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jumpToColorButtonkeywordColorActionPerformed(evt);
            }
        });
        jumpToColorPanel.add(jumpToColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        highlightPanel.add(jumpToColorPanel, gridBagConstraints);

        warningColorPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        warningColorLabel.setText("Warnings");
        warningColorPanel.add(warningColorLabel);

        warningColorButton.setMaximumSize(new java.awt.Dimension(35, 15));
        warningColorButton.setMinimumSize(new java.awt.Dimension(35, 15));
        warningColorButton.setPreferredSize(new java.awt.Dimension(35, 15));
        warningColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                warningColorButtonnormalColorActionPerformed(evt);
            }
        });
        warningColorPanel.add(warningColorButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        highlightPanel.add(warningColorPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        colorHighlightPanel.add(highlightPanel, gridBagConstraints);

        defaultDarkhighlightPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        defaultDarkHighlightLabel.setText("Black setting");
        defaultDarkhighlightPanel.add(defaultDarkHighlightLabel);

        defaultDarkHighlightButton.setMaximumSize(new java.awt.Dimension(35, 15));
        defaultDarkHighlightButton.setMinimumSize(new java.awt.Dimension(35, 15));
        defaultDarkHighlightButton.setPreferredSize(new java.awt.Dimension(35, 15));
        defaultDarkHighlightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultDarkHighlightButtondefaultDarkActionPerformed(evt);
            }
        });
        defaultDarkhighlightPanel.add(defaultDarkHighlightButton);

        defaultHighlightPanel.add(defaultDarkhighlightPanel);

        defaultLightHighlightPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        defaultLightHighlightLabel.setText("White setting");
        defaultLightHighlightPanel.add(defaultLightHighlightLabel);

        defaultLightHighlightButton.setMaximumSize(new java.awt.Dimension(35, 15));
        defaultLightHighlightButton.setMinimumSize(new java.awt.Dimension(35, 15));
        defaultLightHighlightButton.setPreferredSize(new java.awt.Dimension(35, 15));
        defaultLightHighlightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultLightHighlightButtondefaultLightActionPerformed(evt);
            }
        });
        defaultLightHighlightPanel.add(defaultLightHighlightButton);

        defaultHighlightPanel.add(defaultLightHighlightPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        colorHighlightPanel.add(defaultHighlightPanel, gridBagConstraints);

        optionTabbedPane.addTab("Highlights", colorHighlightPanel);

        complierPanel.setPreferredSize(new java.awt.Dimension(469, 99));
        complierPanel.setLayout(new java.awt.GridBagLayout());

        gamePathPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        gamePathLabel.setText(bundle.getString("JDIALOG_CONFIGPATH_ATPATH")); // NOI18N
        gamePathPanel.add(gamePathLabel);

        gamePathTextField.setPreferredSize(new java.awt.Dimension(280, 21));
        gamePathTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                gamePathTextFieldFocusLost(evt);
            }
        });
        gamePathPanel.add(gamePathTextField);

        gamePathButton.setText(bundle.getString("MESSAGE_BROWSE")); // NOI18N
        gamePathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gamePathActionPerformed(evt);
            }
        });
        gamePathPanel.add(gamePathButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        complierPanel.add(gamePathPanel, gridBagConstraints);

        compilerPathPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        compilerPathLabel.setText(bundle.getString("JDIALOG_CONFIGPATH_COMPILERPATH")); // NOI18N
        compilerPathPanel.add(compilerPathLabel);

        compilerPathTextField.setPreferredSize(new java.awt.Dimension(280, 21));
        compilerPathTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                compilerPathTextFieldFocusLost(evt);
            }
        });
        compilerPathPanel.add(compilerPathTextField);

        compilerPathButton.setText(bundle.getString("MESSAGE_BROWSE")); // NOI18N
        compilerPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compilerPathActionPerformed(evt);
            }
        });
        compilerPathPanel.add(compilerPathButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        complierPanel.add(compilerPathPanel, gridBagConstraints);

        interpreterPathPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        interpreterPathLabel.setText(bundle.getString("JDIALOG_CONFIGPATH_INTERPRETERPATH")); // NOI18N
        interpreterPathPanel.add(interpreterPathLabel);

        interpreterPathTextField.setPreferredSize(new java.awt.Dimension(280, 21));
        interpreterPathTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                interpreterPathTextFieldFocusLost(evt);
            }
        });
        interpreterPathPanel.add(interpreterPathTextField);

        interpreterPathButton.setText(bundle.getString("MESSAGE_BROWSE")); // NOI18N
        interpreterPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interpreterPathActionPerformed(evt);
            }
        });
        interpreterPathPanel.add(interpreterPathButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        complierPanel.add(interpreterPathPanel, gridBagConstraints);

        jLabel1.setText("Note: Use absolute paths or relative paths from Jif.jar position.");
        jPanel1.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        complierPanel.add(jPanel1, gridBagConstraints);

        optionTabbedPane.addTab("Compiler Path", complierPanel);

        libraryPanel.setPreferredSize(new java.awt.Dimension(1889, 33));
        libraryPanel.setLayout(new java.awt.GridBagLayout());

        libraryPath0Panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        libraryPath0Label.setText(bundle.getString("JDIALOG_CONFIGPATH_LIBRARY")); // NOI18N
        libraryPath0Panel.add(libraryPath0Label);

        libraryPath0TextField.setPreferredSize(new java.awt.Dimension(280, 21));
        libraryPath0TextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                libraryPath0TextFieldFocusLost(evt);
            }
        });
        libraryPath0Panel.add(libraryPath0TextField);

        libraryPath0Button.setText(bundle.getString("MESSAGE_BROWSE")); // NOI18N
        libraryPath0Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                libraryPath0ActionPerformed(evt);
            }
        });
        libraryPath0Panel.add(libraryPath0Button);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        libraryPanel.add(libraryPath0Panel, gridBagConstraints);

        libraryPath1Panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        libraryPath1Label.setText(bundle.getString("JDIALOG_CONFIGPATH_LIBRARY_SECONDARY1")); // NOI18N
        libraryPath1Panel.add(libraryPath1Label);

        libraryPath1TextField.setPreferredSize(new java.awt.Dimension(280, 21));
        libraryPath1TextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                libraryPath1TextFieldFocusLost(evt);
            }
        });
        libraryPath1Panel.add(libraryPath1TextField);

        libraryPath1Button.setText(bundle.getString("MESSAGE_BROWSE")); // NOI18N
        libraryPath1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                libraryPath1ActionPerformed(evt);
            }
        });
        libraryPath1Panel.add(libraryPath1Button);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        libraryPanel.add(libraryPath1Panel, gridBagConstraints);

        libraryPath2Panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        libraryPath2Label.setText(bundle.getString("JDIALOG_CONFIGPATH_LIBRARY_SECONDARY2")); // NOI18N
        libraryPath2Panel.add(libraryPath2Label);

        libraryPath2TextField.setPreferredSize(new java.awt.Dimension(280, 21));
        libraryPath2TextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                libraryPath2TextFieldFocusLost(evt);
            }
        });
        libraryPath2Panel.add(libraryPath2TextField);

        libraryPath2Button.setText(bundle.getString("MESSAGE_BROWSE")); // NOI18N
        libraryPath2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                libraryPath2ActionPerformed(evt);
            }
        });
        libraryPath2Panel.add(libraryPath2Button);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        libraryPanel.add(libraryPath2Panel, gridBagConstraints);

        libraryPath3Panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        libraryPath3Label.setText(bundle.getString("JDIALOG_CONFIGPATH_LIBRARY_SECONDARY3")); // NOI18N
        libraryPath3Panel.add(libraryPath3Label);

        libraryPath3TextField.setPreferredSize(new java.awt.Dimension(280, 21));
        libraryPath3TextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                libraryPath3TextFieldFocusLost(evt);
            }
        });
        libraryPath3Panel.add(libraryPath3TextField);

        libraryPath3Button.setText(bundle.getString("MESSAGE_BROWSE")); // NOI18N
        libraryPath3Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                libraryPath3ActionPerformed(evt);
            }
        });
        libraryPath3Panel.add(libraryPath3Button);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        libraryPanel.add(libraryPath3Panel, gridBagConstraints);

        jLabel2.setText("Note: Use absolute paths or relative paths from Jif.jar position.");
        jPanel3.add(jLabel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        libraryPanel.add(jPanel3, gridBagConstraints);

        optionTabbedPane.addTab("Library Path", libraryPanel);

        glulxPanel.setPreferredSize(new java.awt.Dimension(1373, 33));
        glulxPanel.setLayout(new java.awt.GridBagLayout());

        glulxPathPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        glulxPathLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        glulxPathLabel.setText(bundle.getString("JDIALOG_CONFIGPATH_GLULXINTERPRETERPATH")); // NOI18N
        glulxPathPanel.add(glulxPathLabel);

        glulxPathTextField.setPreferredSize(new java.awt.Dimension(280, 21));
        glulxPathTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                glulxPathTextFieldFocusLost(evt);
            }
        });
        glulxPathPanel.add(glulxPathTextField);

        glulxPathButton.setText(bundle.getString("MESSAGE_BROWSE")); // NOI18N
        glulxPathButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        glulxPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glulxPathActionPerformed(evt);
            }
        });
        glulxPathPanel.add(glulxPathButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        glulxPanel.add(glulxPathPanel, gridBagConstraints);

        bresPathPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        bresPathLabel.setText(bundle.getString("GLULX_BRES_LOCATION")); // NOI18N
        bresPathPanel.add(bresPathLabel);

        bresPathTextField.setPreferredSize(new java.awt.Dimension(280, 21));
        bresPathTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                bresPathTextFieldFocusLost(evt);
            }
        });
        bresPathPanel.add(bresPathTextField);

        bresPathButton.setText(bundle.getString("MESSAGE_BROWSE")); // NOI18N
        bresPathButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        bresPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bresPathActionPerformed(evt);
            }
        });
        bresPathPanel.add(bresPathButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        glulxPanel.add(bresPathPanel, gridBagConstraints);

        blcPathPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        blcPathLabel.setText(bundle.getString("GLULX_BLC_LOCATION")); // NOI18N
        blcPathPanel.add(blcPathLabel);

        blcPathTextField.setPreferredSize(new java.awt.Dimension(280, 21));
        blcPathTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                blcPathTextFieldFocusLost(evt);
            }
        });
        blcPathPanel.add(blcPathTextField);

        blcPathButton.setText(bundle.getString("MESSAGE_BROWSE")); // NOI18N
        blcPathButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        blcPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                blcPathActionPerformed(evt);
            }
        });
        blcPathPanel.add(blcPathButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        glulxPanel.add(blcPathPanel, gridBagConstraints);

        jLabel3.setText("Note: Use absolute paths or relative paths from Jif.jar position.");
        jPanel4.add(jLabel3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        glulxPanel.add(jPanel4, gridBagConstraints);

        optionTabbedPane.addTab("Glulx Path", glulxPanel);

        optionDialog.getContentPane().add(optionTabbedPane, java.awt.BorderLayout.CENTER);

        optionSaveButton.setText(bundle.getString("MESSAGE_SAVE")); // NOI18N
        optionSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionSaveActionPerformed(evt);
            }
        });
        optionControlPanel.add(optionSaveButton);

        optionDefaultButton.setText(bundle.getString("MESSAGE_DEFAULT")); // NOI18N
        optionDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionDefaultActionPerformed(evt);
            }
        });
        optionControlPanel.add(optionDefaultButton);

        optionCancelButton.setText(bundle.getString("MESSAGE_CANCEL")); // NOI18N
        optionCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optionCancelActionPerformed(evt);
            }
        });
        optionControlPanel.add(optionCancelButton);

        optionDialog.getContentPane().add(optionControlPanel, java.awt.BorderLayout.SOUTH);

        projectSwitchesPanel.setLayout(new java.awt.GridLayout(0, 4));
        projectSwitchesDialog.getContentPane().add(projectSwitchesPanel, java.awt.BorderLayout.CENTER);

        projectSwitchesSaveButton.setText(bundle.getString("MESSAGE_SAVE")); // NOI18N
        projectSwitchesSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectSwitchesSaveActionPerformed(evt);
            }
        });
        projectSwitchesControlPanel.add(projectSwitchesSaveButton);

        projectSwitchesCloseButton.setText(bundle.getString("MESSAGE_CLOSE")); // NOI18N
        projectSwitchesCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectSwitchesCloseActionPerformed(evt);
            }
        });
        projectSwitchesControlPanel.add(projectSwitchesCloseButton);

        projectSwitchesDialog.getContentPane().add(projectSwitchesControlPanel, java.awt.BorderLayout.SOUTH);

        projectPropertiesDialog.setTitle("Project Properties");
        projectPropertiesDialog.setModal(true);

        projectPropertiesTextArea.setColumns(20);
        projectPropertiesTextArea.setRows(5);
        projectPropertiesTextArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5));
        projectPropertiesScrollPane.setViewportView(projectPropertiesTextArea);

        projectPropertiesDialog.getContentPane().add(projectPropertiesScrollPane, java.awt.BorderLayout.CENTER);

        projectPropertiesSaveButton.setText(bundle.getString("MESSAGE_SAVE")); // NOI18N
        projectPropertiesSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectPropertiesSaveActionPerformed(evt);
            }
        });
        projectPropertiesControlPanel.add(projectPropertiesSaveButton);

        projectPropertiesCloseButton.setText(bundle.getString("MESSAGE_CLOSE")); // NOI18N
        projectPropertiesCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectPropertiesCloseActionPerformed(evt);
            }
        });
        projectPropertiesControlPanel.add(projectPropertiesCloseButton);

        projectPropertiesDialog.getContentPane().add(projectPropertiesControlPanel, java.awt.BorderLayout.SOUTH);

        replaceDialog.setTitle(bundle.getString("JDIALOGREPLACE_TITLE")); // NOI18N
        replaceDialog.setModal(true);
        replaceDialog.getContentPane().setLayout(new javax.swing.BoxLayout(replaceDialog.getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        replaceFindLabel.setText(bundle.getString("JDIALOGREPLACE_FIND_LABEL")); // NOI18N
        replaceFindLabel.setPreferredSize(new java.awt.Dimension(41, 17));
        replacePanel.add(replaceFindLabel);

        replaceFindTextField.setMaximumSize(new java.awt.Dimension(111, 20));
        replaceFindTextField.setMinimumSize(new java.awt.Dimension(111, 20));
        replaceFindTextField.setPreferredSize(new java.awt.Dimension(111, 20));
        replacePanel.add(replaceFindTextField);

        replaceReplaceLabel.setText(bundle.getString("JDIALOGREPLACE_REPLACE_LABEL")); // NOI18N
        replacePanel.add(replaceReplaceLabel);

        replaceReplaceTextField.setMaximumSize(new java.awt.Dimension(111, 20));
        replaceReplaceTextField.setMinimumSize(new java.awt.Dimension(111, 20));
        replaceReplaceTextField.setPreferredSize(new java.awt.Dimension(111, 20));
        replacePanel.add(replaceReplaceTextField);

        replaceDialog.getContentPane().add(replacePanel);

        replaceFindButton.setFont(new java.awt.Font("MS Sans Serif", 0, 12)); // NOI18N
        replaceFindButton.setText(bundle.getString("JDIALOGREPLACE_BUTTON_FIND")); // NOI18N
        replaceFindButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceFindActionPerformed(evt);
            }
        });
        replaceControlPanel.add(replaceFindButton);

        replaceReplaceButton.setFont(new java.awt.Font("MS Sans Serif", 0, 12)); // NOI18N
        replaceReplaceButton.setText(bundle.getString("JDIALOGREPLACE_BUTTON_REPLACE")); // NOI18N
        replaceReplaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceReplaceActionPerformed(evt);
            }
        });
        replaceControlPanel.add(replaceReplaceButton);

        replaceAllButton.setFont(new java.awt.Font("MS Sans Serif", 0, 12)); // NOI18N
        replaceAllButton.setText(bundle.getString("JDIALOGREPLACE_BUTTON_REPLACE_ALL")); // NOI18N
        replaceAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceAllActionPerformed(evt);
            }
        });
        replaceControlPanel.add(replaceAllButton);

        replaceCloseButton.setText(bundle.getString("MESSAGE_CLOSE")); // NOI18N
        replaceCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceCloseActionPerformed(evt);
            }
        });
        replaceControlPanel.add(replaceCloseButton);

        replaceDialog.getContentPane().add(replaceControlPanel);

        switchesDialog.setTitle(bundle.getString("JDIALOG_SWITCHES_TITLE")); // NOI18N
        switchesDialog.setModal(true);

        switchesPanel.setLayout(new java.awt.GridLayout(2, 0));

        switchesUpperPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        switchesUpperPanel.setLayout(new java.awt.GridLayout(4, 6));
        switchesPanel.add(switchesUpperPanel);

        switchesLowerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        switchesLowerPanel.setLayout(new java.awt.GridLayout(0, 3));
        switchesPanel.add(switchesLowerPanel);

        switchesDialog.getContentPane().add(switchesPanel, java.awt.BorderLayout.CENTER);

        switchesSaveButton.setText(bundle.getString("MESSAGE_SAVE")); // NOI18N
        switchesSaveButton.setMaximumSize(new java.awt.Dimension(73, 25));
        switchesSaveButton.setMinimumSize(new java.awt.Dimension(73, 25));
        switchesSaveButton.setPreferredSize(new java.awt.Dimension(73, 25));
        switchesSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchesSaveActionPerformed(evt);
            }
        });
        switchesControlPanel.add(switchesSaveButton);

        switchesCloseButton.setText(bundle.getString("MESSAGE_CLOSE")); // NOI18N
        switchesCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchesCloseActionPerformed(evt);
            }
        });
        switchesControlPanel.add(switchesCloseButton);

        switchesDialog.getContentPane().add(switchesControlPanel, java.awt.BorderLayout.SOUTH);

        symbolDialog.setBackground(java.awt.Color.lightGray);
        symbolDialog.setUndecorated(true);

        symbolScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        symbolScrollPane.setDoubleBuffered(true);
        symbolScrollPane.setMinimumSize(new java.awt.Dimension(0, 0));
        symbolScrollPane.setPreferredSize(new java.awt.Dimension(230, 80));

        symbolList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        symbolList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                symbolListKeyPressed(evt);
            }
        });
        symbolList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                symbolListMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                symbolListMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                symbolListMouseExited(evt);
            }
        });
        symbolScrollPane.setViewportView(symbolList);

        symbolDialog.getContentPane().add(symbolScrollPane, java.awt.BorderLayout.WEST);

        textLabel.setText("jLabel5");
        textDialog.getContentPane().add(textLabel, java.awt.BorderLayout.NORTH);

        textTextArea.setEditable(false);
        textTextArea.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        textTextArea.setTabSize(4);
        textScrollPane.setViewportView(textTextArea);

        textDialog.getContentPane().add(textScrollPane, java.awt.BorderLayout.CENTER);

        textCloseButton.setText(bundle.getString("MESSAGE_OK")); // NOI18N
        textCloseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textCloseActionPerformed(evt);
            }
        });
        textControlPanel.add(textCloseButton);

        textDialog.getContentPane().add(textControlPanel, java.awt.BorderLayout.SOUTH);

        tutorialLabel.setText("jLabel5");
        tutorialDialog.getContentPane().add(tutorialLabel, java.awt.BorderLayout.NORTH);

        tutorialEditorPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5));
        tutorialEditorPane.setEditable(false);
        tutorialScrollPane.setViewportView(tutorialEditorPane);

        tutorialDialog.getContentPane().add(tutorialScrollPane, java.awt.BorderLayout.CENTER);

        tutorialOKButton.setText(bundle.getString("MESSAGE_OK")); // NOI18N
        tutorialOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tutorialOKActionPerformed(evt);
            }
        });
        tutorialControlPanel.add(tutorialOKButton);

        tutorialPrintButton.setText(bundle.getString("MENUITEM_PRINT")); // NOI18N
        tutorialPrintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tutorialPrintActionPerformed(evt);
            }
        });
        tutorialControlPanel.add(tutorialPrintButton);

        tutorialDialog.getContentPane().add(tutorialControlPanel, java.awt.BorderLayout.SOUTH);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(getJifVersion());
        setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jFrameResized(evt);
            }
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                jFrameMoved(evt);
            }
        });

        toolbarPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        toolbarPanel.setLayout(new java.awt.BorderLayout());

        jToolBarCommon.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jToolBarCommon.setFloatable(false);
        jToolBarCommon.setToolTipText("Jif Toolbar");
        jToolBarCommon.setPreferredSize(new java.awt.Dimension(400, 34));

        newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filenew.png"))); // NOI18N
        newButton.setToolTipText(bundle.getString("MENUITEM_NEW_TOOLTIP")); // NOI18N
        newButton.setBorderPainted(false);
        newButton.setMaximumSize(new java.awt.Dimension(29, 29));
        newButton.setMinimumSize(new java.awt.Dimension(29, 29));
        newButton.setPreferredSize(new java.awt.Dimension(29, 29));
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newActionPerformed(evt);
            }
        });
        newButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(newButton);

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fileopen.png"))); // NOI18N
        openButton.setToolTipText(bundle.getString("MENUITEM_OPEN")); // NOI18N
        openButton.setBorderPainted(false);
        openButton.setMaximumSize(new java.awt.Dimension(29, 29));
        openButton.setMinimumSize(new java.awt.Dimension(29, 29));
        openButton.setPreferredSize(new java.awt.Dimension(29, 29));
        openButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openActionPerformed(evt);
            }
        });
        jToolBarCommon.add(openButton);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filesave.png"))); // NOI18N
        saveButton.setToolTipText(bundle.getString("MENUITEM_SAVE")); // NOI18N
        saveButton.setBorderPainted(false);
        saveButton.setMaximumSize(new java.awt.Dimension(29, 29));
        saveButton.setMinimumSize(new java.awt.Dimension(29, 29));
        saveButton.setPreferredSize(new java.awt.Dimension(29, 29));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(saveButton);

        saveAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filesaveall.png"))); // NOI18N
        saveAllButton.setToolTipText(bundle.getString("MENUITEM_SAVEALL")); // NOI18N
        saveAllButton.setBorderPainted(false);
        saveAllButton.setMaximumSize(new java.awt.Dimension(29, 29));
        saveAllButton.setMinimumSize(new java.awt.Dimension(29, 29));
        saveAllButton.setPreferredSize(new java.awt.Dimension(29, 29));
        saveAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAllActionPerformed(evt);
            }
        });
        saveAllButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(saveAllButton);

        saveAsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filesaveas.png"))); // NOI18N
        saveAsButton.setToolTipText(bundle.getString("MENUITEM_SAVEAS")); // NOI18N
        saveAsButton.setBorderPainted(false);
        saveAsButton.setMaximumSize(new java.awt.Dimension(29, 29));
        saveAsButton.setMinimumSize(new java.awt.Dimension(29, 29));
        saveAsButton.setPreferredSize(new java.awt.Dimension(29, 29));
        saveAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsActionPerformed(evt);
            }
        });
        saveAsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(saveAsButton);

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fileclose.png"))); // NOI18N
        closeButton.setToolTipText(bundle.getString("MENUITEM_CLOSE")); // NOI18N
        closeButton.setBorderPainted(false);
        closeButton.setMaximumSize(new java.awt.Dimension(29, 29));
        closeButton.setMinimumSize(new java.awt.Dimension(29, 29));
        closeButton.setPreferredSize(new java.awt.Dimension(29, 29));
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(closeButton);

        closeAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filecloseAll.png"))); // NOI18N
        closeAllButton.setToolTipText(bundle.getString("MENUITEM_CLOSEALL")); // NOI18N
        closeAllButton.setBorderPainted(false);
        closeAllButton.setMaximumSize(new java.awt.Dimension(29, 29));
        closeAllButton.setMinimumSize(new java.awt.Dimension(29, 29));
        closeAllButton.setPreferredSize(new java.awt.Dimension(29, 29));
        closeAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllActionPerformed(evt);
            }
        });
        closeAllButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(closeAllButton);

        undoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/undo.png"))); // NOI18N
        undoButton.setToolTipText(bundle.getString("JFRAME_UNDO")); // NOI18N
        undoButton.setBorderPainted(false);
        undoButton.setMaximumSize(new java.awt.Dimension(29, 29));
        undoButton.setMinimumSize(new java.awt.Dimension(29, 29));
        undoButton.setPreferredSize(new java.awt.Dimension(29, 29));
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoActionPerformed(evt);
            }
        });
        undoButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(undoButton);

        redoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/redo.png"))); // NOI18N
        redoButton.setToolTipText(bundle.getString("JFRAME_REDO")); // NOI18N
        redoButton.setBorderPainted(false);
        redoButton.setMaximumSize(new java.awt.Dimension(29, 29));
        redoButton.setMinimumSize(new java.awt.Dimension(29, 29));
        redoButton.setPreferredSize(new java.awt.Dimension(29, 29));
        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoActionPerformed(evt);
            }
        });
        redoButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(redoButton);

        commentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/comment.png"))); // NOI18N
        commentButton.setToolTipText(bundle.getString("JFRAME_COMMENT_SELECTION")); // NOI18N
        commentButton.setBorderPainted(false);
        commentButton.setMaximumSize(new java.awt.Dimension(29, 29));
        commentButton.setMinimumSize(new java.awt.Dimension(29, 29));
        commentButton.setPreferredSize(new java.awt.Dimension(29, 29));
        commentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commentSelectionActionPerformed(evt);
            }
        });
        commentButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(commentButton);

        uncommentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/uncomment.png"))); // NOI18N
        uncommentButton.setToolTipText(bundle.getString("JFRAME_UNCOMMENT_SELECTION")); // NOI18N
        uncommentButton.setBorderPainted(false);
        uncommentButton.setMaximumSize(new java.awt.Dimension(29, 29));
        uncommentButton.setMinimumSize(new java.awt.Dimension(29, 29));
        uncommentButton.setPreferredSize(new java.awt.Dimension(29, 29));
        uncommentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uncommentSelectionActionPerformed(evt);
            }
        });
        uncommentButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(uncommentButton);

        tabLeftButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/leftIndent.png"))); // NOI18N
        tabLeftButton.setToolTipText(bundle.getString("JFRAME_LEFTTAB_SELECTION")); // NOI18N
        tabLeftButton.setBorderPainted(false);
        tabLeftButton.setMaximumSize(new java.awt.Dimension(29, 29));
        tabLeftButton.setMinimumSize(new java.awt.Dimension(29, 29));
        tabLeftButton.setPreferredSize(new java.awt.Dimension(29, 29));
        tabLeftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabLeftActionPerformed(evt);
            }
        });
        tabLeftButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(tabLeftButton);

        tabRightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/rightIndent.png"))); // NOI18N
        tabRightButton.setToolTipText(bundle.getString("JFRAME_RIGHTTAB_SELECTION")); // NOI18N
        tabRightButton.setBorderPainted(false);
        tabRightButton.setMaximumSize(new java.awt.Dimension(29, 29));
        tabRightButton.setMinimumSize(new java.awt.Dimension(29, 29));
        tabRightButton.setPreferredSize(new java.awt.Dimension(29, 29));
        tabRightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabRightActionPerformed(evt);
            }
        });
        tabRightButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(tabRightButton);

        bracketCheckButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/check.png"))); // NOI18N
        bracketCheckButton.setToolTipText(bundle.getString("JFRAME_CHECK_BRACKETS")); // NOI18N
        bracketCheckButton.setBorderPainted(false);
        bracketCheckButton.setMaximumSize(new java.awt.Dimension(29, 29));
        bracketCheckButton.setMinimumSize(new java.awt.Dimension(29, 29));
        bracketCheckButton.setPreferredSize(new java.awt.Dimension(29, 29));
        bracketCheckButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bracketCheckActionPerformed(evt);
            }
        });
        bracketCheckButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(bracketCheckButton);

        buildAllButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/compfile.png"))); // NOI18N
        buildAllButton.setToolTipText(bundle.getString("MENUITEM_BUILDALL")); // NOI18N
        buildAllButton.setBorderPainted(false);
        buildAllButton.setMaximumSize(new java.awt.Dimension(29, 29));
        buildAllButton.setMinimumSize(new java.awt.Dimension(29, 29));
        buildAllButton.setPreferredSize(new java.awt.Dimension(29, 29));
        buildAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildAllActionPerformed(evt);
            }
        });
        buildAllButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(buildAllButton);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/launch.png"))); // NOI18N
        runButton.setToolTipText(bundle.getString("MENUITEM_RUN")); // NOI18N
        runButton.setBorderPainted(false);
        runButton.setMaximumSize(new java.awt.Dimension(29, 29));
        runButton.setMinimumSize(new java.awt.Dimension(29, 29));
        runButton.setPreferredSize(new java.awt.Dimension(29, 29));
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runActionPerformed(evt);
            }
        });
        runButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(runButton);

        insertSymbolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/insertSymbol.png"))); // NOI18N
        insertSymbolButton.setToolTipText(bundle.getString("JFRAME_INSERT_SYMBOL")); // NOI18N
        insertSymbolButton.setBorderPainted(false);
        insertSymbolButton.setMaximumSize(new java.awt.Dimension(29, 29));
        insertSymbolButton.setMinimumSize(new java.awt.Dimension(29, 29));
        insertSymbolButton.setPreferredSize(new java.awt.Dimension(29, 29));
        insertSymbolButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertSymbolActionPerformed(evt);
            }
        });
        insertSymbolButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(insertSymbolButton);

        interpreterButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/runInterpreter.png"))); // NOI18N
        interpreterButton.setToolTipText(bundle.getString("JFRAME_RUN_INTERPRETER")); // NOI18N
        interpreterButton.setBorderPainted(false);
        interpreterButton.setMaximumSize(new java.awt.Dimension(29, 29));
        interpreterButton.setMinimumSize(new java.awt.Dimension(29, 29));
        interpreterButton.setPreferredSize(new java.awt.Dimension(29, 29));
        interpreterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interpreterActionPerformed(evt);
            }
        });
        interpreterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(interpreterButton);

        switchManagerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/edit.png"))); // NOI18N
        switchManagerButton.setToolTipText(bundle.getString("MENUITEM_SWITCHES_TOOLTIP")); // NOI18N
        switchManagerButton.setBorderPainted(false);
        switchManagerButton.setMaximumSize(new java.awt.Dimension(29, 29));
        switchManagerButton.setMinimumSize(new java.awt.Dimension(29, 29));
        switchManagerButton.setPreferredSize(new java.awt.Dimension(29, 29));
        switchManagerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchManagerActionPerformed(evt);
            }
        });
        switchManagerButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(switchManagerButton);

        settingsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/configure.png"))); // NOI18N
        settingsButton.setToolTipText(bundle.getString("JFRAME_SETTING")); // NOI18N
        settingsButton.setBorderPainted(false);
        settingsButton.setMaximumSize(new java.awt.Dimension(29, 29));
        settingsButton.setMinimumSize(new java.awt.Dimension(29, 29));
        settingsButton.setPreferredSize(new java.awt.Dimension(29, 29));
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsActionPerformed(evt);
            }
        });
        settingsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(settingsButton);

        findTextField.setColumns(15);
        findTextField.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        findTextField.setToolTipText(bundle.getString("JTOOLBAR_SEARCH")); // NOI18N
        findTextField.setMaximumSize(new java.awt.Dimension(111, 20));
        findTextField.setMinimumSize(new java.awt.Dimension(10, 22));
        findTextField.setPreferredSize(new java.awt.Dimension(111, 29));
        jToolBarCommon.add(findTextField);

        findButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filefind.png"))); // NOI18N
        findButton.setToolTipText(bundle.getString("JFRAME_SEARCH_BUTTON")); // NOI18N
        findButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        findButton.setBorderPainted(false);
        findButton.setMaximumSize(new java.awt.Dimension(29, 29));
        findButton.setMinimumSize(new java.awt.Dimension(29, 29));
        findButton.setPreferredSize(new java.awt.Dimension(29, 29));
        findButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findActionPerformed(evt);
            }
        });
        findButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(findButton);

        replaceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        replaceButton.setToolTipText(bundle.getString("MENUITEM_REPLACE")); // NOI18N
        replaceButton.setBorderPainted(false);
        replaceButton.setMaximumSize(new java.awt.Dimension(29, 29));
        replaceButton.setMinimumSize(new java.awt.Dimension(29, 29));
        replaceButton.setPreferredSize(new java.awt.Dimension(29, 29));
        replaceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceActionPerformed(evt);
            }
        });
        replaceButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        jToolBarCommon.add(replaceButton);

        toolbarPanel.add(jToolBarCommon, java.awt.BorderLayout.NORTH);

        getContentPane().add(toolbarPanel, java.awt.BorderLayout.NORTH);

        mainSplitPane.setDividerLocation(450);
        mainSplitPane.setDividerSize(3);
        mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDoubleBuffered(true);

        upperSplitPane.setDividerSize(3);
        upperSplitPane.setDoubleBuffered(true);
        upperSplitPane.setMinimumSize(new java.awt.Dimension(180, 328));
        upperSplitPane.setPreferredSize(new java.awt.Dimension(180, 328));

        leftTabbedPane.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        treePanel.setLayout(new javax.swing.BoxLayout(treePanel, javax.swing.BoxLayout.Y_AXIS));

        treeScrollPane.setBorder(null);
        treeScrollPane.setDoubleBuffered(true);
        treeScrollPane.setMinimumSize(new java.awt.Dimension(150, 200));
        treeScrollPane.setPreferredSize(new java.awt.Dimension(150, 300));

        codeTree.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        codeTree.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        codeTree.setMaximumSize(new java.awt.Dimension(0, 0));
        codeTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                codeTreeMouseEntered(evt);
            }
        });
        codeTree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                codeTreeTreeExpanded(evt);
            }
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
            }
        });
        codeTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                codeTreeValueChanged(evt);
            }
        });
        treeScrollPane.setViewportView(codeTree);

        treePanel.add(treeScrollPane);

        leftTabbedPane.addTab(bundle.getString("JFRAME_TREE"), treePanel); // NOI18N

        projectPanel.setLayout(new javax.swing.BoxLayout(projectPanel, javax.swing.BoxLayout.Y_AXIS));

        projectScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Project", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11))); // NOI18N
        projectScrollPane.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        projectScrollPane.setPreferredSize(new java.awt.Dimension(90, 131));

        projectList.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        projectList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                projectListMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                projectListMouseEntered(evt);
            }
        });
        projectScrollPane.setViewportView(projectList);

        projectPanel.add(projectScrollPane);

        mainFileLabel.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        mainFileLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        mainFileLabel.setText("Main:");
        mainFileLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        projectPanel.add(mainFileLabel);

        leftTabbedPane.addTab(bundle.getString("PROJECT_PROJECT"), projectPanel); // NOI18N

        searchPanel.setMaximumSize(new java.awt.Dimension(1800, 220));
        searchPanel.setMinimumSize(new java.awt.Dimension(180, 220));
        searchPanel.setPreferredSize(new java.awt.Dimension(180, 220));
        searchPanel.setLayout(new javax.swing.BoxLayout(searchPanel, javax.swing.BoxLayout.Y_AXIS));

        searchProjectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search all project files", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11))); // NOI18N
        searchProjectPanel.setMaximumSize(new java.awt.Dimension(180, 55));
        searchProjectPanel.setMinimumSize(new java.awt.Dimension(180, 55));
        searchProjectPanel.setPreferredSize(new java.awt.Dimension(180, 55));
        searchProjectPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        searchProjectTextField.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        searchProjectTextField.setToolTipText(bundle.getString("JTOOLBAR_SEARCH")); // NOI18N
        searchProjectTextField.setMaximumSize(new java.awt.Dimension(250, 20));
        searchProjectTextField.setMinimumSize(new java.awt.Dimension(130, 22));
        searchProjectTextField.setPreferredSize(new java.awt.Dimension(120, 20));
        searchProjectPanel.add(searchProjectTextField);

        searchProjectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fileprojectfind.png"))); // NOI18N
        searchProjectButton.setToolTipText(bundle.getString("JFRAME_SEARCHALL_BUTTON")); // NOI18N
        searchProjectButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        searchProjectButton.setBorderPainted(false);
        searchProjectButton.setMaximumSize(new java.awt.Dimension(29, 29));
        searchProjectButton.setMinimumSize(new java.awt.Dimension(29, 29));
        searchProjectButton.setPreferredSize(new java.awt.Dimension(29, 29));
        searchProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchProjectActionPerformed(evt);
            }
        });
        searchProjectButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        searchProjectPanel.add(searchProjectButton);

        searchPanel.add(searchProjectPanel);

        definitionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Search for Definition", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 11))); // NOI18N
        definitionPanel.setMaximumSize(new java.awt.Dimension(180, 55));
        definitionPanel.setMinimumSize(new java.awt.Dimension(180, 55));
        definitionPanel.setPreferredSize(new java.awt.Dimension(180, 55));
        definitionPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));

        definitionTextField.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        definitionTextField.setMaximumSize(new java.awt.Dimension(150, 20));
        definitionTextField.setMinimumSize(new java.awt.Dimension(30, 20));
        definitionTextField.setPreferredSize(new java.awt.Dimension(120, 20));
        definitionPanel.add(definitionTextField);

        definitionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filefind.png"))); // NOI18N
        definitionButton.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        definitionButton.setBorderPainted(false);
        definitionButton.setMaximumSize(new java.awt.Dimension(29, 29));
        definitionButton.setMinimumSize(new java.awt.Dimension(29, 29));
        definitionButton.setPreferredSize(new java.awt.Dimension(29, 29));
        definitionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                definitionActionPerformed(evt);
            }
        });
        definitionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButtonMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButtonMouseExited(evt);
            }
        });
        definitionPanel.add(definitionButton);

        searchPanel.add(definitionPanel);

        leftTabbedPane.addTab(bundle.getString("JFRAME_SEARCH"), searchPanel); // NOI18N

        upperSplitPane.setLeftComponent(leftTabbedPane);

        filePanel.setLayout(new java.awt.BorderLayout());

        fileTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        fileTabbedPane.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        fileTabbedPane.setMinimumSize(new java.awt.Dimension(350, 350));
        fileTabbedPane.setPreferredSize(new java.awt.Dimension(700, 450));
        fileTabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileTabbedPaneMouseClicked(evt);
            }
        });
        fileTabbedPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                fileTabbedPaneComponentShown(evt);
            }
        });
        filePanel.add(fileTabbedPane, java.awt.BorderLayout.CENTER);

        upperSplitPane.setRightComponent(filePanel);

        mainSplitPane.setTopComponent(upperSplitPane);

        outputTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        outputTabbedPane.setAutoscrolls(true);
        outputTabbedPane.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        outputTabbedPane.setMinimumSize(new java.awt.Dimension(31, 100));
        outputTabbedPane.setPreferredSize(new java.awt.Dimension(30, 150));

        outputScrollPane.setAutoscrolls(true);

        outputTextArea.setEditable(false);
        outputTextArea.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        outputTextArea.setTabSize(4);
        outputTextArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 5, 2, 5));
        outputTextArea.setMinimumSize(new java.awt.Dimension(0, 45));
        outputTextArea.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                outputTextAreaMouseMoved(evt);
            }
        });
        outputTextArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                outputTextAreaMouseClicked(evt);
            }
        });
        outputScrollPane.setViewportView(outputTextArea);

        outputTabbedPane.addTab(bundle.getString("JFRAME_OUTPUT"), outputScrollPane); // NOI18N
        outputScrollPane.getAccessibleContext().setAccessibleParent(outputTabbedPane);

        mainSplitPane.setBottomComponent(outputTabbedPane);

        getContentPane().add(mainSplitPane, java.awt.BorderLayout.CENTER);

        mainMenuBar.setBorder(null);
        mainMenuBar.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        fileMenu.setText(bundle.getString("MENU_FILE")); // NOI18N
        fileMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newMenuItem.setText(bundle.getString("MENUITEM_NEW")); // NOI18N
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newActionPerformed(evt);
            }
        });
        fileMenu.add(newMenuItem);

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        openMenuItem.setText(bundle.getString("MENUITEM_OPEN")); // NOI18N
        openMenuItem.setToolTipText(bundle.getString("MENUITEM_OPEN_TOOLTIP")); // NOI18N
        openMenuItem.setName("Open"); // NOI18N
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);
        fileMenu.add(jSeparator8);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        saveMenuItem.setText(bundle.getString("MENUITEM_SAVE")); // NOI18N
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        saveAsMenuItem.setText(bundle.getString("MENUITEM_SAVEAS")); // NOI18N
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMenuItem);

        saveAllMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        saveAllMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        saveAllMenuItem.setText(bundle.getString("MENUITEM_SAVEALL")); // NOI18N
        saveAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAllActionPerformed(evt);
            }
        });
        fileMenu.add(saveAllMenuItem);
        fileMenu.add(jSeparator10);

        closeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        closeMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        closeMenuItem.setText(bundle.getString("MENUITEM_CLOSE")); // NOI18N
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });
        fileMenu.add(closeMenuItem);

        closeAllMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        closeAllMenuItem.setText(bundle.getString("MENUITEM_CLOSEALL")); // NOI18N
        closeAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAllActionPerformed(evt);
            }
        });
        fileMenu.add(closeAllMenuItem);
        fileMenu.add(jSeparator9);

        recentFilesMenu.setText(bundle.getString("MENUITEM_RECENTFILES")); // NOI18N
        recentFilesMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        fileMenu.add(recentFilesMenu);

        clearRecentFilesMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        clearRecentFilesMenuItem.setText(bundle.getString("MENUITEM_CLEARRECENTFILES")); // NOI18N
        clearRecentFilesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearRecentFilesActionPerformed(evt);
            }
        });
        fileMenu.add(clearRecentFilesMenuItem);
        fileMenu.add(jSeparator1);

        printMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        printMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        printMenuItem.setText(bundle.getString("MENUITEM_PRINT")); // NOI18N
        printMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printActionPerformed(evt);
            }
        });
        fileMenu.add(printMenuItem);
        fileMenu.add(jSeparator4);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        exitMenuItem.setText(bundle.getString("MENUITEM_EXIT")); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        mainMenuBar.add(fileMenu);

        editMenu.setText(bundle.getString("MENU_EDIT")); // NOI18N
        editMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        cutMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        cutMenuItem.setText(bundle.getString("JFRAME_EDIT_CUT")); // NOI18N
        editMenu.add(cutMenuItem);

        copyMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        copyMenuItem.setText(bundle.getString("MENUITEM_COPY")); // NOI18N
        copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyActionPerformed(evt);
            }
        });
        editMenu.add(copyMenuItem);

        pasteMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        pasteMenuItem.setText(bundle.getString("JFRAME_EDIT_PASTE")); // NOI18N
        editMenu.add(pasteMenuItem);
        editMenu.add(jSeparator11);

        searchMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, 0));
        searchMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        searchMenuItem.setText(bundle.getString("JFRAME_SEARCH")); // NOI18N
        searchMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchActionPerformed(evt);
            }
        });
        editMenu.add(searchMenuItem);

        searchAllMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F3, java.awt.event.InputEvent.CTRL_MASK));
        searchAllMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        searchAllMenuItem.setText(bundle.getString("JFRAME_SEARCHALL")); // NOI18N
        searchAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchAllctionPerformed(evt);
            }
        });
        editMenu.add(searchAllMenuItem);

        replaceMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        replaceMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        replaceMenuItem.setText(bundle.getString("MENUITEM_REPLACE")); // NOI18N
        replaceMenuItem.setToolTipText(bundle.getString("MENUITEM_REPLACE_TOOLTIP")); // NOI18N
        replaceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceActionPerformed(evt);
            }
        });
        editMenu.add(replaceMenuItem);

        selectAllMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        selectAllMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        selectAllMenuItem.setText(bundle.getString("MENUITEM_SELECTALL")); // NOI18N
        selectAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllActionPerformed(evt);
            }
        });
        editMenu.add(selectAllMenuItem);

        clearAllMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        clearAllMenuItem.setText(bundle.getString("MENUITEM_DELETEALL")); // NOI18N
        clearAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAllActionPerformed(evt);
            }
        });
        editMenu.add(clearAllMenuItem);
        editMenu.add(jSeparator16);

        commentSelectionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PERIOD, java.awt.event.InputEvent.CTRL_MASK));
        commentSelectionMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        commentSelectionMenuItem.setText(bundle.getString("POPUPMENU_MENUITEM_COMMENT")); // NOI18N
        commentSelectionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commentSelectionActionPerformed(evt);
            }
        });
        editMenu.add(commentSelectionMenuItem);

        uncommentSelectionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_COMMA, java.awt.event.InputEvent.CTRL_MASK));
        uncommentSelectionMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        uncommentSelectionMenuItem.setText(bundle.getString("JFRAME_EDIT_UNCOMMENT_SELECTION")); // NOI18N
        uncommentSelectionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uncommentSelectionActionPerformed(evt);
            }
        });
        editMenu.add(uncommentSelectionMenuItem);

        tabRightMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tabRightMenuItem.setText(bundle.getString("MENUITEM_RIGHTSHIFT")); // NOI18N
        tabRightMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabRightActionPerformed(evt);
            }
        });
        editMenu.add(tabRightMenuItem);

        tabLeftMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        tabLeftMenuItem.setText(bundle.getString("MENUITEM_LEFTSHIFT")); // NOI18N
        tabLeftMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tabLeftActionPerformed(evt);
            }
        });
        editMenu.add(tabLeftMenuItem);
        editMenu.add(jSeparator17);

        insertFileMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        insertFileMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        insertFileMenuItem.setText(bundle.getString("JFRAME_INSERT_FROM_FILE")); // NOI18N
        insertFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertFileActionPerformed(evt);
            }
        });
        editMenu.add(insertFileMenuItem);

        insertSymbolMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        insertSymbolMenuItem.setText(bundle.getString("JFRAME_INSERT_SYMBOL")); // NOI18N
        insertSymbolMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertSymbolActionPerformed(evt);
            }
        });
        editMenu.add(insertSymbolMenuItem);

        setBookmarkMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, java.awt.event.InputEvent.CTRL_MASK));
        setBookmarkMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        setBookmarkMenuItem.setText(bundle.getString("MENUITEM_SETBOOKMARK")); // NOI18N
        setBookmarkMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setBookmarkMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(setBookmarkMenuItem);

        nextBookmarkMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        nextBookmarkMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        nextBookmarkMenuItem.setText(bundle.getString("MENUITEM_NEXTBOOKMARK")); // NOI18N
        nextBookmarkMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextBookmarkMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(nextBookmarkMenuItem);

        extractStringsMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        extractStringsMenuItem.setText("Extract Strings");
        extractStringsMenuItem.setToolTipText(bundle.getString("JFRAME_EXTRACT_STRINGS")); // NOI18N
        extractStringsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractStringsActionPerformed(evt);
            }
        });
        editMenu.add(extractStringsMenuItem);

        translateMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        translateMenuItem.setText("Translate Strings");
        translateMenuItem.setToolTipText(bundle.getString("JFRAME_TRANSLATE")); // NOI18N
        translateMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                translateActionPerformed(evt);
            }
        });
        editMenu.add(translateMenuItem);

        mainMenuBar.add(editMenu);

        viewMenu.setText(bundle.getString("MENU_VIEW")); // NOI18N
        viewMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        outputCheckBoxMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        outputCheckBoxMenuItem.setSelected(true);
        outputCheckBoxMenuItem.setText(bundle.getString("CHECKBOX_OUTPUT")); // NOI18N
        outputCheckBoxMenuItem.setToolTipText(bundle.getString("CHECKBOX_OUTPUT_TOOLTIP")); // NOI18N
        outputCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputCheckBoxActionPerformed(evt);
            }
        });
        viewMenu.add(outputCheckBoxMenuItem);

        toolbarCheckBoxMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        toolbarCheckBoxMenuItem.setSelected(true);
        toolbarCheckBoxMenuItem.setText(bundle.getString("CHECKBOX_JTOOLBAR")); // NOI18N
        toolbarCheckBoxMenuItem.setToolTipText(bundle.getString("CHECKBOX_JTOOLBAR_TOOLTIP")); // NOI18N
        toolbarCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toolbarCheckBoxActionPerformed(evt);
            }
        });
        viewMenu.add(toolbarCheckBoxMenuItem);

        treeCheckBoxMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        treeCheckBoxMenuItem.setSelected(true);
        treeCheckBoxMenuItem.setText(bundle.getString("CHECKBOX_JTREE")); // NOI18N
        treeCheckBoxMenuItem.setToolTipText(bundle.getString("CHECKBOX_JTREE_TOOLTIP")); // NOI18N
        treeCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeCheckBoxActionPerformed(evt);
            }
        });
        viewMenu.add(treeCheckBoxMenuItem);

        toggleFullscreenCheckBoxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        toggleFullscreenCheckBoxMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        toggleFullscreenCheckBoxMenuItem.setText(bundle.getString("MENUITEM_TOGGLEFULLSCREEN")); // NOI18N
        toggleFullscreenCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleFullscreenCheckBoxActionPerformed(evt);
            }
        });
        viewMenu.add(toggleFullscreenCheckBoxMenuItem);

        mainMenuBar.add(viewMenu);

        projectMenu.setText(bundle.getString("PROJECT_PROJECT")); // NOI18N
        projectMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        newProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.CTRL_MASK));
        newProjectMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        newProjectMenuItem.setText(bundle.getString("PROJECT_NEW_PROJECT")); // NOI18N
        newProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectActionPerformed(evt);
            }
        });
        projectMenu.add(newProjectMenuItem);

        openProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, 0));
        openProjectMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        openProjectMenuItem.setText(bundle.getString("PROJECT_OPEN_PROJECT")); // NOI18N
        openProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openProjectActionPerformed(evt);
            }
        });
        projectMenu.add(openProjectMenuItem);

        saveProjectMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        saveProjectMenuItem.setText(bundle.getString("PROJECT_SAVE_PROJECT")); // NOI18N
        saveProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProjectActionPerformed(evt);
            }
        });
        projectMenu.add(saveProjectMenuItem);

        closeProjectMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        closeProjectMenuItem.setText(bundle.getString("PROJECT_CLOSE_PROJECT")); // NOI18N
        closeProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeProjectActionPerformed(evt);
            }
        });
        projectMenu.add(closeProjectMenuItem);
        projectMenu.add(jSeparator14);

        addNewToProjectMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        addNewToProjectMenuItem.setText(bundle.getString("PROJECT_ADD_NEWFILE_TO_PROJECT")); // NOI18N
        addNewToProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewToProjectActionPerformed(evt);
            }
        });
        projectMenu.add(addNewToProjectMenuItem);

        addFileToProjectMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        addFileToProjectMenuItem.setText(bundle.getString("PROJECT_ADD_FILE_TO_PROJECT")); // NOI18N
        addFileToProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFileToProjectActionPerformed(evt);
            }
        });
        projectMenu.add(addFileToProjectMenuItem);

        removeFromProjectMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        removeFromProjectMenuItem.setText(bundle.getString("PROJECT_POPUP_REMOVE")); // NOI18N
        removeFromProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFromProjectActionPerformed(evt);
            }
        });
        projectMenu.add(removeFromProjectMenuItem);

        projectPropertiesMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        projectPropertiesMenuItem.setText("Project Properties");
        projectPropertiesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectPropertiesActionPerformed(evt);
            }
        });
        projectMenu.add(projectPropertiesMenuItem);

        projectSwitchesMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        projectSwitchesMenuItem.setText("Project Switches");
        projectSwitchesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectSwitchesActionPerformed(evt);
            }
        });
        projectMenu.add(projectSwitchesMenuItem);
        projectMenu.add(jSeparator5);

        lastProjectMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lastProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastProjectActionPerformed(evt);
            }
        });
        projectMenu.add(lastProjectMenuItem);

        mainMenuBar.add(projectMenu);

        modeMenu.setText("Mode");
        modeMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        informModeCheckBoxMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        informModeCheckBoxMenuItem.setText("Inform Mode");
        informModeCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                informModeActionPerformed(evt);
            }
        });
        modeMenu.add(informModeCheckBoxMenuItem);

        glulxModeCheckBoxMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        glulxModeCheckBoxMenuItem.setText("Glulx Mode");
        glulxModeCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                glulxModeActionPerformed(evt);
            }
        });
        modeMenu.add(glulxModeCheckBoxMenuItem);

        mainMenuBar.add(modeMenu);

        buildMenu.setText(bundle.getString("MENU_BUILD")); // NOI18N
        buildMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        buildAllMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        buildAllMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buildAllMenuItem.setText(bundle.getString("MENUITEM_BUILDALL")); // NOI18N
        buildAllMenuItem.setToolTipText(bundle.getString("MENUITEM_BUILDALL_TOOLTIP")); // NOI18N
        buildAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildAllActionPerformed(evt);
            }
        });
        buildMenu.add(buildAllMenuItem);

        switchesMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        switchesMenuItem.setText(bundle.getString("MENUITEM_SWITCHES")); // NOI18N
        switchesMenuItem.setToolTipText(bundle.getString("MENUITEM_SWITCHES_TOOLTIP")); // NOI18N
        switchesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchesActionPerformed(evt);
            }
        });
        buildMenu.add(switchesMenuItem);
        buildMenu.add(jSeparator2);

        runMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, java.awt.event.InputEvent.CTRL_MASK));
        runMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        runMenuItem.setText(bundle.getString("MENUITEM_RUN")); // NOI18N
        runMenuItem.setToolTipText(bundle.getString("MENUITEM_RUN_TOOLTIP")); // NOI18N
        runMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runActionPerformed(evt);
            }
        });
        buildMenu.add(runMenuItem);

        mainMenuBar.add(buildMenu);

        glulxMenu.setText("Glulx");
        glulxMenu.setEnabled(false);
        glulxMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        buildAllGlulxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, 0));
        buildAllGlulxMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        buildAllGlulxMenuItem.setText(bundle.getString("MENUITEM_BUILD_ALL")); // NOI18N
        buildAllGlulxMenuItem.setToolTipText(bundle.getString("MENUITEM_BUILD_ALL_TOOLTIP")); // NOI18N
        buildAllGlulxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildAllGlulxActionPerformed(evt);
            }
        });
        glulxMenu.add(buildAllGlulxMenuItem);
        glulxMenu.add(jSeparator18);

        makeResourceMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        makeResourceMenuItem.setText(bundle.getString("MENUITEM_MAKE_RES")); // NOI18N
        makeResourceMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeResourceActionPerformed(evt);
            }
        });
        glulxMenu.add(makeResourceMenuItem);

        compileMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        compileMenuItem.setText(bundle.getString("MENUITEM_COMPILE_INF")); // NOI18N
        compileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compileActionPerformed(evt);
            }
        });
        glulxMenu.add(compileMenuItem);

        makeBlbMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, java.awt.event.InputEvent.CTRL_MASK));
        makeBlbMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        makeBlbMenuItem.setText(bundle.getString("MENUITEM_MAKE_BLB")); // NOI18N
        makeBlbMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeBlbActionPerformed(evt);
            }
        });
        glulxMenu.add(makeBlbMenuItem);
        glulxMenu.add(jSeparator15);

        runUlxMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        runUlxMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        runUlxMenuItem.setText(bundle.getString("MENUITEM_RUN_ULX")); // NOI18N
        runUlxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runUlxActionPerformed(evt);
            }
        });
        glulxMenu.add(runUlxMenuItem);

        runBlbMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, java.awt.event.InputEvent.CTRL_MASK));
        runBlbMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        runBlbMenuItem.setText(bundle.getString("MENUITEM_RUN_BLB")); // NOI18N
        runBlbMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runBlbActionPerformed(evt);
            }
        });
        glulxMenu.add(runBlbMenuItem);

        mainMenuBar.add(glulxMenu);

        optionsMenu.setText(bundle.getString("MENU_OPTIONS")); // NOI18N
        optionsMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        configFileMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        configFileMenuItem.setText("Jif.cfg file");
        configFileMenuItem.setToolTipText("Edit configuration file for JIF");
        configFileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configFileActionPerformed(evt);
            }
        });
        optionsMenu.add(configFileMenuItem);

        settingsMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        settingsMenuItem.setText(bundle.getString("JFRAME_SETTING")); // NOI18N
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsActionPerformed(evt);
            }
        });
        optionsMenu.add(settingsMenuItem);
        optionsMenu.add(jSeparator12);

        garbageCollectionMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        garbageCollectionMenuItem.setText("Garbage Collector");
        garbageCollectionMenuItem.setToolTipText("Free unused object from the memory");
        garbageCollectionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                garbageCollectionActionPerformed(evt);
            }
        });
        optionsMenu.add(garbageCollectionMenuItem);

        mainMenuBar.add(optionsMenu);

        helpMenu.setText(bundle.getString("MENU_HELP")); // NOI18N
        helpMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        readMeMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        readMeMenuItem.setText(bundle.getString("README")); // NOI18N
        readMeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                readMeActionPerformed(evt);
            }
        });
        helpMenu.add(readMeMenuItem);

        changelogMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        changelogMenuItem.setText("Changelog");
        changelogMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changelogMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(changelogMenuItem);
        helpMenu.add(jSeparator7);

        aboutMenuItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        aboutMenuItem.setText(bundle.getString("ABOUTJIF")); // NOI18N
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        mainMenuBar.add(helpMenu);

        setJMenuBar(mainMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void highlightSelectedComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_highlightSelectedComboBoxActionPerformed
        optionHighlightUpdate();
    }//GEN-LAST:event_highlightSelectedComboBoxActionPerformed

    private void warningColorButtonnormalColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_warningColorButtonnormalColorActionPerformed
        optionColor(InformSyntax.Warnings);
        optionWarningsColor();
    }//GEN-LAST:event_warningColorButtonnormalColorActionPerformed

    private void defaultLightHighlightButtondefaultLightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultLightHighlightButtondefaultLightActionPerformed
        optionHighlightDefaultLight();
    }//GEN-LAST:event_defaultLightHighlightButtondefaultLightActionPerformed

    private void defaultDarkHighlightButtondefaultDarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultDarkHighlightButtondefaultDarkActionPerformed
        optionHighlightDefaultDark();
    }//GEN-LAST:event_defaultDarkHighlightButtondefaultDarkActionPerformed

    private void jumpToColorButtonkeywordColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jumpToColorButtonkeywordColorActionPerformed
        optionColor(InformSyntax.JumpTo);
        optionJumpToColor();
    }//GEN-LAST:event_jumpToColorButtonkeywordColorActionPerformed

    private void errorColorButtoncommentColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorColorButtoncommentColorActionPerformed
        optionColor(InformSyntax.Errors);
        optionErrorsColor();
    }//GEN-LAST:event_errorColorButtoncommentColorActionPerformed

    private void bookmarkColorButtonattributeColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookmarkColorButtonattributeColorActionPerformed
        optionColor(InformSyntax.Bookmarks);
        optionBookmarksColor();
    }//GEN-LAST:event_bookmarkColorButtonattributeColorActionPerformed

    private void bracketColorButtonbackgroundColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bracketColorButtonbackgroundColorActionPerformed
        optionColor(InformSyntax.Brackets);
        optionBracketsColor();
    }//GEN-LAST:event_bracketColorButtonbackgroundColorActionPerformed

    private void projectSwitchesSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectSwitchesSaveActionPerformed
        projectSwitchesSave();
    }//GEN-LAST:event_projectSwitchesSaveActionPerformed

    private void optionCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionCancelActionPerformed
        optionHide();
    }//GEN-LAST:event_optionCancelActionPerformed

    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        exitJif();
    }//GEN-LAST:event_exitForm

    private void symbolListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_symbolListKeyPressed
        symbolsKey(evt);
    }//GEN-LAST:event_symbolListKeyPressed

    private void symbolListMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_symbolListMouseExited
        symbolDialog.setTitle(java.util.ResourceBundle.getBundle("JIF").getString("STR_SYMBOLS"));
    }//GEN-LAST:event_symbolListMouseExited

    private void symbolListMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_symbolListMouseEntered
        symbolDialog.setTitle(java.util.ResourceBundle.getBundle("JIF").getString("JWINDOW_TOOLTIP"));
    }//GEN-LAST:event_symbolListMouseEntered

    private void symbolListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_symbolListMouseClicked
        symbolsClick(evt);
    }//GEN-LAST:event_symbolListMouseClicked

    private void tabSizeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabSizeTextFieldActionPerformed
        try {
            optionTabSize = Integer.parseInt(tabSizeTextField.getText());
        } catch (Exception e) {
            optionTabSize = 4;
        }
    }//GEN-LAST:event_tabSizeTextFieldActionPerformed

    private void defaultLightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultLightActionPerformed
        optionColorDefaultLight();
        optionFontDefault();
    }//GEN-LAST:event_defaultLightActionPerformed

    private void wordColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wordColorActionPerformed
        optionColor(InformSyntax.Word);
        optionWordColor();
    }//GEN-LAST:event_wordColorActionPerformed

    private void numberColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numberColorActionPerformed
        optionColor(InformSyntax.Number);
        optionNumberColor();
    }//GEN-LAST:event_numberColorActionPerformed

    private void stringColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stringColorActionPerformed
        optionColor(InformSyntax.String);
        optionStringColor();
    }//GEN-LAST:event_stringColorActionPerformed

    private void projectSwitchesCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectSwitchesCloseActionPerformed
        projectSwitchesHide();
    }//GEN-LAST:event_projectSwitchesCloseActionPerformed

    private void projectPropertiesCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectPropertiesCloseActionPerformed
        projectPropertiesHide();
    }//GEN-LAST:event_projectPropertiesCloseActionPerformed

    private void projectPropertiesSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectPropertiesSaveActionPerformed
        projectPropertiesSave();
    }//GEN-LAST:event_projectPropertiesSaveActionPerformed

    private void projectSwitchesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectSwitchesActionPerformed
        projectSwitchesDialog();
    }//GEN-LAST:event_projectSwitchesActionPerformed

    private void projectPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectPropertiesActionPerformed
        projectPropertiesDialog();
    }//GEN-LAST:event_projectPropertiesActionPerformed

    private void garbageCollectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_garbageCollectionActionPerformed
        System.gc();
    }//GEN-LAST:event_garbageCollectionActionPerformed

    private void lastProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastProjectActionPerformed
        if (((JMenuItem) evt.getSource()).getText() == null
                || ((JMenuItem) evt.getSource()).getText().length() <= 0) {
            return;
        }
        projectOpen(new File(config.getLastProjectPath()));
    }//GEN-LAST:event_lastProjectActionPerformed

    private void outputTextAreaMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_outputTextAreaMouseMoved
        outputMouse(evt);
    }//GEN-LAST:event_outputTextAreaMouseMoved

    private void codeTreeTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_codeTreeTreeExpanded
        treeExpand(evt);
    }//GEN-LAST:event_codeTreeTreeExpanded

    private void outputCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputCheckBoxActionPerformed
        config.setOutputVisible(outputCheckBoxMenuItem.getState());
    }//GEN-LAST:event_outputCheckBoxActionPerformed

    private void translateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_translateActionPerformed
        getSelectedTextPane().insertTranslate(
                new File(getSelectedPath() + "_translate.txt"),
                new File(getSelectedPath() + "_translated.inf"));
    }//GEN-LAST:event_translateActionPerformed

    private void extractStringsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractStringsActionPerformed
        getSelectedTextPane().extractTranslate(
                new File(getSelectedPath() + "_translate.txt"));
    }//GEN-LAST:event_extractStringsActionPerformed
    
    private void findActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findActionPerformed
        getSelectedTextPane().findString(this);
    }//GEN-LAST:event_findActionPerformed

    private void searchProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchProjectActionPerformed
        String target = searchProjectTextField.getText();
        if (target == null || target.trim().equals("")) {
            return;
        }
        // if output window is hidden, I'll show it
        if (!outputCheckBoxMenuItem.getState()) {
            mainSplitPane.setBottomComponent(outputTabbedPane);
            outputTabbedPane.setVisible(true);
        }
        searchAllFiles(target);
    }//GEN-LAST:event_searchProjectActionPerformed

    private void definitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_definitionActionPerformed
        String target = definitionTextField.getText();
        if (target == null || target.trim().equals("")) {
            return;
        }
        checkDefinition(target);
    }//GEN-LAST:event_definitionActionPerformed

    private void replaceCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceCloseActionPerformed
        replaceHide();
    }//GEN-LAST:event_replaceCloseActionPerformed

    private void replaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceActionPerformed
        replaceDialog();
    }//GEN-LAST:event_replaceActionPerformed

    private void searchAllctionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchAllctionPerformed
        searchAllDialog();
    }//GEN-LAST:event_searchAllctionPerformed

    private void searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchActionPerformed
        searchDialog();
    }//GEN-LAST:event_searchActionPerformed

    private void numberLinesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numberLinesCheckBoxActionPerformed
        if (numberLinesCheckBox.isSelected()) {
            wrapLinesCheckBox.setSelected(false);
        }
        this.numberLines = this.numberLinesCheckBox.isSelected();
    }//GEN-LAST:event_numberLinesCheckBoxActionPerformed

    private void cutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutActionPerformed
        getSelectedTextPane().cut();
    }//GEN-LAST:event_cutActionPerformed

    private void wrapLinesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wrapLinesCheckBoxActionPerformed
        if (wrapLinesCheckBox.isSelected()) {
            numberLinesCheckBox.setSelected(false);
        }
        this.wrapLines = this.wrapLinesCheckBox.isSelected();
    }//GEN-LAST:event_wrapLinesCheckBoxActionPerformed
    
    private void toggleFullscreenCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleFullscreenCheckBoxActionPerformed
        config.setFullScreen(toggleFullscreenCheckBoxMenuItem.getState());
    }//GEN-LAST:event_toggleFullscreenCheckBoxActionPerformed

    private void nextBookmarkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextBookmarkMenuItemActionPerformed
        getSelectedTextPane().nextBookmark();
    }//GEN-LAST:event_nextBookmarkMenuItemActionPerformed

    private void setBookmarkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setBookmarkMenuItemActionPerformed
        getSelectedTextPane().setBookmark();
    }//GEN-LAST:event_setBookmarkMenuItemActionPerformed

    private void libraryPath3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_libraryPath3ActionPerformed
        chooseDirectory(libraryPath3TextField);
    }//GEN-LAST:event_libraryPath3ActionPerformed

    private void libraryPath2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_libraryPath2ActionPerformed
        chooseDirectory(libraryPath2TextField);
    }//GEN-LAST:event_libraryPath2ActionPerformed

    private void jumpToSourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jumpToSourceActionPerformed
        try {
            String target = getSelectedTextPane().getCurrentWord();
            if (target == null || target.trim().equals("")) {
                return;
            }
            checkDefinition(target);
        } catch (BadLocationException ble) {
            System.err.println("Jump to source: " + ble.getMessage());
        }
    }//GEN-LAST:event_jumpToSourceActionPerformed

    private void codeTreeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_codeTreeMouseEntered
        treeRefreshIncremental();
    }//GEN-LAST:event_codeTreeMouseEntered

    private void defaultDarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultDarkActionPerformed
        optionColorDefaultDark();
        optionFontDefault();
    }//GEN-LAST:event_defaultDarkActionPerformed

    private void jButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonMouseExited
        ((JButton) evt.getSource()).setBorderPainted(false);
    }//GEN-LAST:event_jButtonMouseExited

    private void jButtonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonMouseEntered
        ((JButton) evt.getSource()).setBorderPainted(true);
    }//GEN-LAST:event_jButtonMouseEntered

    private void saveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAllActionPerformed
        allFilesSaveDialog();
    }//GEN-LAST:event_saveAllActionPerformed

    private void removeMainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeMainActionPerformed
        projectMainClear();
    }//GEN-LAST:event_removeMainActionPerformed

    private void setMainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setMainActionPerformed
        projectMainSet();
    }//GEN-LAST:event_setMainActionPerformed

    private void buildAllGlulxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildAllGlulxActionPerformed
        buildAllDialog();
    }//GEN-LAST:event_buildAllGlulxActionPerformed

    private void runBlbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runBlbActionPerformed
        runBlbDialog();
    }//GEN-LAST:event_runBlbActionPerformed

    private void runUlxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runUlxActionPerformed
        runUlxDialog();
    }//GEN-LAST:event_runUlxActionPerformed

    private void compileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compileActionPerformed
        buildAllDialog();
    }//GEN-LAST:event_compileActionPerformed

    private void makeBlbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeBlbActionPerformed
        makeBlbDialog();
    }//GEN-LAST:event_makeBlbActionPerformed

    private void makeResourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeResourceActionPerformed
        // Make the resource file and visualize the log in the output window
        makeResourcesDialog();
    }//GEN-LAST:event_makeResourceActionPerformed

    private void blcPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blcPathActionPerformed
        choosePath(blcPathTextField);
    }//GEN-LAST:event_blcPathActionPerformed

    private void bresPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bresPathActionPerformed
        choosePath(bresPathTextField);
    }//GEN-LAST:event_bresPathActionPerformed

    private void glulxPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glulxPathActionPerformed
        choosePath(glulxPathTextField);
    }//GEN-LAST:event_glulxPathActionPerformed

    private void informModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_informModeActionPerformed
        setMode(informModeCheckBoxMenuItem.getState());
    }//GEN-LAST:event_informModeActionPerformed

    private void glulxModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_glulxModeActionPerformed
        setMode(!glulxModeCheckBoxMenuItem.getState());
    }//GEN-LAST:event_glulxModeActionPerformed

    private void libraryPath1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_libraryPath1ActionPerformed
        chooseDirectory(libraryPath1TextField);
    }//GEN-LAST:event_libraryPath1ActionPerformed

    private void bracketCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bracketCheckActionPerformed
        fileBracketsCheck();
    }//GEN-LAST:event_bracketCheckActionPerformed

    private void treeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_treeCheckBoxActionPerformed
        config.setTreeVisible(treeCheckBoxMenuItem.getState());
    }//GEN-LAST:event_treeCheckBoxActionPerformed

    private void backgroundColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backgroundColorActionPerformed
        optionBackground();
        optionBackgroundColor();
    }//GEN-LAST:event_backgroundColorActionPerformed

    private void addNewToProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewToProjectActionPerformed
        projectFileNew();
    }//GEN-LAST:event_addNewToProjectActionPerformed

    private void tabLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabLeftActionPerformed
        getAction(JifEditorKit.tabLeftAction).actionPerformed(evt);
    }//GEN-LAST:event_tabLeftActionPerformed

    private void tabRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tabRightActionPerformed
        getAction(JifEditorKit.tabRightAction).actionPerformed(evt);
    }//GEN-LAST:event_tabRightActionPerformed
    
    private void tutorialPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tutorialPrintActionPerformed
        tutorialPrint();
    }//GEN-LAST:event_tutorialPrintActionPerformed

    private void redoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoActionPerformed
        getAction("Redo").actionPerformed(evt);
    }//GEN-LAST:event_redoActionPerformed

    private void undoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoActionPerformed
        getAction("Undo").actionPerformed(evt);
    }//GEN-LAST:event_undoActionPerformed

    private void settingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsActionPerformed
        optionDialog();
    }//GEN-LAST:event_settingsActionPerformed

    private void tutorialOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tutorialOKActionPerformed
        tutorialHide();
    }//GEN-LAST:event_tutorialOKActionPerformed

    private void fontSizeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontSizeComboBoxActionPerformed
        optionContext.setFontSize(Integer.parseInt((String) fontSizeComboBox.getSelectedItem()));
    }//GEN-LAST:event_fontSizeComboBoxActionPerformed

    private void fontNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontNameComboBoxActionPerformed
        optionContext.setFontName((String) fontNameComboBox.getSelectedItem());
    }//GEN-LAST:event_fontNameComboBoxActionPerformed

    private void optionDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionDefaultActionPerformed
        optionDefault();
    }//GEN-LAST:event_optionDefaultActionPerformed

    private void commentColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commentColorActionPerformed
        optionColor(InformSyntax.Comment);
        optionCommentColor();
    }//GEN-LAST:event_commentColorActionPerformed

    private void normalColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normalColorActionPerformed
        optionColor(InformSyntax.Normal);
        optionNormalColor();
    }//GEN-LAST:event_normalColorActionPerformed

    private void verbColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verbColorActionPerformed
        optionColor(InformSyntax.Verb);
        optionVerbColor();
    }//GEN-LAST:event_verbColorActionPerformed

    private void propertyColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propertyColorActionPerformed
        optionColor(InformSyntax.Property);
        optionPropertyColor();
    }//GEN-LAST:event_propertyColorActionPerformed

    private void attributeColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attributeColorActionPerformed
        optionColor(InformSyntax.Attribute);
        optionAttributeColor();
    }//GEN-LAST:event_attributeColorActionPerformed

    private void keywordColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keywordColorActionPerformed
        optionColor(InformSyntax.Keyword);
        optionKeywordColor();
    }//GEN-LAST:event_keywordColorActionPerformed

    private void switchManagerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchManagerActionPerformed
        // if a project exists, Jif will launch the project switches dialog
        if (project.isClosed()) {
            configurationSwitchesDialog();
        } else {
            projectSwitchesDialog();
        }
    }//GEN-LAST:event_switchManagerActionPerformed

    private void openSelectedFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSelectedFilesActionPerformed
        projectOpenSelected(projectList.getSelectedValues());
    }//GEN-LAST:event_openSelectedFilesActionPerformed

    private void switchesSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchesSaveActionPerformed
        configurationSwitchesSave();
    }//GEN-LAST:event_switchesSaveActionPerformed

    private void projectListMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_projectListMouseEntered
        projectEntered();
    }//GEN-LAST:event_projectListMouseEntered

    private void interpreterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interpreterActionPerformed
        runInterpreter();
    }//GEN-LAST:event_interpreterActionPerformed

    private void infoCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoCloseActionPerformed
        infoDialog.setVisible(false);
    }//GEN-LAST:event_infoCloseActionPerformed

    private void fileTabbedPaneComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_fileTabbedPaneComponentShown
        fileTabClick();
    }//GEN-LAST:event_fileTabbedPaneComponentShown

    private void fileTabbedPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileTabbedPaneMouseClicked
        fileTabClick();
    }//GEN-LAST:event_fileTabbedPaneMouseClicked

    private void saveProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProjectActionPerformed
        projectSaveDialog();
    }//GEN-LAST:event_saveProjectActionPerformed

    private void closeProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeProjectActionPerformed
        projectClose();
    }//GEN-LAST:event_closeProjectActionPerformed

    private void openProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openProjectActionPerformed
        projectOpenDialog();
    }//GEN-LAST:event_openProjectActionPerformed

    private void newProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectActionPerformed
        projectNewDialog();
    }//GEN-LAST:event_newProjectActionPerformed

    private void optionSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optionSaveActionPerformed
        optionSave();
    }//GEN-LAST:event_optionSaveActionPerformed

    private void projectListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_projectListMouseClicked
        projectClick(evt);
    }//GEN-LAST:event_projectListMouseClicked

    private void printPopupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printPopupMenuItemActionPerformed
        print();
    }//GEN-LAST:event_printPopupMenuItemActionPerformed

    private void removeFromProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFromProjectActionPerformed
        projectFileRemove();
    }//GEN-LAST:event_removeFromProjectActionPerformed

    private void addFileToProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFileToProjectActionPerformed
        projectFileAdd();
    }//GEN-LAST:event_addFileToProjectActionPerformed

    private void insertSymbolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertSymbolActionPerformed
        symbolsShow();
    }//GEN-LAST:event_insertSymbolActionPerformed

    private void uncommentSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uncommentSelectionActionPerformed
        getAction(JifEditorKit.uncommentAction).actionPerformed(evt);
    }//GEN-LAST:event_uncommentSelectionActionPerformed

    private void replaceReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceReplaceActionPerformed
        replaceReplace();
    }//GEN-LAST:event_replaceReplaceActionPerformed

    private void replaceFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceFindActionPerformed
        replaceFind();
    }//GEN-LAST:event_replaceFindActionPerformed

    private void replaceAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceAllActionPerformed
        replaceReplaceAll();
    }//GEN-LAST:event_replaceAllActionPerformed

    private void commentSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commentSelectionActionPerformed
        getAction(JifEditorKit.commentAction).actionPerformed(evt);
    }//GEN-LAST:event_commentSelectionActionPerformed

    private void insertFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertFileActionPerformed
        insertFromFile();
    }//GEN-LAST:event_insertFileActionPerformed

    private void printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printActionPerformed
        print();
    }//GEN-LAST:event_printActionPerformed

    private void codeTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_codeTreeValueChanged
        treeClick();
    }//GEN-LAST:event_codeTreeValueChanged

    private void outputTextAreaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_outputTextAreaMouseClicked
        outputClick(evt);
    }//GEN-LAST:event_outputTextAreaMouseClicked

    private void clearAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearAllActionPerformed
        fileClearAll();
    }//GEN-LAST:event_clearAllActionPerformed

    private void selectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllActionPerformed
        getSelectedTextPane().selectAll();
    }//GEN-LAST:event_selectAllActionPerformed

    private void clearRecentFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearRecentFilesActionPerformed
        config.clearRecentFiles();
        configurationSave();
    }//GEN-LAST:event_clearRecentFilesActionPerformed
 
    private void closeAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeAllActionPerformed
        allFilesClose();
    }//GEN-LAST:event_closeAllActionPerformed

    private void interpreterPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interpreterPathActionPerformed
        choosePath(interpreterPathTextField);
    }//GEN-LAST:event_interpreterPathActionPerformed

    private void compilerPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compilerPathActionPerformed
        choosePath(compilerPathTextField);
    }//GEN-LAST:event_compilerPathActionPerformed

    private void gamePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gamePathActionPerformed
        choosePath(gamePathTextField);
    }//GEN-LAST:event_gamePathActionPerformed

    private void libraryPath0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_libraryPath0ActionPerformed
        chooseDirectory(libraryPath0TextField);
    }//GEN-LAST:event_libraryPath0ActionPerformed

    private void closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeActionPerformed
        fileCloseDialog();
    }//GEN-LAST:event_closeActionPerformed

    private void copyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyActionPerformed
        copyToClipBoard();
    }//GEN-LAST:event_copyActionPerformed

    private void readMeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_readMeActionPerformed
        tutorialDialog("readme.txt", "Read Me");
    }//GEN-LAST:event_readMeActionPerformed

    private void textCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textCloseActionPerformed
        textDialog.setVisible(false);
        textTextArea.setText("");
        textLabel.setText("");
    }//GEN-LAST:event_textCloseActionPerformed

    private void saveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsActionPerformed
        fileSaveAs(null);
    }//GEN-LAST:event_saveAsActionPerformed
    private void configFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configFileActionPerformed
        configurationPropertiesDialog();
    }//GEN-LAST:event_configFileActionPerformed

    private void switchesCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchesCloseActionPerformed
        switchesDialog.setVisible(false);
    }//GEN-LAST:event_switchesCloseActionPerformed

    private void switchesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchesActionPerformed
        configurationSwitchesDialog();
    }//GEN-LAST:event_switchesActionPerformed

    private void configSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configSaveActionPerformed
        configurationPropertiesSave();
    }//GEN-LAST:event_configSaveActionPerformed
    private void configCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configCloseActionPerformed
        configurationPropertiesHide();
    }//GEN-LAST:event_configCloseActionPerformed

    private void newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newActionPerformed
        fileNew();
    }//GEN-LAST:event_newActionPerformed

    private void aboutOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutOKActionPerformed
        aboutHide();
    }//GEN-LAST:event_aboutOKActionPerformed

    private void clearPopupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearPopupActionPerformed
        pastePopupMenu.removeAll();
    }//GEN-LAST:event_clearPopupActionPerformed

    private void toolbarCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolbarCheckBoxActionPerformed
        config.setToolbarVisible(toolbarCheckBoxMenuItem.getState());
    }//GEN-LAST:event_toolbarCheckBoxActionPerformed

    private void aboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutActionPerformed
        aboutShow();
    }//GEN-LAST:event_aboutActionPerformed

    private void runActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runActionPerformed
        runDialog();
    }//GEN-LAST:event_runActionPerformed

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        exitJif();
    }//GEN-LAST:event_exitActionPerformed

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        fileSaveDialog();
    }//GEN-LAST:event_saveActionPerformed

    private void buildAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildAllActionPerformed
        buildAllDialog();
    }//GEN-LAST:event_buildAllActionPerformed

    private void openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openActionPerformed
        fileOpenDialog();
    }//GEN-LAST:event_openActionPerformed

    private void changelogMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changelogMenuItemActionPerformed
        tutorialDialog("changelog.txt", "Change Log");
    }//GEN-LAST:event_changelogMenuItemActionPerformed

    private void jFrameMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jFrameMoved
        frameMoved();
    }//GEN-LAST:event_jFrameMoved

    private void jFrameResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jFrameResized
        frameResized();
    }//GEN-LAST:event_jFrameResized

    private void gamePathTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_gamePathTextFieldFocusLost
        this.game = this.gamePathTextField.getText();
    }//GEN-LAST:event_gamePathTextFieldFocusLost

    private void compilerPathTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_compilerPathTextFieldFocusLost
        this.compiler = this.compilerPathTextField.getText();
    }//GEN-LAST:event_compilerPathTextFieldFocusLost

    private void interpreterPathTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_interpreterPathTextFieldFocusLost
        this.interpreterZcode = this.interpreterPathTextField.getText();
    }//GEN-LAST:event_interpreterPathTextFieldFocusLost

    private void libraryPath0TextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_libraryPath0TextFieldFocusLost
        this.library[0] = this.libraryPath0TextField.getText();
    }//GEN-LAST:event_libraryPath0TextFieldFocusLost

    private void libraryPath1TextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_libraryPath1TextFieldFocusLost
        this.library[1] = this.libraryPath1TextField.getText();
    }//GEN-LAST:event_libraryPath1TextFieldFocusLost

    private void libraryPath2TextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_libraryPath2TextFieldFocusLost
        this.library[2] = this.libraryPath2TextField.getText();
    }//GEN-LAST:event_libraryPath2TextFieldFocusLost

    private void libraryPath3TextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_libraryPath3TextFieldFocusLost
        this.library[3] = this.libraryPath3TextField.getText();
    }//GEN-LAST:event_libraryPath3TextFieldFocusLost

    private void glulxPathTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_glulxPathTextFieldFocusLost
        this.interpreterGlulx = this.glulxPathTextField.getText();
    }//GEN-LAST:event_glulxPathTextFieldFocusLost

    private void bresPathTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bresPathTextFieldFocusLost
        this.bres = this.bresPathTextField.getText();
    }//GEN-LAST:event_bresPathTextFieldFocusLost

    private void blcPathTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_blcPathTextFieldFocusLost
        this.blc = this.blcPathTextField.getText();
    }//GEN-LAST:event_blcPathTextFieldFocusLost

    private void openLastFileCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openLastFileCheckBoxActionPerformed
        this.openLastFile = this.openLastFileCheckBox.isSelected();
    }//GEN-LAST:event_openLastFileCheckBoxActionPerformed

    private void createNewFileCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewFileCheckBoxActionPerformed
        this.createNewFile = this.createNewFileCheckBox.isSelected();
    }//GEN-LAST:event_createNewFileCheckBoxActionPerformed

    private void mappingLiveCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mappingLiveCheckBoxActionPerformed
        this.mappingLive = this.mappingLiveCheckBox.isSelected();
    }//GEN-LAST:event_mappingLiveCheckBoxActionPerformed

    private void helpedCodeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpedCodeCheckBoxActionPerformed
        this.helpedCode = this.helpedCodeCheckBox.isSelected();
    }//GEN-LAST:event_helpedCodeCheckBoxActionPerformed

    private void syntaxCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_syntaxCheckBoxActionPerformed
        this.syntaxHighlighting = this.syntaxCheckBox.isSelected();
    }//GEN-LAST:event_syntaxCheckBoxActionPerformed

// =============================================================================
    
    /**
     * Jif editor main
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // Check java version
        String javaVersion = System.getProperty("java.version");

        if (javaVersion.compareTo("1.6") < 0) {
            System.err.println("You are running Java version " + javaVersion + ".");
            System.err.println("JIF requires Java version 1.6 or later.");
            System.exit(1);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    // This ensures that the GUI won't have a thread-safety problem that could
    // break the UI before it even appears onscreen. (See Swing tutorial).
    private static void createAndShowGUI() {
        // Back to origins
//        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
//            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//
//        } catch (Exception e) {
//            System.out.println("ERROR Can't set look & feel: " + e.getMessage());
//        }
        new jFrame().setVisible(true);
    }

// -----------------------------------------------------------------------------
    
    // --- About ---------------------------------------------------------------
    
    private void aboutHide() {
        aboutDialog.setVisible(false);
    }
    
    private void aboutShow() {
        aboutDialog.pack();
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setVisible(true);
    }
    
    // --- All files -----------------------------------------------------------

    /**
     * Close all the currently open files
     */
    private void allFilesClose() {
        int numberOfComponents = getFileTabCount();
        for (int i=0; i < numberOfComponents; i++) {
            fileCloseDialog();
        }
    }
    
    /**
     * Save all the currently open files
     */
    private void allFilesSave() {
        // Remember the file selected
        int index = getSelectedIndex();

        int componenti = getFileTabCount();
        for (int i = 0; i < componenti; i++) {
            setSelectedIndex(i);
            // Only save modified files
            if (getSelectedTitle().indexOf("*") != -1) {
                fileSave();
            }
        }

        // reassign the file selected
        setSelectedIndex(index);
    }

    /**
     * Save all currently open files and projects
     */
    private void allFilesSaveDialog() {
        outputClear();
        allFilesSave();
        if (project.isOpen()) {
            projectSave();
        }
    }
    
    // --- Configuration -------------------------------------------------------
    
    private void configurationSave() {
        
        try {
            JifConfigurationDAO.store(config);
        } catch (JifConfigurationException ex) {
            System.err.println("Unable to save configuration: " + ex.getMessage());
        }
    }
    
    // --- Configuration properties dialog -------------------------------------
    
    private void configurationPropertiesDialog() {
        
        try {
            String fileName = config.getFilePath();
            File file = new File(fileName);
            
            if (!file.exists()) {
                System.out.println(java.util.ResourceBundle.getBundle("JIF").getString("ERR_OPENFILE1") + fileName);
                return;
            }
            
            configTextArea.setText(JifDAO.read(file));
            configTextArea.setCaretPosition(0);
            
            configurationPropertiesShow();
            
        } catch (Exception ex) {
            System.err.println("Configuration properties: " + ex.getMessage());
        }
    }
    
    void configurationPropertiesHide() {
        configDialog.setVisible(false);
    }
    
    private void configurationPropertiesSave() {
        // Save configuration properties file and reload configuration
        File file = new File(config.getFilePath());
        try {
            JifDAO.save(file, configTextArea.getText());
            JifConfigurationDAO.reload(config);
            configurationPropertiesSaveMessage();
        } catch (Exception ex) {
            System.err.println("Unable to save configuration: " + ex.getMessage());
        }
    }

    private void configurationPropertiesSaveMessage() {
        JOptionPane.showMessageDialog(
                configDialog,
                java.util.ResourceBundle.getBundle("JIF").getString("OK_SAVE1"),
                java.util.ResourceBundle.getBundle("JIF").getString("OK_SAVE2"),
                JOptionPane.INFORMATION_MESSAGE
                );
    }
    
    void configurationPropertiesShow() {
        configLabel.setText(config.getFilePath());
        configDialog.setSize(600,550);
        configDialog.setLocationRelativeTo(this);
        configDialog.setVisible(true);
    }
    
    // --- Configuration switches dialog ---------------------------------------
    
    void configurationSwitchesCreate() {
        switchesLowerPanel.removeAll();
        switchesUpperPanel.removeAll();
        for (Iterator i = config.getSwitches().entrySet().iterator(); i.hasNext(); ) {
            Entry e = (Entry) i.next();
            final String name    = (String) e.getKey();
            final String setting = (String) e.getValue();
            final Checkbox check = new Checkbox(name);
            //check.setFont(new Font("Monospaced", Font.PLAIN, 11));
            check.setFont(new Font("Dialog", Font.PLAIN, 14));
            check.setState(setting.equals("on") ? true : false);
            if (name.length() < 4) {
                switchesUpperPanel.add(check);
            } else {
                switchesLowerPanel.add(check);
            }
        }
    }
    
    private void configurationSwitchesDialog() {
        configurationSwitchesCreate();
        configurationSwitchesShow();
    }
    
    void configurationSwitchesHide() {
        switchesDialog.setVisible(false);
    }
    
    private void configurationSwitchesSave() {
        for (int i=0; i < switchesUpperPanel.getComponentCount(); i++) {
            Checkbox ch = (Checkbox) switchesUpperPanel.getComponent(i);
            config.setSwitch(ch.getLabel(), (ch.getState())?"on":"off");
        }
        for (int i=0; i < switchesLowerPanel.getComponentCount(); i++) {
            Checkbox ch = (Checkbox) switchesLowerPanel.getComponent(i);
            config.setSwitch(ch.getLabel(), (ch.getState())?"on":"off");
        }
        configurationSave();
        configurationSwitchesHide();
    }
    
    void configurationSwitchesShow() {
        switchesDialog.pack();
        switchesDialog.setLocationRelativeTo(this);
        switchesDialog.setVisible(true);
    }
    
    // --- File ----------------------------------------------------------------
    
    private void fileBracketsCheck() {
        if (getSelectedTextPane().checkBrackets()) {
            fileBracketsOKMessage();
        }
    }

    private void fileBracketsOKMessage() {
        JOptionPane.showMessageDialog(
                this,
                java.util.ResourceBundle.getBundle("JIF").getString("JFRAME_CHECK_BRACKET_OK"),
                "OK",
                JOptionPane.INFORMATION_MESSAGE
                );
    }

    /**
     * Search for the definition of a class within some Inform source.
     *
     * @param cb
     *              The Inform source code to search
     * @param entity
     *              The class definition to search for
     * @return <code>true</code> if the class definition is found
     */
    boolean fileClassCheck(CharBuffer cb, String entity) {
        Matcher m = classPattern.matcher(cb);
        while(m.find()) {
            if (m.group(1).equalsIgnoreCase(entity)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Search for the definition of a constant within some Inform source.
     *
     * @param cb
     *              The Inform source code to search
     * @param entity
     *              The constant definition to search for
     * @return <code>true</code> if the constant definition is found
     */
    boolean fileClassObjectCheck(CharBuffer cb, String className, String entity) {
        Pattern p = Pattern.compile(
                "(?:^|;)\\s*" + className + "\\s+(?:->\\s+)*(\\w+)",
                Pattern.MULTILINE|Pattern.CASE_INSENSITIVE
                );
        Matcher m = p.matcher(cb);
        while (m.find()) {
            if (m.group(1).equalsIgnoreCase(entity)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Find all the objects directly defined using a class name in some Inform
     * source code
     *
     * @param cb
     *              Inform source to search for object to class references
     * @param fileName
     *              Name of the file being searched
     * @param asset
     *              The class definition to which any references found should be
     *              added
     */
    private void fileClassObjectScan(CharBuffer cb, InformAsset asset) {
        String parentName = asset.getName();
        // Objects defined directly with a class reference using regular expression
        Pattern p = Pattern.compile(
                "(?:^|;)\\s*" + parentName + "\\s+(?:->\\s+)*(\\w+)",
                Pattern.MULTILINE|Pattern.CASE_INSENSITIVE
                );
        Matcher m = p.matcher(cb);
        fileObjectCreate(m, asset, parentName);
    }
    
    /**
     * Find all the class definitions in some Inform source code
     *
     * @param cb
     *              Inform source to search for class definitions
     * @param path
     *              Inform source path
     */
    private void fileClassScan(CharBuffer cb, String path) {
        
        Matcher m = classPattern.matcher(cb);
        while(m.find()) {
            String name  = m.group(1).toLowerCase();
            int position = m.start(1);
            
            if (fileAssets.containsKey(name)) {
                InformAsset ia = (InformAsset) fileAssets.get(name);
                ia.setLocation(new Inspect(name, path, position));
            } else {
                fileAssets.put(name, new InformAsset(path, name, position));
            }
        }
    }
    
    /**
     * Find all the class to class references in some Inform source code
     *
     * @param cb
     *              Inform source to search for class to class references
     * @param path
     *              Inform source path
     */
    private void fileClassToClassScan(CharBuffer cb, String path) {
        
        Matcher m = classToClassPattern.matcher(cb);
        while(m.find()) {
            String childName  = m.group(1).toLowerCase();
            String parentName = m.group(2).toLowerCase();
            
            InformAsset child  = getAsset(fileAssets, childName);
            InformAsset parent = getAsset(fileAssets, parentName);
            
            child.setParent(parentName);
            parent.addChild(childName);
            
        }
    }

    private InformAsset getAsset(Map assets, String name) {

        InformAsset asset;
        if (assets.containsKey(name)) {
            asset = (InformAsset) assets.get(name);
        } else {
            asset = new InformAsset(null, name, -1);
            assets.put(name, asset);
        }
        return asset;
    }
    
    void fileClearAll() {
    
        if (JOptionPane.showConfirmDialog(
                this,
                java.util.ResourceBundle.getBundle("JIF").getString("MSG_DELETE1"),
                java.util.ResourceBundle.getBundle("JIF").getString("MSG_DELETE2"),
                JOptionPane.OK_CANCEL_OPTION
                )
                == JOptionPane.CANCEL_OPTION) {
            return;
        }
        setText("");
    }
    
    public void fileClose() {
        JifFileName selected = getSelectedFileName();
        fileTabbedPane.remove(getSelectedScrollPane());
        buffer.remove(selected);
        updateBuffer();
        updateFile(selected);
        treeRefresh();
    }
    
    private void fileCloseDialog() {

        // Save and close, close and cancel messages for close options
        String[] scelte = new String[3];
        scelte[0] = java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF14");
        scelte[1] = java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF15");
        scelte[2] = java.util.ResourceBundle.getBundle("JIF").getString("MESSAGE_CANCEL");
        
        // If file is saved then close; otherwise, prompt with close options
        int result = getSelectedTitle().indexOf("*") == -1  ?
            1 :
            JOptionPane.showOptionDialog(
                null, 
                java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF16"),
                java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF17") + 
                getSelectedPath(),
                0,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                scelte,
                scelte[2]
                );
        
        switch (result) {
            // Save
            case 0:
                fileSave();
            // Close
            case 1:
                fileClose();
            // Cancel
            case 2:
            default:
                return;
        }
    }
    
    /**
     * Search for the definition of a constant within some Inform source.
     *
     * @param cb
     *              The Inform source code to search
     * @param entity
     *              The constant definition to search for
     * @return <code>true</code> if the constant defintion is found
     */
    boolean fileConstantCheck(CharBuffer cb, String entity) {
        Matcher m = constantPattern.matcher(cb);
        while (m.find()) {
            if (m.group(1).equalsIgnoreCase(entity)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Search for an Inform source code definition within a file.
     * The definition could be a constant, a class, a function, a global or an
     * object.
     *
     * @param path
     *              The file of Inform source code to search
     * @param entity
     *              The definition to search for
     * @return <code>true</code> if the definition is found
     */
    boolean fileDefinitionCheck(JifFileName path, String entity) {
        if (path.getContentType() != JifFileName.INFORM) {
            return false;
        }
        
        try {
            CharBuffer cb = JifDAO.buffer(new File(path.getPath()));
            
            return fileObjectCheck(cb, entity) ||
                    fileGlobalCheck(cb, entity) ||
                    fileConstantCheck(cb, entity) ||
                    fileClassCheck(cb, entity) ||
                    fileFunctionCheck(cb, entity) ||
                    fileProjectClassCheck(cb, entity);
            
        } catch (CharacterCodingException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    private void fileEdit(JifFileName fileName, File file) {
        
        // Check whether the file is already being edited
        if (isOpen(fileName)) {
            return;
        }
        
        JifEditorKit kit = isSyntaxHighlighting()
                ? fileName.createEditorKit()
                : new JifEditorKit();

        JifDocument doc = kit.createDefaultDocument(config.getContext());

        JifTextPane tp = new JifTextPane(this, fileName, file, config.getContext());
        tp.setWrap(config.getWrapLines());
        
        JifScrollPane sp = new JifScrollPane(tp, fileName);
        sp.setToolTipText(fileName.getPath());
        sp.setLineNumbers(config.getNumberLines());
 
        fileTabbedPane.add(sp, fileName.getTabTitle());

        buffer.put(fileName, sp);
        updateBuffer();
        updateFile(fileName);
    }
    
    /**
     * Search for the definition of a function within some Inform source.
     *
     * @param cb
     *              The Inform source code to search
     * @param entity
     *              The function definition to search for
     * @return <code>true</code> if the function definition is found
     */
    boolean fileFunctionCheck(CharBuffer cb, String entity) {
        Matcher m = functionPattern.matcher(cb);
        while (m.find()) {
            if (m.group(1).equalsIgnoreCase(entity) || m.group(1).equalsIgnoreCase(entity + "sub")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Search for the definition of a global within some Inform source.
     *
     * @param cb
     *              The Inform source code to search
     * @param entity
     *              The global definition to search for
     * @return <code>true</code> if the global definition is found
     */
    boolean fileGlobalCheck(CharBuffer cb, String entity) {
        Matcher m = globalPattern.matcher(cb);
        while (m.find()) {
            if (m.group(1).equalsIgnoreCase(entity)) {
                return true;
            }
        }
        return false;
    }
    
    private void fileMissingMessage(String path) {
        JOptionPane.showMessageDialog(
                this,
                java.util.ResourceBundle.getBundle("JIF").getString("ERR_OPENFILE2") +
                " " + path + " " +
                java.util.ResourceBundle.getBundle("JIF").getString("ERR_NAMEFILE3"),
                java.util.ResourceBundle.getBundle("JIF").getString("ERR_OPENFILE1"),
                JOptionPane.ERROR_MESSAGE);
    }
    
    private void fileNew() {

        // Generate file name
        JifFileName fileName = new JifFileName(
                resolveAbsolutePath(config.getWorkingDirectory(), config.getGamePath()) +
                File.separator +
                java.util.ResourceBundle.getBundle("JIF").getString("MSG_NEWFILE3") +
                (countNewFile++) +
                ".inf"
                );
        
        fileEdit(fileName, null);
        fileSelect(fileName);
    }
    
    /**
     * Search for the definition of an object within some Inform source.
     *
     * @param cb
     *              The Inform source code to search
     * @param entity
     *              The object definition to search for
     * @return <code>true</code> if the class definition is found
     */
    boolean fileObjectCheck(CharBuffer cb, String entity) {
        Matcher m = objectPattern.matcher(cb);
        while (m.find()) {
            if (m.group(1).equalsIgnoreCase(entity)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Create Inform source file assets for code tree
     * 
     * @param m
     *              Pattern matcher to use to identify asset in Inform source file
     * @param asset
     *              <code>InformAsset</code> to which the result should be added
     * @param parentName
     *              Name of parent inform asset
     */
    private void fileObjectCreate(Matcher m, InformAsset asset, String parentName) {
        while (m.find()) {
            String name = m.group(1).toLowerCase();
            int position = m.start(1);
            
            InformAsset object = new InformAsset(getSelectedPath(), name, position);
            object.setParent(parentName);
            object.setAllowChildren(false);
            asset.addChild(name);
            fileObjects.put(name, object);
        }
    }
    
    /**
     * Find all the objects with class references in some Inform source code
     *
     * @param cb
     *              Inform source to search for class to class references
     * @param path
     *              Inform source path
     */
    private void fileObjectToClassScan(CharBuffer cb, String path) {
        
        Matcher m = objectToClassPattern.matcher(cb);
        while(m.find()) {
            String childName  = m.group(1).toLowerCase();
            String parentName = m.group(2).toLowerCase();
            
            InformAsset child  = getAsset(fileObjects, childName);
            InformAsset parent = getAsset(fileAssets, parentName);
            
            child.setParent(parentName);
            parent.addChild(childName);
            
        }
    }
    
    /**
     * Find all the object to class references in some Inform source code
     *
     * @param cb
     *              Inform source to search for object to class references
     * @param asset
     *              The class definition to which any class references found
     *              should be added
     */
    private void fileObjectToClassScan(CharBuffer cb, InformAsset asset) {
        String parentName = asset.getName();
        // Object class references (first only) using regular expression
        Pattern p = Pattern.compile(
                "\n+\\s*(?:Object|Nearby)\\s+(?:->\\s+)*(\\w+)\\s+(\"[^\"]+\")*\\s*(?:\\w+)*\\s*class\\s+" + parentName,
                Pattern.MULTILINE|Pattern.CASE_INSENSITIVE
                );
        Matcher m = p.matcher(cb);
        fileObjectCreate(m, asset, parentName);
    }
    
    // funzione per l'apertura di file
    private void fileOpenDialog() {
        
        // File chooser dialog to obtain file names
        JFileChooser chooser;
        if (config.getLastFile() != null) {
            chooser = new JFileChooser(config.getLastFileDirectory());
        } else {
            chooser = new JFileChooser(resolveAbsolutePath(config.getWorkingDirectory(), config.getGamePath()));
        }
        
        JifFileFilter infFilter = new JifFileFilter("inf", java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF7"));
        infFilter.addExtension("h");
        infFilter.addExtension("res");
        infFilter.addExtension("txt");
        chooser.setFileFilter(infFilter);
        chooser.setMultiSelectionEnabled(true);
        
        if (chooser.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
            return;
        }
        
        File[] files = chooser.getSelectedFiles();
        
        for (int i = 0; i < files.length; i++) {
            fileOpen(files[i]);
        }
        
        // Select last file choosen
        fileSelect(new JifFileName(files[files.length-1].getAbsolutePath()));
    }
    
    private void fileOpen(String path) {
        
        // Check file exists
        File file = new File(path);
        if (!file.exists()) {
            fileMissingMessage(path);
            return;
        }
        fileOpen(file);
        fileSelect(new JifFileName(file.getAbsolutePath()));
    }

    private void fileOpen(File file) {
        fileEdit(new JifFileName(file.getAbsolutePath()), file);
        config.setLastFile(file.getAbsolutePath());
        config.addRecentFile(file.getAbsolutePath());
        adjustSplit();
    }
    
    boolean fileProjectClassCheck(CharBuffer cb, String entity) {
        for (Iterator j=project.getClasses().keySet().iterator(); j.hasNext(); ) {
            if (fileClassObjectCheck(cb, (String) j.next(), entity)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Save the selected file
     */
    private void fileSave() {
        if (getSelectedTitle().endsWith("*")) {
            String newtitle = getSelectedTitle().substring(0, getSelectedTitle().length()-1);
            setSelectedTitle(newtitle);
        }
        
        File file = new File(getSelectedPath());
        try {
            // The internal line separator character ("\n") is replaced with the 
            // system line separator for the current environment
            JifDAO.save(
                    file, 
                    Utils.replace(getText(), "\n", System.getProperty("line.separator"))
                    );
            
            StringBuilder strb = new StringBuilder(java.util.ResourceBundle.getBundle("JIF").getString("OK_SAVE3"));
            strb.append(getSelectedPath());
            strb.append(java.util.ResourceBundle.getBundle("JIF").getString("OK_SAVE4"));
            outputAppend(strb.toString());
            
            updateTitle();
            
        } catch (IOException ex) {
            System.err.println("Save file: " + ex.getMessage());
        }
    }
    
    /**
     * Save currently selected file to a new path
     *
     * @param directory
     *              Optional base directory to use for new path
     */
    private void fileSaveAs(String directory) {
        // recupero il nuovo nome del file e lo salvo....
        JFileChooser chooser;
        if (directory != null) {
            chooser  = new JFileChooser(directory);
        } else if (config.getLastFile() != null) {
            chooser = new JFileChooser(config.getLastFileDirectory());
        } else {
            chooser = new JFileChooser(resolveAbsolutePath(config.getWorkingDirectory(), config.getGamePath()));
        }
        chooser.setDialogTitle(java.util.ResourceBundle.getBundle("JIF").getString("MENUITEM_SAVEAS"));
        chooser.setApproveButtonText(java.util.ResourceBundle.getBundle("JIF").getString("MESSAGE_SAVE"));
        
        // Selezione Multipla
        chooser.setMultiSelectionEnabled(false);
        
        if (chooser.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
            return;
        }
        
        File file = chooser.getSelectedFile();
        String result = file.getAbsolutePath();
        
        // se il file non ha estensione: gliela inserisco io INF
        if (result.lastIndexOf(".") == -1) {
            result = result + ".inf";
        }
        
        // se l'utente ha inserito una cosa del tipo...
        // nome.cognome -> il nome viene convertito in nome.cognome.inf
        // controllo che l'utente non abbia scritto nome.txt, nome.res ecc
        if (((result.lastIndexOf(".") != -1) && (result.lastIndexOf(".inf")) == -1)
        &&
                (
                !(result.endsWith(".res"))
                &&
                !(result.endsWith(".txt"))
                &&
                !(result.endsWith(".h"))
                &&
                !(result.endsWith(".doc"))
                )) {
            result = result + ".inf";
        }
        
        // controllo che non esista già  un file con quel nome
        if (file.exists()) {
            if (JOptionPane.showConfirmDialog(
                    this,
                    java.util.ResourceBundle.getBundle("JIF").getString("ERR_NAMEFILE4"),
                    java.util.ResourceBundle.getBundle("JIF").getString("ERR_NAMEFILE2"),
                    JOptionPane.ERROR_MESSAGE
                    )
                    == JOptionPane.NO_OPTION) {
                return;
            }
        }
        
        JifFileName newName = new JifFileName(result);
        
        buffer.remove(getSelectedFileName());
        buffer.put(newName, getSelectedScrollPane());
        
        setSelectedTitle(newName.getTabTitle());
        setSelectedFileName(newName.getPath());
        
        fileSave();
        fileSelect(newName);
    }
    
    private void fileSaveDialog() {
        outputClear();
        fileSave();
    }
    
    void fileSelect(JifFileName path) {
        JifScrollPane sp = (JifScrollPane) buffer.get(path);
        setSelectedIndex(fileTabbedPane.indexOfComponent(sp));
        treeRefresh();
        updateTitle();
    }
    
    private void fileTabClick() {
        if (getFileTabCount()==0) {
            return;
        }
        fileSelect(getSelectedFileName());
    }
    
    // --- Frame ---------------------------------------------------------------

    private void frameMoved() {
        config.setFrameX(getX());
        config.setFrameY(getY());
    }

    private void frameResized() {
        config.setFrameHeight(getHeight());
        config.setFrameWidth(getWidth());
    }
    // --- Option dialog -------------------------------------------------------
    
    private void optionAttributeColor() {
        Color color = optionContext.getForeground(InformSyntax.Attribute);
        attributeColorLabel.setForeground(color);
        attributeColorButton.setBackground(color);
    }
    
    void optionBackground() {
        
        Color color = JColorChooser.showDialog(
                this,
                "Color Dialog",
                optionContext.getBackground()
                );
        if (color == null) {
            return;
        }
        optionContext.setBackground(color);
    }
    
    private void optionBackgroundColor() {
        Color color =  optionContext.getBackground();
        backgroundColorLabel.setForeground(color);
        backgroundColorButton.setBackground(color);
        colorEditorPane.setBackground(color);
        highlightEditorPane.setBackground(color);
    }
    
    private void optionBookmarksColor() {
        Color color =  optionContext.getForeground(InformSyntax.Bookmarks);
        bookmarkColorLabel.setForeground(color);
        bookmarkColorButton.setBackground(color);
    }
    
    private void optionBracketsColor() {
        Color color =  optionContext.getForeground(InformSyntax.Brackets);
        bracketColorLabel.setForeground(color);
        bracketColorButton.setBackground(color);
    }
    
    private void optionColor() {
        // Background colour
        optionBackgroundColor();
        // Syntax colours
        optionAttributeColor();
        optionCommentColor();
        optionKeywordColor();
        optionNormalColor();
        optionNumberColor();
        optionPropertyColor();
        optionStringColor();
        optionVerbColor();
        optionWhiteColor();
        optionWordColor();
    }
    
    void optionColor(InformSyntax style) {
        
        Color color = JColorChooser.showDialog(
                this,
                "Color Dialog",
                optionContext.getForeground(style)
                );
        if (color == null) {
            return;
        }
        optionContext.setForeground(style, color);
    }

    // Update color editor settings to dark color defaults
    private void optionColorDefaultDark() {
        optionContext.defaultDarkColors();
        optionColor();
    }
    
    // Update color editor settings to light color defaults
    private void optionColorDefaultLight() {
        optionContext.defaultLightColors();
        optionColor();
    }
    
    private void optionCommentColor() {
        Color color = optionContext.getForeground(InformSyntax.Comment);
        commentColorLabel.setForeground(color);
        commentColorButton.setBackground(color);
    }
    
    // Update option settings to defaults (light color)
    private void optionDefault() {
        optionGeneralDefault();
        optionColorDefaultLight();
        optionFontDefault();
        optionHighlightDefaultLight();
        optionPaths();
    }
    
    private void optionDialog() {
        // Build general tab from current settings
        adventInLibCheckBox.setSelected(config.getAdventInLib());
        createNewFileCheckBox.setSelected(config.getCreateNewFile());
        helpedCodeCheckBox.setSelected(config.getHelpedCode());
        makeResourceCheckBox.setSelected(config.getMakeResource());
        mappingLiveCheckBox.setSelected(config.getMappingLive());
        numberLinesCheckBox.setSelected(config.getNumberLines());
        openLastFileCheckBox.setSelected(config.getOpenLastFile());
        projectOpenAllFilesCheckBox.setSelected(config.getOpenProjectFiles());
        scanProjectFilesCheckBox.setSelected(config.getScanProjectFiles());
        syntaxCheckBox.setSelected(config.getSyntaxHighlighting());
        wrapLinesCheckBox.setSelected(config.getWrapLines());
        
        // Build colour and font tab from current settings
        optionContext.replaceStyles(config.getContext());
        optionEditorCreate(colorEditorPane);
        optionColor();
        optionFont();
        
        // Build highlight tab from current settings
        optionEditorCreate(highlightEditorPane);
        optionHighlightColor();
        
        // Build path tabs from current settings
        optionPaths();
        
        // Display options
        optionShow();
    }
    
    private void optionEditorCreate(JEditorPane editor) {
        editor.setDoubleBuffered(false);
        editor.setEditorKit(new InformEditorKit());
        editor.setEditable(false);
        editor.setBackground(optionContext.getBackground());
        editor.setDocument(new InformDocument(optionContext));
        StringBuilder sb = new StringBuilder();
        sb
                .append("! Poisoned Apple\n")
                .append("Object  apple \"Poisoned Apple\"\n")
                .append("with\n")
                .append("  description \"It's a red apple.\",\n")
                .append("  name \'apple\' \'red\' \'poisoned\',\n")
                .append("  number 1234,\n")
                .append("  before [;\n")
                .append("    Eat : \n")
                .append("    print \"This is a poisoned apple, isn't it?\"\n")
                .append("    return true;\n")
                .append("  ],\n")
                .append("has   light;\n");
        editor.setText(sb.toString());
    }
    
    private void optionErrorsColor() {
        Color color =  optionContext.getForeground(InformSyntax.Errors);
        errorColorLabel.setForeground(color);
        errorColorButton.setBackground(color);
    }
    
    private void optionFont() {
        fontNameComboBox.setSelectedItem(config.getFontName());
        Integer fontSize = new Integer(config.getFontSize());
        fontSizeComboBox.setSelectedItem(fontSize.toString());
        tabSizeTextField.setText(String.valueOf(JifEditorKit.getTabSize()));
    }

    // Change font editor settings to defaults
    private void optionFontDefault() {
        fontNameComboBox.setSelectedItem(InformContext.defaultFontName);
        int fontSize = InformContext.defaultFontSize;
        fontSizeComboBox.setSelectedItem((new Integer(fontSize)).toString());
        tabSizeTextField.setText(Integer.toString(InformContext.defaultTabSize));
    }
    
    // Change general editor settings to defaults
    private void optionGeneralDefault() {
        openLastFileCheckBox.setSelected(false);
        createNewFileCheckBox.setSelected(false);
        mappingLiveCheckBox.setSelected(false);
        projectOpenAllFilesCheckBox.setSelected(false);
        helpedCodeCheckBox.setSelected(true);
        numberLinesCheckBox.setSelected(true);
        scanProjectFilesCheckBox.setSelected(true);
        syntaxCheckBox.setSelected(true);
        wrapLinesCheckBox.setSelected(false);
    }
    
    void optionHide() {
        optionDialog.setVisible(false);
    }
    
    private void optionHighlightColor() {
        // Highlight colours
        optionBookmarksColor();
        optionBracketsColor();
        optionErrorsColor();
        optionJumpToColor();
        optionWarningsColor();
    }
    
    private void optionHighlightDefaultDark() {
        optionContext.defaultDarkSelections();
        optionHighlightColor();
        optionHighlightUpdate();
    }
    
    private void optionHighlightDefaultLight() {
        optionContext.defaultLightSelections();
        optionHighlightColor();
        optionHighlightUpdate();
    }
    
    private void optionHighlightUpdate() {
        String selection = (String) highlightSelectedComboBox.getSelectedItem();
        
        if (optionHighlight != null) {
            optionHighlight.removeHighlights(highlightEditorPane);
        }
        
        Color highlightColor = null;
            
        if (selection.equals("Bookmark"))
            highlightColor = optionContext.getForeground(InformSyntax.Bookmarks);
        else if (selection.equals("Bracket"))
            highlightColor = optionContext.getForeground(InformSyntax.Brackets);
        else if (selection.equals("Error"))
            highlightColor = optionContext.getForeground(InformSyntax.Errors);
        else if (selection.equals("JumpTo"))
            highlightColor = optionContext.getForeground(InformSyntax.JumpTo);
        else if (selection.equals("Warning"))
            highlightColor = optionContext.getForeground(InformSyntax.Warnings);
        
        optionHighlight = new HighlightText(
                highlightEditorPane,
                highlightColor);
        optionHighlight.highlightFromTo(highlightEditorPane, 17, 159);
    }
    
    private void optionJumpToColor() {
        Color color =  optionContext.getForeground(InformSyntax.JumpTo);
        jumpToColorLabel.setForeground(color);
        jumpToColorButton.setBackground(color);
    }
    
    private void optionKeywordColor() {
        Color color = optionContext.getForeground(InformSyntax.Keyword);
        keywordColorLabel.setForeground(color);
        keywordColorButton.setBackground(color);
    }
    
    private void optionNormalColor() {
        Color color = optionContext.getForeground(InformSyntax.Normal);
        normalColorLabel.setForeground(color);
        normalColorButton.setBackground(color);
    }
    
    private void optionNumberColor() {
        Color color = optionContext.getForeground(InformSyntax.Number);
        numberColorLabel.setForeground(color);
        numberColorButton.setBackground(color);
    }
    
    private void optionPaths() {
        
        // Compiler path tab
        compilerPathTextField.setText(config.getCompilerPath());
        gamePathTextField.setText(config.getGamePath());
        interpreterPathTextField.setText(config.getInterpreterZcodePath());
        
        // Library path tab
        libraryPath0TextField.setText(config.getLibraryPath());
        libraryPath1TextField.setText(config.getLibraryPath1());
        libraryPath2TextField.setText(config.getLibraryPath2());
        libraryPath3TextField.setText(config.getLibraryPath3());
        
        // Glulx path tab
        glulxPathTextField.setText(config.getInterpreterGlulxPath());
        bresPathTextField.setText(config.getBresPath());
        blcPathTextField.setText(config.getBlcPath());
        
    }
    
    private void optionPropertyColor() {
        Color color = optionContext.getForeground(InformSyntax.Property);
        propertyColorLabel.setForeground(color);
        propertyColorButton.setBackground(color);
    }
    
    private void optionSave() {
        // Apply general tab settings
        config.setAdventInLib(adventInLibCheckBox.isSelected());
        config.setCreateNewFile(createNewFileCheckBox.isSelected());
        config.setHelpedCode(helpedCodeCheckBox.isSelected());
        config.setMappingLive(mappingLiveCheckBox.isSelected());
        config.setNumberLines(numberLinesCheckBox.isSelected());
        config.setOpenLastFile(openLastFileCheckBox.isSelected());
        config.setOpenProjectFiles(projectOpenAllFilesCheckBox.isSelected());
        config.setScanProjectFiles(scanProjectFilesCheckBox.isSelected());
        config.setSyntaxHighlighting(syntaxCheckBox.isSelected());
        config.setWrapLines(wrapLinesCheckBox.isSelected());
        
        // Apply tab size settings
        config.setTabSize(optionTabSize);
        JifEditorKit.setTabSize(optionTabSize);
        
        // Update the common syntax highlighting styles with color editor styles
        config.setContext(optionContext);
        
        // Apply compiler tab path settings
        config.setGamePath(gamePathTextField.getText());
        config.setCompilerPath(compilerPathTextField.getText());
        config.setInterpreterZcodePath(interpreterPathTextField.getText());
        
        // Apply library tab paths
        config.setLibraryPath(libraryPath0TextField.getText());
        config.setLibraryPath1(libraryPath1TextField.getText());
        config.setLibraryPath2(libraryPath2TextField.getText());
        config.setLibraryPath3(libraryPath3TextField.getText());
        
        // Apply Glulx tab paths
        config.setInterpreterGlulxPath(glulxPathTextField.getText());
        config.setBresPath(bresPathTextField.getText());
        config.setBlcPath(blcPathTextField.getText());
        
        configurationSave();
        optionHide();
        adjustSplit();
    }

    void optionShow() {
        //optionDialog.pack();
        optionDialog.setSize(650, 600);
        optionDialog.setLocationRelativeTo(this);
        optionDialog.setVisible(true);
    }
    
    private void optionStringColor() {
        Color color = optionContext.getForeground(InformSyntax.String);
        stringColorLabel.setForeground(color);
        stringColorButton.setBackground(color);
    }
    
    private void optionVerbColor() {
        Color color = optionContext.getForeground(InformSyntax.Verb);
        verbColorLabel.setForeground(color);
        verbColorButton.setBackground(color);
    }
    
    private void optionWarningsColor() {
        Color color =  optionContext.getForeground(InformSyntax.Warnings);
        warningColorLabel.setForeground(color);
        warningColorButton.setBackground(color);
    }
    
    private void optionWhiteColor() {
        Color color = optionContext.getForeground(InformSyntax.White);
        wordColorLabel.setForeground(color);
        wordColorButton.setBackground(color);
    }
    
    private void optionWordColor() {
        Color color = optionContext.getForeground(InformSyntax.Word);
        wordColorLabel.setForeground(color);
        wordColorButton.setBackground(color);
    }
    
    // --- Output messages -----------------------------------------------------

    void outputAppend(String text) {
        outputTextArea.append(text);
    }
    
    void outputClear() {
        outputTextArea.setText("");
    }
    
    void outputClearHighlights() {
        hlighterOutputErrors.removeHighlights(outputTextArea);
    }
    
    // When the user clicks on a compilation error/warning message or a search
    // result in the output pane, jump to the line and file in the message.
    void outputClick(MouseEvent evt) {
        
        try {
            int riga = 0;
            
            // Rescues the correct line
            Element el = outputMouseElement(evt);
            String ultima = outputTextArea.getText(el.getStartOffset(), el.getEndOffset()-el.getStartOffset());
            
            // Only if the line starts with the comment character
            if (ultima.indexOf(Constants.TOKENCOMMENT)!=-1 &&
                    ((ultima.indexOf(".inf") != -1) || (ultima.indexOf(".h") != -1))) {
                
                // Errors in E1 format
                if (Utils.IgnoreCaseIndexOf(ultima, "line ") == -1) {
                    // Removing all highlights from the output window
                    outputClearHighlights();
                    
                    StringTokenizer stok = new StringTokenizer(ultima, "#()");
                    
                    synchronized (this) {
                        fileOpen(stok.nextToken());
                    }
                    
                    riga = Integer.parseInt(stok.nextToken()) - 1;
                    
                    // Clear previous error and warning highlights
                    getSelectedTextPane().removeHighlighterErrors();
                    getSelectedTextPane().removeHighlighterWarnings();
                    
                    // Find and highlight the line with the error or warning
                    if (Utils.IgnoreCaseIndexOf(ultima, "warning")==-1) {
                        getSelectedTextPane().jumpToError(riga);
                    } else {
                        getSelectedTextPane().jumpToWarning(riga);
                    }
                    
                } else {
                    // Errors in E0-E2 format
                    outputFormatMessage();
                    return;
                }
            } else if (ultima.indexOf(Constants.TOKENCOMMENT) != -1 && Utils.IgnoreCaseIndexOf(ultima, "file") == -1) {
                outputFormatMessage();
                return;
            } else if (ultima.startsWith(Constants.TOKENSEARCH)) {
                // Find text from all files search function
                
                // Highlight output line in error color
                outputClearHighlights();
                outputSetError(el);
                
                StringTokenizer stok = new StringTokenizer(ultima, Constants.TOKENSEARCH);
                
                synchronized (this) {
                    fileOpen(stok.nextToken());
                }
                
                riga = Integer.parseInt(stok.nextToken()) - 1;
                
                // Find and highlight the line found by the search
                getSelectedTextPane().jumpToText(riga);
                
            } else {
                return;
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    private void outputFormatMessage() {
        JOptionPane.showMessageDialog(
                this,
                "Please, use the -E1 error switch.",
                "Jump to Error",
                JOptionPane.INFORMATION_MESSAGE
                );
    }
    
    void outputHide() {
        outputTabbedPane.setVisible(false);
    }
    
    void outputInit() {
        outputClear();
        config.setOutputVisible(true);
        adjustSplit();
    }
    
    void outputMouse(MouseEvent evt) {
        try {
            // Rescues the correct line
            Element el = outputMouseElement(evt);
            String ultima = outputTextArea.getText(el.getStartOffset(), el.getEndOffset()-el.getStartOffset());
            
            if (ultima.startsWith(Constants.TOKENCOMMENT) || ultima.startsWith(Constants.TOKENSEARCH) ) {
                
                outputClearHighlights();
                
                if (Utils.IgnoreCaseIndexOf(ultima, "error") != -1) {
                    // In case of errors
                    outputSetError(el);
                } else {
                    // In case of warnings
                    outputSetWarning(el);
                }
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
    
    Element outputMouseElement(MouseEvent evt) {
        Element el = outputTextArea.getDocument().getDefaultRootElement();
        int ind = el.getElementIndex(outputTextArea.viewToModel(evt.getPoint()));
        return outputTextArea.getDocument().getDefaultRootElement().getElement(ind);
    }
   
    void outputSetCaretPosition(int pos) {
        outputTextArea.setCaretPosition(pos);
    }
    
    void outputSetError(Element el) {
        hlighterOutputErrors.highlightFromTo(
                outputTextArea,
                el.getStartOffset(),
                el.getEndOffset()
                );
    }
    
    void outputSetText(String text) {
        outputTextArea.setText(text);
    }
    
    void outputSetWarning(Element el) {
        hlighterOutputWarnings.highlightFromTo(
                outputTextArea,
                el.getStartOffset(),
                el.getEndOffset()
                );
    }
    
    void outputShow() {
        mainSplitPane.setBottomComponent(outputTabbedPane);
        outputTabbedPane.setSelectedComponent(outputScrollPane);
        outputTabbedPane.setVisible(true);
    }
    
    // --- Project -------------------------------------------------------------

    /**
     * Scan for class definitions in all the Inform source files within the
     * currently open project
     */
    private void projectClassScan() {
        for (Iterator i = project.iterator(); i.hasNext(); ) {
            JifFileName projectFile = (JifFileName) i.next();
            if (projectFile.getContentType() == JifFileName.INFORM) {
                File file = new File(projectFile.getPath());
                try {
                    fileClassScan(JifDAO.buffer(file), projectFile.getPath());
                } catch (Exception ex) {
                    System.err.println("Seek classes " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Scan for class to class references in all the Inform source files within
     * the currently open project
     */
    private void projectClassToClassScan() {        
        for (Iterator i = project.iterator(); i.hasNext(); ) {
            JifFileName projectFile = (JifFileName) i.next();
            if (projectFile.getContentType() == JifFileName.INFORM) {
                File file = new File(projectFile.getPath());
                try {
                    fileClassToClassScan(JifDAO.buffer(file), projectFile.getPath());                    
                } catch (Exception ex) {
                    System.err.println("Seek class to class " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Scan for object to class references in all the Inform source files within
     * the currently open project
     */
    private void projectObjectToClassScan() {        
        for (Iterator i = project.iterator(); i.hasNext(); ) {
            JifFileName projectFile = (JifFileName) i.next();
            if (projectFile.getContentType() == JifFileName.INFORM) {
                File file = new File(projectFile.getPath());
                try {
                    fileObjectToClassScan(JifDAO.buffer(file), projectFile.getPath());                    
                } catch (Exception ex) {
                    System.err.println("Seek class to class " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private void projectClick(MouseEvent evt) {
        // Double clicking an entry, JIF opens the selected file
        if (evt.getClickCount()==2) {
            // opens the file
            JifFileName fp = getSelectedProjectFile();
            if (fp != null) {
                fileOpen(fp.getPath());
            }
        } else {
            // Sets the tooltip in case of One click
            JifFileName fp = getSelectedProjectFile();
            if (fp != null) {
                projectList.setToolTipText(fp.getPath());
            }
        }
    }
    
    // chiude un progetto. Inserire un flag per chiudere tutti i files
    // relativi ad un progetto quando si chiude il progetto stesso
    private void projectClose() {
        projectFilesClose();
        project.clear();
    }

    /**
     * Search for the file within a project that contains an Inform source code
     * definition. The definition could be a constant, a class, a function,  
     * a global (including arrays) or an object.
     *
     * @param entity
     *              The definition to search for
     * @return The name of the file containing the defintion
     */
    String projectDefinitionCheck(String entity) {
        for (Iterator i = project.iterator(); i.hasNext(); ) {
            JifFileName file = (JifFileName) i.next();
            
            if (fileDefinitionCheck(file, entity)) {
                return file.getPath();
            }
        }
        return null;
    }
    
    private void projectEntered() {
        // Just One click: shows the tooltip
        JifFileName fp = getSelectedProjectFile();
        if (fp != null) {
            projectList.setToolTipText(fp.getPath());
        } else {
            projectList.setToolTipText(null);
        }
    }
    
    // funzione che gestisce l'inserimento di files in un progetto
    // supporta la seleziona multipla
    private void projectFileAdd() {
        // Open the project file directory to add the files
        String dir = project.getDirectory();
        JFileChooser chooser = new JFileChooser(dir);
        JifFileFilter infFilter = new JifFileFilter("inf", java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF7"));
        infFilter.addExtension("h");
        infFilter.addExtension("res");
        infFilter.addExtension("txt");
        chooser.setFileFilter(infFilter);
        // Selezione Multipla
        chooser.setMultiSelectionEnabled(true);
        
        if (chooser.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
            return;
        }
        
        File[] files = chooser.getSelectedFiles();
        File file;
        
        for (int i=0 ; i<files.length; i++) {
            file = files[i];
            // apro il file e lo aggiungo alla lista se il checkbox è attivo
            if (config.getOpenProjectFiles()) {
                fileOpen(file.getAbsolutePath());
            }
            
            project.addFile(file.getAbsolutePath());
        }
        projectSave();
    }
    
    // Creates a new file and append this to the project
    private void projectFileNew() {
        fileNew();
        fileSaveAs(project.getDirectory());
        project.addFile(getSelectedPath());
        projectSave();
    }
    
    // Remove a file from the project
    private void projectFileRemove() {

        JifFileName fp = getSelectedProjectFile();
        if (fp == null) {
            return;
        }
        
        // Confirm file removal message
        String message = (project.isMain(fp)) ?
            java.util.ResourceBundle.getBundle("JIF").getString("PROJECT_DELETE_MAIN_FROM_PROJECT"):
            java.util.ResourceBundle.getBundle("JIF").getString("PROJECT_DELETE_FILE_FROM_PROJECT");
        
        // Confirm file removal
        if (JOptionPane.showConfirmDialog(
                this,
                message,
                "File : " + fp.getName(),
                JOptionPane.OK_CANCEL_OPTION
                )
                == JOptionPane.CANCEL_OPTION) {
            return;
        }
        
        // Remove the file and save the project
        project.removeFile(fp);
        projectSave();
    }

    // Close all currently open project files
    private void projectFilesClose() {
        int numberOfComponents = getFileTabCount();
        for (int i=getFileTabCount()-1; i >= 0; i--) {
            if (project.contains(getPathAt(i))) {
                fileSelect(new JifFileName(getPathAt(i)));
                fileCloseDialog();
            }
        }
    }
    
    // Open all the file of a project
    private void projectFilesOpen() {
        
        int fileCount = getFileTabCount();
        
        for (Iterator i = project.iterator(); i.hasNext(); ) {
            JifFileName projectFile = (JifFileName) i.next();
            // don't automatically open header files
            if (!projectFile.getType().equals("h")) {
                fileOpen(projectFile.getPath());
            }
        }

        // Select first project file opened (if any)
        if (fileCount != getFileTabCount()) {
            fileSelect(getFileNameAt(fileCount));
        }
    }
    
    private void projectFilesScan() {
        fileAssets.clear();
        fileObjects.clear();
        projectClassScan();
        projectClassToClassScan();
        projectObjectToClassScan();
        project.addClasses(fileAssets);
    }
    
    private void projectMainClear() {
        JOptionPane.showMessageDialog(
                this,
                "Clearing Main file",
                "Main file",
                JOptionPane.INFORMATION_MESSAGE
                );
        project.clearMain();
    }
    
    private void projectMainSet() {
        JifFileName fp = getSelectedProjectFile();
        if (fp == null || !fp.getType().equals("inf")) {
            return;
        }
        project.setMain(fp);
    }
    
    /**
     * Creates a new project that inherits default switches and mode from the
     * configuration settings
     */
    private void projectNewDialog() {
        
        try {
            JFileChooser chooser;
            chooser  = new JFileChooser();
            chooser.setDialogTitle(java.util.ResourceBundle.getBundle("JIF").getString("PROJECT_NEW_PROJECT"));
            chooser.setApproveButtonText(java.util.ResourceBundle.getBundle("JIF").getString("MESSAGE_SAVE"));
            chooser.setMultiSelectionEnabled(false);
            if (chooser.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
                return;
            }
            File file = chooser.getSelectedFile();
            
            if (file.getName().indexOf(".jpf") == -1) {
                // Add the .jpf extension
                file = new File(file.getAbsolutePath() + ".jpf");
            }
            
            if (file.exists()) {
                int result =  JOptionPane.showConfirmDialog(
                        this,
                        java.util.ResourceBundle.getBundle("JIF").getString("PROJECT_PROJECT_EXISTS_OVERWRITE"),
                        file.getAbsolutePath(),
                        JOptionPane.OK_CANCEL_OPTION
                        );
                if (result == JOptionPane.CANCEL_OPTION) {
                    return;
                }
                file.delete();
            }
            
            project.clear();
            project.setFile(file.getAbsolutePath());
            project.setSwitches(config.getSwitches());
            project.setInformMode(config.getInformMode());
            
            projectSave();
            projectSaveMessage();
            
        } catch (Exception ex) {
            System.err.println("New project: " + ex.getMessage());
        }
    }
    
    private void projectOpen(File projectPath) {
        
        // Check whether another project is already open
        if (project.isOpen()) {
            projectSave();
            projectClose();
        }
        
        project.setFile(projectPath.getAbsolutePath());
        projectReload();
        
        // Open all project files (except header files) if neccesary
        if (config.getOpenProjectFiles()) {
            projectFilesOpen();
        } else {
            // Scan project files for classes if neccesary
            if (config.getScanProjectFiles()) {
                projectFilesScan();
            }            
        }
        
        // Display the project panel
        projectSelect();
        
        // Last Project opened
        config.setLastProject(projectPath.getAbsolutePath());
        
        adjustSplit();
    }

    private void projectOpenDialog() {
        
        String search = (config.getLastProject() == null)
            ? config.getWorkingDirectory()
            : config.getLastProjectPath();
        
        JFileChooser chooser = new JFileChooser(search);
        JifFileFilter infFilter = new JifFileFilter("jpf", "Jif Project File");
        chooser.setFileFilter(infFilter);
        if (chooser.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
            return;
        }
        
        projectOpen(chooser.getSelectedFile());
    }
    
    private void projectOpenSelected(Object[] oggetti) {
        if (oggetti.length == 0) {
            return;
        }
        for (int i=0; i < oggetti.length; i++) {
            if (oggetti[i] != null) {
                fileOpen(((JifFileName) oggetti[i]).getPath());
            }
        }
    }
    
    private void projectReload() {
        try {
            JifProjectDAO.reload(project);
        } catch (JifProjectException ex) {
            System.err.println("Unable to reload project: " + ex.getMessage());
        }
    }
    
    private void projectSaveDialog() {
        projectSave();
        projectSaveMessage();
    }
    
    private void projectSave() {
        try {
            JifProjectDAO.store(project);
        } catch (JifProjectException ex ) {
            System.err.println("Unable to save project: " + ex.getMessage());
        }
    }
    
    private void projectSaveMessage() {
        JOptionPane.showMessageDialog(
                this,
                project.getName() + " " +
                java.util.ResourceBundle.getBundle("JIF").getString("OK_SAVE4"),
                java.util.ResourceBundle.getBundle("JIF").getString("OK_SAVE2"),
                JOptionPane.INFORMATION_MESSAGE
                );
    }
    
    private void projectSelect() {
        leftTabbedPane.setSelectedIndex(1);
    }
    
    // --- Project properties dialog -------------------------------------------
    
    private void projectPropertiesDialog() {
        
        try {
            File file = new File(project.getPath());
            
            if (!file.exists()) {
                System.out.println(java.util.ResourceBundle.getBundle("JIF").getString("ERR_OPENFILE1") + project.getPath());
                return;
            }
            
            projectPropertiesTextArea.setText(JifDAO.read(file));
            projectPropertiesTextArea.setCaretPosition(0);
            
            projectPropertiesShow();
            
        } catch (Exception ex) {
            System.err.println("Project properties: " + ex.getMessage());
        }
    }
    
    void projectPropertiesHide() {
        projectPropertiesDialog.setVisible(false);
    }
    
    private void projectPropertiesSave() {
        // Save project properties file and reload project
        File file = new File(project.getPath());
        try {
            JifDAO.save(file, projectPropertiesTextArea.getText());
            JifProjectDAO.reload(project);
            projectPropertiesSaveMessage();
        } catch (Exception ex) {
            System.err.println("Project properties save: " + ex.getMessage());
        }
    }

    private void projectPropertiesSaveMessage() {
        JOptionPane.showMessageDialog(
                projectPropertiesDialog,
                java.util.ResourceBundle.getBundle("JIF").getString("OK_SAVE1"),
                java.util.ResourceBundle.getBundle("JIF").getString("OK_SAVE2"),
                JOptionPane.INFORMATION_MESSAGE
                );
    }
    
    void projectPropertiesShow() {
        projectPropertiesDialog.setSize(500,500);
        projectPropertiesDialog.setLocationRelativeTo(this);
        projectPropertiesDialog.setVisible(true);
    }
    
    // --- Project switches dialog ---------------------------------------------
    
    void projectSwitchesCreate() {
        projectSwitchesPanel.removeAll();
        for (Iterator i = project.getSwitches().entrySet().iterator(); i.hasNext(); ) {
            Entry e = (Entry) i.next();
            String name = (String) e.getKey();
            String setting = (String) e.getValue();
            Checkbox check = new Checkbox(name);
            //check.setFont(new Font("Monospaced", Font.PLAIN, 11));
            check.setFont(new Font("Dialog", Font.PLAIN, 12));
            check.setState(setting.equals("on") ? true : false);
            projectSwitchesPanel.add(check);
        }
    }
    
    void projectSwitchesDialog() {
        projectSwitchesCreate();
        projectSwitchesShow();
    }

    void projectSwitchesHide() {
        projectSwitchesDialog.setVisible(false);
    }
    
    void projectSwitchesSave() {
        for (int i=0; i < projectSwitchesPanel.getComponentCount(); i++) {
            Checkbox ch = (Checkbox) projectSwitchesPanel.getComponent(i);
            project.setSwitch(ch.getLabel(), (ch.getState())?"on":"off");
        }
        projectSave();
        projectSwitchesHide();
    }
    
    void projectSwitchesShow() {
        projectSwitchesDialog.setTitle("Project Switches");
        projectSwitchesDialog.pack();
        projectSwitchesDialog.setLocationRelativeTo(this);
        projectSwitchesDialog.setVisible(true);
    }
    
    // --- Replace dialog ------------------------------------------------------

    void replaceDialog() {
        // Default find field to current selection
        if (getSelectedText() !=  null) {
            replaceFindTextField.setText(getSelectedText());
        }
        selectedRequestFocus();
        setCaretPosition(0);
        replaceShow();
    }
    
    void replaceFind() {
        if (replaceFindTextField.getText().equals("")) {
            JOptionPane.showMessageDialog(
                    this,
                    java.util.ResourceBundle.getBundle("JIF").getString("ERR_EMPTY_STRING")
                    );
        }
        replaceFindString(replaceFindTextField.getText());
    }
    
    void replaceFindString(String pattern) {
        // rimuovo tutti gli highligh
        // recupero la posizione del cursore
        // eeguo la ricerca e l'highlight del testo trovato
        int pos = getCaretPosition();   // current position
        try {
            String text = getText();
            boolean trovato = false;
            while (((pos = text.indexOf(pattern, pos)) >= 0) && (!trovato)) {
                //while ( ( (pos = Utils.IgnoreCaseIndexOf(text, pattern, pos)) >= 0) && (!trovato)) {
                selectedRequestFocus();
                setCaretPosition(pos);
                setSelectionStart(pos);
                setSelectionEnd(pos + pattern.length());
                selectedRepaint();
                pos += pattern.length();
                trovato=true;
                replaceDialog.requestFocus();
            }
            
            //se non lo trovo comunico che sono alla fine del file
            if (!trovato) {
                String[] scelte = new String[2];
                scelte[0] = java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF18");
                scelte[1] = java.util.ResourceBundle.getBundle("JIF").getString("MESSAGE_CANCEL");
                int result = JOptionPane.showOptionDialog(
                        replaceDialog,
                        java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF19") + 
                        " [" + pattern + "] " + 
                        java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF20"),
                        java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF21"),
                        0,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        scelte,
                        scelte[1]);
                if (result==0) {
                    setCaretPosition(0);
                    replaceFindString(pattern);
                }
                return;
            }
        } catch (Exception ex) {
            System.err.println("Find string: " + ex.getMessage());
        }
    }
    
    boolean replaceFindAllString(String pattern) {
        // rimuovo tutti gli highlight
        // recupero la posizione del cursore
        // eeguo la ricerca e l'highlight del testo trovato
        
        int pos = getCaretPosition();   // current position
        try {
            String text = getText();
            boolean trovato = false;
            while (((pos = text.indexOf(pattern, pos)) >= 0) && (!trovato)) {
                selectedRequestFocus();
                setCaretPosition(pos);
                setSelectionStart(pos);
                setSelectionEnd(pos + pattern.length());
                selectedRepaint();
                pos += pattern.length();
                trovato = true;
                replaceDialog.requestFocus();
            }
            
            //se non lo trovo comunico che sono alla fine del file
            return !trovato;
                    
        } catch (Exception ex) {
            System.err.println("Find all: " + ex.getMessage());
        }
        return false;
    }
    
    void replaceHide() {
        replaceDialog.setVisible(false);
    }
    
    void replaceReplace() {
        // Replacing....only if there is a selected TEXT
        if (getSelectedText() == null) {
            return;
        }
        replaceSelection(replaceReplaceTextField.getText());
    }
    
    // 1. cerco la prima occorrenza se non esiste esci
    // 2. replace string e vai al punto 1.
    void replaceReplaceAll() {
        // Start at the beginning of the selected file
        setCaretPosition(0);
        boolean eseguito = false;
        while (!eseguito) {
            eseguito = replaceFindAllString(replaceFindTextField.getText());
            if (!eseguito) {
                replaceSelection(replaceReplaceTextField.getText());
            }
        }
        treeRefreshIncremental();
    }
    
    void replaceShow() {
        replaceDialog.pack();
        replaceDialog.setLocationRelativeTo(this);
        replaceDialog.setVisible(true);
    }
    
    // --- Search --------------------------------------------------------------

    private void searchAllDialog() {
        // se è presente una stringa uso quella altrimenti la prendo da quella selezionata
        // No: vince quella selezionata
        String target = null;
        if (searchProjectTextField.getText() != null && getSelectedText() != null) {
            target = getSelectedText();
        }

        if (target == null) {
            target = searchProjectTextField.getText();
        }

        if (target == null || target.trim().equals("")) {
            target = getSelectedText();
        }

        if (target != null && !target.trim().equals("")) {
            searchAllFiles(target);
        }
    }

    /**
     * Search for a string in all the files of a project
     *
     * @param target Search string to find in project files
     */
    private void searchAllFiles(String target) {
        if (project.isClosed()) {
            return;
        }

        outputInit();
        StringBuilder output = new StringBuilder();
        String result;
        for (Iterator i = project.iterator(); i.hasNext();) {
            JifFileName file = (JifFileName) i.next();
            result = Utils.searchString(target, new File(file.getPath()));
            if (result != null) {
                output.append(result).append("\n");
            }
        }
        outputSetText(output.toString());
        outputSetCaretPosition(0);
    }

    private void searchDialog() {
        String selezione = getSelectedText();
        String box = getFindText();
        String target = null;
        // vince la box
        if (box != null && !box.equals("")) {
            target = box;
        } else {
            target = selezione;
        }

        if (getSelectedTextPane().isFocusOwner() && selezione != null) {
            target = selezione;
        }

        setFindText(target);
        getSelectedTextPane().findString(this);
    }

    void searchSelect() {
        leftTabbedPane.setSelectedIndex(2);
    }

    // --- Symbol dialog -------------------------------------------------------

    void symbolsClick(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            symbolsInsert((String) symbolList.getSelectedValue());
        }
    }

    void symbolsHide() {
        symbolDialog.setVisible(false);
    }

    void symbolsInsert(String key) {
        try {
            insertString(getMapping(key).trim());
        } catch (BadLocationException ex) {
            System.err.println("Insert symbol failed: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            symbolsHide();
        }
    }

    void symbolsKey(KeyEvent evt) {
        //  Insert selected symbol with ENTER
        if ((evt.getKeyCode() == KeyEvent.VK_ENTER)) {
            symbolsInsert((String) symbolList.getSelectedValue());
            evt.consume();
            return;
        }
        // Hide symbol dialog with ESCAPE
        if ((evt.getKeyCode() == KeyEvent.VK_ESCAPE)) {
            symbolsHide();
            evt.consume();
            return;
        }
    }

    void symbolsShow() {
        try {
            int pointx = (int) getSelectedTextPane().modelToView(getCaretPosition()).getX();
            int pointy = (int) getSelectedTextPane().modelToView(getCaretPosition()).getY();
            symbolDialog.setLocation(
                    (int) getSelectedTextPane().getLocationOnScreen().getX() + pointx,
                    (int) getSelectedTextPane().getLocationOnScreen().getY() + pointy + 15);
            symbolDialog.setSize(230, 200);
            symbolDialog.requestFocus();
            symbolDialog.toFront();
            symbolDialog.setVisible(true);
        } catch (BadLocationException ex) {
            System.err.println("Show symbols: " + ex.getMessage());
        }
    }

    // --- Toolbar -------------------------------------------------------------

    void toolbarHide() {
        jToolBarCommon.setVisible(false);
    }

    void toolbarShow() {
        jToolBarCommon.setVisible(true);
    }

    // --- Tree ----------------------------------------------------------------
    
    private void treeClassesBuild() {

        classTree.removeAllChildren();

//        Set deletions = new TreeSet(fileNodes.keySet());
//        deletions.removeAll(fileAssets.keySet());
//        
//        for (Iterator i=deletions.iterator(); i.hasNext(); ) {
//            String name = (String) i.next();
//            InformTreeNode node = (InformTreeNode) fileNodes.get(name); 
//            ((InformTreeNode) node.getParent()).remove(node);
//            fileNodes.remove(name);
//        }

        for (Iterator i = fileAssets.entrySet().iterator(); i.hasNext();) {
            Entry e = (Entry) i.next();
            InformAsset asset = (InformAsset) e.getValue();

            // Only basic class definitions have no parent
            if (!asset.hasParent()) {
                treeNodeBuild(classTree, asset);
            }
        }

    }

    private void treeClassesRefresh(CharBuffer cb) {

        fileAssets.clear();
        fileObjects.clear();

        if (config.getScanProjectFiles() && project.contains(getSelectedFileName())) {
            project.clear(getSelectedName());
            fileAssets.putAll(project.getClasses());
        }

        fileClassScan(cb, getSelectedPath());
        fileClassToClassScan(cb, getSelectedPath());
        fileObjectToClassScan(cb, getSelectedPath());

        if (config.getScanProjectFiles() && project.contains(getSelectedFileName())) {
            project.addClasses(fileAssets);
        }

        for (Iterator i = fileAssets.entrySet().iterator(); i.hasNext();) {
            Entry e = (Entry) i.next();
            InformAsset asset = (InformAsset) e.getValue();
            fileObjectToClassScan(cb, asset);
            fileClassObjectScan(cb, asset);
        }

        fileAssets.putAll(fileObjects);
        treeClassesBuild();
        treeModel.reload(classTree);
    }

    void treeClear(String name) {
        top.setUserObject(name);
        globalTree.removeAllChildren();
        constantTree.removeAllChildren();
        objectTree.removeAllChildren();
        functionTree.removeAllChildren();
        classTree.removeAllChildren();
        treeModel.reload();
    }

    void treeClick() {

        if (getFileTabCount() == 0) {
            return;
        }

        InformTreeNode node = (InformTreeNode) codeTree.getLastSelectedPathComponent();
        if (node == null || !(node.getUserObject() instanceof Inspect)) {
            return;
        }

        Inspect insp = (Inspect) node.getUserObject();
        if (insp == null || insp.getPath() != getSelectedPath() || insp.getPosition() == -1) {
            return;
        }

        removeHighlighter();
        jumpTo(insp.getPosition());
    }

    private void treeConstantsRefresh(CharBuffer cb) {
        constantTree.replaceChildren(treeNodeList(constantPattern.matcher(cb)));
        treeModel.reload(constantTree);
    }

    void treeExpand(TreeExpansionEvent evt) {
        CharBuffer cb = getCharBuffer();
        if (evt.getPath().equals(new TreePath(treeModel.getPathToRoot(globalTree)))) {
            treeGlobalsRefresh(cb);
        } else if (evt.getPath().equals(new TreePath(treeModel.getPathToRoot(constantTree)))) {
            treeConstantsRefresh(cb);
        } else if (evt.getPath().equals(new TreePath(treeModel.getPathToRoot(objectTree)))) {
            treeObjectsRefresh(cb);
        } else if (evt.getPath().equals(new TreePath(treeModel.getPathToRoot(functionTree)))) {
            treeFunctionsRefresh(cb);
        } else if (evt.getPath().equals(new TreePath(treeModel.getPathToRoot(classTree)))) {
            treeClassesRefresh(cb);
        }
        evt = null;
    }

    private void treeExpand(JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        treeExpand(tree, new TreePath(root));
    }

    private void treeExpand(JTree tree, TreePath parent) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        for (Enumeration e = node.children(); e.hasMoreElements();) {
            TreeNode n = (TreeNode) e.nextElement();
            TreePath path = parent.pathByAddingChild(n);
            treeExpand(tree, path);
        }
        tree.expandPath(parent);
    }

    private void treeFunctionsRefresh(CharBuffer cb) {
        functionTree.replaceChildren(treeNodeList(functionPattern.matcher(cb)));
        treeModel.reload(functionTree);
    }

    private void treeGlobalsRefresh(CharBuffer cb) {
        globalTree.replaceChildren(treeNodeList(globalPattern.matcher(cb)));
        treeModel.reload(globalTree);
    }

    void treeHide() {
        upperSplitPane.setDividerLocation(0);
    }

    private void treeNodeBuild(InformTreeNode tree, InformAsset asset) {

        // Only display inform object definitions that are in the current file
        if (asset.isInformObject() && !isSelected(asset.getPath())) {
            return;
        }

        // New code
//        String name = asset.getName();
//        InformTreeNode node;
//        if (fileNodes.containsKey(name)) {
//            node = (InformTreeNode) fileNodes.get(name);
//            if (!tree.isNodeChild(node)) {
//                tree.insert(node);
//            }
//        } else {
        InformTreeNode node = new InformTreeNode(asset.getLocation());
//            fileNodes.put(asset.getName(), node);
        tree.add(node);
//        }

        for (Iterator i = asset.iterator(); i.hasNext();) {
            String childName = (String) i.next();
            if (fileAssets.containsKey(childName)) {
                InformAsset child = (InformAsset) fileAssets.get(childName);
                treeNodeBuild(node, child);
            }
        }
    }

    private List treeNodeList(Matcher m) {
        List list = new ArrayList();
        while (m.find()) {
            list.add(new Inspect(
                    m.group(1).toLowerCase(),
                    getSelectedPath(),
                    m.start(1)));
        }
        return list;
    }

    private void treeObjectsRefresh(CharBuffer cb) {
        objectTree.replaceChildren(treeNodeList(objectPattern.matcher(cb)));
        treeModel.reload(objectTree);
    }

    private void treeRefresh() {

        // Reset the tree if no files, no selection or selected file is not 
        // Inform source code
        if (getFileTabCount() == 0
                || getSelectedIndex() == -1
                || getSelectedContentType() != JifFileName.INFORM) {
            treeClear("Inspect");
            codeTree.setEnabled(false);
            return;
        }

        top.setUserObject(getSelectedName());
        codeTree.setEnabled(true);

        CharBuffer cb = getCharBuffer();

        // Globals
        treeGlobalsRefresh(cb);
        // Constants
        treeConstantsRefresh(cb);
        // Objects
        treeObjectsRefresh(cb);
        // Functions
        treeFunctionsRefresh(cb);
        // Classes
        treeClassesRefresh(cb);
        // Display new tree fully expanded
        treeExpand(codeTree);
    }

    private void treeRefreshIncremental() {

        // No tree to refresh if no files, no selection or selected file is not
        // inform source code
        if (getFileTabCount() == 0
                || getSelectedIndex() == -1
                || getSelectedContentType() != JifFileName.INFORM) {
            return;
        }

        DefaultTreeModel treeModel = (DefaultTreeModel) codeTree.getModel();
        TreePath globalPath = new TreePath(treeModel.getPathToRoot(globalTree));
        TreePath constantPath = new TreePath(treeModel.getPathToRoot(constantTree));
        TreePath objectPath = new TreePath(treeModel.getPathToRoot(objectTree));
        TreePath functionPath = new TreePath(treeModel.getPathToRoot(functionTree));
        TreePath classPath = new TreePath(treeModel.getPathToRoot(classTree));

        // Inform source in char buffer format for use by regular expressions 
        CharBuffer cb = getCharBuffer();

        // Globals
        if (codeTree.isExpanded(globalPath) || globalTree.isLeaf()) {
            treeGlobalsRefresh(cb);
        }
        // Constants
        if (codeTree.isExpanded(constantPath) || constantTree.isLeaf()) {
            treeConstantsRefresh(cb);
        }
        // Objects
        if (codeTree.isExpanded(objectPath) || objectTree.isLeaf()) {
            treeObjectsRefresh(cb);
        }
        // Functions
        if (codeTree.isExpanded(functionPath) || functionTree.isLeaf()) {
            treeFunctionsRefresh(cb);
        }
        // Classes
        if (codeTree.isExpanded(classPath) || classTree.isLeaf()) {
            treeClassesRefresh(cb);
            treeExpand(codeTree, classPath);
        }
    }

    void treeSelect() {
        leftTabbedPane.setSelectedIndex(0);
    }

    void treeShow() {
        upperSplitPane.setDividerLocation(180);
    }

    // --- Tutorial dialog -----------------------------------------------------
    
    void tutorialCreate() {
        tutorialEditorPane.setEditorKit(new StyledEditorKit());
        tutorialEditorPane.setBackground(config.getBackground());
        tutorialEditorPane.setForeground(config.getForeground(InformSyntax.Normal));
        tutorialEditorPane.setCaretColor(config.getForeground(InformSyntax.Normal));
    }

    void tutorialDialog(String resource, String title) {
        try {
            tutorialCreate();
            // Load the readme.txt file from the jar file
            InputStream is = ClassLoader.getSystemClassLoader().getResource(resource).openStream();
            tutorialEditorPane.setText(JifDAO.read(is));
            tutorialEditorPane.setCaretPosition(0);
            tutorialShow(title);
        } catch (IOException ex) {
            System.err.println("Tutorial dialog: " + ex.getMessage());
        }
    }

    void tutorialHide() {
        tutorialDialog.setVisible(false);
    }

    void tutorialPrint() {
        new Utils().printInform(
                this,
                "Jif - " + tutorialDialog.getTitle(),
                tutorialEditorPane);
    }

    void tutorialShow(String title) {
        tutorialDialog.setTitle(title);
        tutorialDialog.setSize(700, 550);
        tutorialDialog.setLocationRelativeTo(this);
        tutorialDialog.setVisible(true);
    }

    // -------------------------------------------------------------------------
    private void buildAllDialog() {
        outputInit();
        allFilesSave();
        if (isGlulxMode() && config.getMakeResource()) {
            makeResources();
        }
        buildAll();
        if (isGlulxMode()) {
            makeBlb();
        }
    }

    private void buildAll() {

        // Check main file is valid when compiling a project
        if (project.isOpen() && project.isMainClear()) {
            mainMissingMessage();
            return;
        }

        Inform compiler = new Inform(
                resolveAbsolutePath(config.getWorkingDirectory(), config.getCompilerPath()),
                resolveAbsolutePath(config.getWorkingDirectory(), config.getGamePath()));

        try {
            compiler.verify();
            outputAppend(compiler.run(
                    makeCompilerSwitches(),
                    makeCompilerLibraries(),
                    getSourcePath(),
                    makeFile(getSourcePath(), makeExtension())));
        } catch (ProgramMissingException ex) {
            compilerMissingMessage(resolveAbsolutePath(config.getWorkingDirectory(), config.getCompilerPath()));
        } catch (GamePathMissingException ex) {
            gamePathMissingMessage();
        } catch (IOException ex) {
            System.err.println("Build all: " + ex.getMessage());
        }
        
        adjustSplit();
    }

    private void compilerMissingMessage(String path) {
        JOptionPane.showMessageDialog(
                this,
                java.util.ResourceBundle.getBundle("JIF").getString("ERR_COMPILER1")
                + " " + path + " "
                + java.util.ResourceBundle.getBundle("JIF").getString("ERR_COMPILER2"),
                java.util.ResourceBundle.getBundle("JIF").getString("ERR_COMPILER3"),
                JOptionPane.ERROR_MESSAGE);
    }

    private void configMissingMessage() {
        JOptionPane.showMessageDialog(
                this,
                java.util.ResourceBundle.getBundle("JIF").getString("JIF_CONFIG_NOT_EXITS"),
                "Warning",
                JOptionPane.WARNING_MESSAGE);
    }

    private void gamePathMissingMessage() {
        JOptionPane.showMessageDialog(
                this,
                java.util.ResourceBundle.getBundle("JIF").getString("ERR_GAMESPATH"),
                java.util.ResourceBundle.getBundle("JIF").getString("ERR_GENERIC"),
                JOptionPane.ERROR_MESSAGE);
    }

    private void interpreterMissingMessage() {
        JOptionPane.showMessageDialog(
                this,
                java.util.ResourceBundle.getBundle("JIF").getString("ERR_INTERPRETER1")
                + " " + config.getInterpreterPath() + " "
                + java.util.ResourceBundle.getBundle("JIF").getString("ERR_INTERPRETER2"),
                java.util.ResourceBundle.getBundle("JIF").getString("ERR_COMPILER3"),
                JOptionPane.ERROR_MESSAGE);
    }

    private void mainMissingMessage() {
        JOptionPane.showMessageDialog(
                this,
                "Set a Main file first.",
                "Warning",
                JOptionPane.ERROR_MESSAGE);
    }

    private String makeCompilerLibraries() {

        String fileInf = getSourcePath();
        StringBuilder lib = new StringBuilder("+include_path=");

        if (config.getAdventInLib()) {
            lib.append(fileInf.substring(0, fileInf.lastIndexOf(File.separator))).append(",");
        }

        lib.append(resolveAbsolutePath(config.getWorkingDirectory(), config.getLibraryPath()));

        // Secondary 1-2-3 Library Path
        if (!config.getLibraryPath1().trim().equals("")) {
            lib.append(",").append(resolveAbsolutePath(config.getWorkingDirectory(), config.getLibraryPath1()));
        }
        if (!config.getLibraryPath2().trim().equals("")) {
            lib.append(",").append(resolveAbsolutePath(config.getWorkingDirectory(), config.getLibraryPath2()));
        }
        if (!config.getLibraryPath3().trim().equals("")) {
            lib.append(",").append(resolveAbsolutePath(config.getWorkingDirectory(), config.getLibraryPath3()));
        }
        return lib.toString();
    }

    private Set makeCompilerSwitches() {

        Set make = new TreeSet();

        for (Iterator i = getSwitches().entrySet().iterator(); i.hasNext();) {
            Entry e = (Entry) i.next();
            String name = (String) e.getKey();
            String setting = (String) e.getValue();

            if (setting.equals("on")) {
                if (name.indexOf("-v") == -1) {
                    make.add(name);
                } else {
                    if (isInformMode()) {
                        make.add(name);
                    }
                }
            }
        }

        // If in GLULX MODE, a "-G" switch is to be added
        if (isGlulxMode()) {
            make.add("-G");
        }

        return make;
    }

    private String makeExtension() {

        if (isGlulxMode()) {
            return "ulx";
        }

        // Default extension to zcode version 5
        String extension = "z5";

        for (Iterator i = getSwitches().entrySet().iterator(); i.hasNext();) {
            Entry e = (Entry) i.next();
            String name = (String) e.getKey();
            String setting = (String) e.getValue();

            if (setting.equals("on")) {
                if (name.equals("-v3")) {
                    extension = "z3";
                }
                if (name.equals("-v4")) {
                    extension = "z4";
                }
                if (name.equals("-v5")) {
                    extension = "z5";
                }
                if (name.equals("-v6")) {
                    extension = "z6";
                }
                if (name.equals("-v8")) {
                    extension = "z8";
                }
            }
        }
        return extension;
    }

    private String makeFile(String path, String extention) {
        return makeName(path) + "." + extention;
    }

    private String makeName(String path) {
        return path.substring(0, path.indexOf("."));
    }

    private void print() {
        new Utils().printInform(
                this,
                "Jif print - " + getSelectedPath(),
                getSelectedTextPane());
    }

    private void runAdventure(String adventure) {
        Interpreter interp = new Interpreter(config.getInterpreterPath());
        try {
            interp.verify();
            outputAppend(interp.run(adventure));
        } catch (ProgramMissingException ex) {
            interpreterMissingMessage();
        } catch (IOException ex) {
            System.out.println("ERROR Run adventure: " + ex.getMessage());
        }
    }

    private void runBlbDialog() {
        outputInit();
        fileSave();
        runAdventure(makeFile(getSourcePath(), "blb"));
    }

    private void runDialog() {
        outputInit();
        allFilesSave();
        buildAll();
        runAdventure(makeFile(getSourcePath(), makeExtension()));
    }

    private void runInterpreter() {
        Interpreter interp = new Interpreter(config.getInterpreterPath());
        try {
            interp.verify();
            outputAppend(interp.run());
        } catch (ProgramMissingException ex) {
            interpreterMissingMessage();
        } catch (IOException ex) {
            System.out.println("Run interpreter: " + ex.getMessage());
        }
    }

    private void runUlxDialog() {
        outputInit();
        fileSave();
        runAdventure(makeFile(getSourcePath(), makeExtension()));
    }

    private String getSourcePath() {
        return (project.isClosed() || project.isMainClear())
                ? getSelectedPath()
                : project.getMainPath();
    }

    void checkDefinition(String key) {

        // Search selected file tree first
        Inspect ins = top.search(key);
        if (ins.getPosition() != -1 && isSelected(ins.getPath())) {
            removeHighlighter();
            jumpTo(ins.getPosition());
            return;
        }

        // Search project files if necessary
        if (project.isClosed()) {
            return;
        }

        String file = projectDefinitionCheck(key);
        if (file == null) {
            return;
        }

        // Display definition source file and refresh tree
        synchronized (this) {
            fileOpen(file);
        }

        // Search source tree
        ins = top.search(key);
        if (ins.getPosition() != -1 && isSelected(ins.getPath())) {
            removeHighlighter();
            jumpTo(ins.getPosition());
        }
    }

    public void exitJif() {

        // Save, Exit and Cancel messages for exit options
        String[] scelte = new String[3];
        scelte[0] = java.util.ResourceBundle.getBundle("JIF").getString("MSG_SAVE_AND_EXIT");
        scelte[1] = java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF10");
        scelte[2] = java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF11");

        // If all files are saved then exit; otherwise, prompt with exit options
        int result = (isSaved())
                ? 1
                : JOptionPane.showOptionDialog(
                this,
                java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF12"),
                java.util.ResourceBundle.getBundle("JIF").getString("STR_JIF13"),
                0,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                scelte,
                scelte[2]);

        switch (result) {
            // Save
            case 0:
                allFilesSaveDialog();
            // Exit
            case 1:
                configurationSave();
                System.exit(0);
            // Cancel
            default:
        }
    }

    // --- Enable / disable components -----------------------------------------
    
    /**
     * Disable components that are invalid when no files are open.
     */
    private void disableFileComponents() {
        
        // toolbar buttons
        saveButton.setEnabled(false);
        saveAllButton.setEnabled(false);
        saveAsButton.setEnabled(false);
        closeButton.setEnabled(false);
        closeAllButton.setEnabled(false);
        undoButton.setEnabled(false);
        redoButton.setEnabled(false);
        commentButton.setEnabled(false);
        uncommentButton.setEnabled(false);
        tabLeftButton.setEnabled(false);
        tabRightButton.setEnabled(false);
        bracketCheckButton.setEnabled(false);
        buildAllButton.setEnabled(false);
        runButton.setEnabled(false);
        insertSymbolButton.setEnabled(false);
        findTextField.setEnabled(false);
        findButton.setEnabled(false);
        replaceButton.setEnabled(false);
        
        // menu items
        saveMenuItem.setEnabled(false);
        saveAllMenuItem.setEnabled(false);
        saveAsMenuItem.setEnabled(false);
        closeMenuItem.setEnabled(false);
        closeAllMenuItem.setEnabled(false);
        printMenuItem.setEnabled(false);
        
        // menus
        editMenu.setEnabled(false);
        buildMenu.setEnabled(false);
        glulxMenu.setEnabled(false);
        
        // tree
        codeTree.setEnabled(false);
        
        // search
        definitionTextField.setEnabled(false);
        definitionButton.setEnabled(false);
    }
    
    // Disable glulx menu
    public void disableGlulxMenu() {
        glulxMenu.setEnabled(false);
    }
    
    /**
     * Disable components that are invalid when no project is open.
     */
    private void disableProjectComponents() {
        
        // menu
        saveProjectMenuItem.setEnabled(false);
        closeProjectMenuItem.setEnabled(false);
        addNewToProjectMenuItem.setEnabled(false);
        addFileToProjectMenuItem.setEnabled(false);
        removeFromProjectMenuItem.setEnabled(false);
        projectPropertiesMenuItem.setEnabled(false);
        projectSwitchesMenuItem.setEnabled(false);
        
        // popup menu
        saveProjectPopupMenuItem.setEnabled(false);
        closeProjectPopupMenuItem.setEnabled(false);
        addNewToProjectPopupMenuItem.setEnabled(false);
        addFileToProjectPopupMenuItem.setEnabled(false);
        removeFromProjectPopupMenuItem.setEnabled(false);
        openSelectedFilesPopupMenuItem.setEnabled(false);
        setMainPopupMenuItem.setEnabled(false);
        removeMainPopupMenuItem.setEnabled(false);
        
        // project tab
        projectScrollPane.setEnabled(false);
        
        // search tab
        searchProjectTextField.setEnabled(false);
        searchProjectButton.setEnabled(false);
    }
    
    /**
     * Enable components that are valid when a file is open
     */
    private void enableFileComponents() {
        
        // toolbar buttons
        saveButton.setEnabled(true);
        saveAllButton.setEnabled(true);
        saveAsButton.setEnabled(true);
        closeButton.setEnabled(true);
        closeAllButton.setEnabled(true);
        undoButton.setEnabled(true);
        redoButton.setEnabled(true);
        commentButton.setEnabled(true);
        uncommentButton.setEnabled(true);
        tabLeftButton.setEnabled(true);
        tabRightButton.setEnabled(true);
        bracketCheckButton.setEnabled(true);
        buildAllButton.setEnabled(true);
        runButton.setEnabled(true);
        insertSymbolButton.setEnabled(true);
        findTextField.setEnabled(true);
        findButton.setEnabled(true);
        replaceButton.setEnabled(true);
        
        // menu items
        saveMenuItem.setEnabled(true);
        saveAllMenuItem.setEnabled(true);
        saveAsMenuItem.setEnabled(true);
        closeMenuItem.setEnabled(true);
        closeAllMenuItem.setEnabled(true);
        printMenuItem.setEnabled(true);
        
        // menus
        editMenu.setEnabled(true);
        buildMenu.setEnabled(true);
        
        if (isGlulxMode() && getFileTabCount() > 0) {
            glulxMenu.setEnabled(true);
        }
        
        // tree
        codeTree.setEnabled(true);
        
        // search
        definitionTextField.setEnabled(true);
        definitionButton.setEnabled(true);
    }
    
    // Enable glulx menu
    public void enableGlulxMenu() {
        glulxMenu.setEnabled(true);
    }
    
    /**
     * Enable components that are valid when a project is open.
     */
    private void enableProjectComponents() {
        
        // menu
        saveProjectMenuItem.setEnabled(true);
        closeProjectMenuItem.setEnabled(true);
        addNewToProjectMenuItem.setEnabled(true);
        addFileToProjectMenuItem.setEnabled(true);
        removeFromProjectMenuItem.setEnabled(true);
        projectPropertiesMenuItem.setEnabled(true);
        projectSwitchesMenuItem.setEnabled(true);
        
        // popup menu
        saveProjectPopupMenuItem.setEnabled(true);
        closeProjectPopupMenuItem.setEnabled(true);
        addNewToProjectPopupMenuItem.setEnabled(true);
        addFileToProjectPopupMenuItem.setEnabled(true);
        removeFromProjectPopupMenuItem.setEnabled(true);
        openSelectedFilesPopupMenuItem.setEnabled(true);
        setMainPopupMenuItem.setEnabled(true);
        removeMainPopupMenuItem.setEnabled(true);
        
        // project tab
        projectScrollPane.setEnabled(true);
        
        // search tab
        searchProjectTextField.setEnabled(true);
        searchProjectButton.setEnabled(true);
    }
    
    // Every opened file, this method resize the split
    // dimension to avoid the console is too big
    private void adjustSplit(){
        mainSplitPane.setDividerLocation(this.getHeight()-230);
    }
    
    
    //--------------------------------------------------------------------------
    
    private void insertFromFile() {
        try {
            String directory = config.getLastInsertDirectory();
            if (directory == null) {
                directory = config.getLastFileDirectory();
            }
            if (directory == null) {
                directory = config.getWorkingDirectory();
            }
            
            JFileChooser chooser = new JFileChooser(directory);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (chooser.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
                return;
            }
            
            File file = chooser.getSelectedFile();
            
            // imposto la lastInsert = a quella selezionata l'ultima volta
            config.setLastInsert(file.getAbsolutePath());

            insertString(JifDAO.read(file));

            treeRefreshIncremental();
            
        } catch (Exception ex) {
            System.err.println("Insert from file: " + ex.getMessage());
        }
    }
    
    // TODO Extend default control-c "copy" action in input map as well
    public void copyToClipBoard() {
        // Prendo il testo selezionato e prendo la substring fino al primo carattere \n
        // e lo inserisco come testo del menu
        
        // controllo che che non venga superato il limite max di entry nel menu PASTE
        if (pastePopupMenu.getMenuComponentCount() > Constants.MAX_DIMENSION_PASTE_MENU) {
            //System.out.println("superato dimensione max per menu");
            return;
        }
        
        // come titolo del menu, limito al max a 8 caratteri
        // il testo incollato, sarà contenuto nel tooltip, opportunamente
        // modificato PLAIN -> HTML  e HTML -> PLAIN
        String test = getSelectedText();
        
        StringSelection ss = new StringSelection(test);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        
        if (test.trim().length()> 25) test = test.trim().substring(0,25)+"...";
        JMenuItem mi = new JMenuItem(test.trim());
        
        // Come tool tip del menu metto tutto il codice selezionato
        String tmp = getSelectedText();
        // per vederlo tutto su più righe....lo trasformo il testo in formato HTML
        tmp = Utils.replace(tmp,"\n","<br>");
        mi.setToolTipText("<html>"+tmp+"</html>");
        mi.setFont(new Font("Dialog",Font.PLAIN,11));
        pastePopupMenu.add(mi).addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    //String id = ((javax.swing.JMenuItem)evt.getSource()).getText();
                    String id = ((javax.swing.JMenuItem)evt.getSource()).getToolTipText();
                    
                    //ricostruisco la stringa... da html a plain text
                    id = Utils.replace(id,"<br>","\n");
                    id = Utils.replace(id,"<html>","");
                    id = Utils.replace(id,"</html>","");
                    MutableAttributeSet attr = new SimpleAttributeSet();
                    insertString(id);
                } catch (BadLocationException ex) {
                    System.err.println("Copy to clipboard: " + ex.getMessage());
                }
            }
        });
    }
    
    /**
     * If a string is on the system clipboard, this method returns it;
     * otherwise it returns null.
     *
     * @return clipboard string
     */
    // TODO This method is not used
    public static String getClipboard() {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String text = (String)t.getTransferData(DataFlavor.stringFlavor);
                return text;
            }
        } catch (UnsupportedFlavorException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    // funzione che testa se TUTTI gli switch +language_name.....sono DISATTIVATI...
    // true : tutti spenti
    // false : almeno uno acceso
    // TODO This method is not used
    private boolean checkDefaultLanguage() {
        // prendo il pannello 2 dello switch manager,
        // scandisco tutti i chechbox, se ne trovo uno solo che inizia con
        // +language_name ATTIVO, ritorno FALSE
        // altrimenti ritorno TRUE
        
        for (int count=0; count < switchesLowerPanel.getComponentCount(); count++) {
            Checkbox ch = (Checkbox) switchesLowerPanel.getComponent(count);
            if (ch.getLabel().startsWith("+language_name") && ch.getState()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Run the BLC utility program to make a blb file (GLULX MODE ONLY).
     * This is equivalent to the following command line:<br>
     * <code>BLC source.blc source.blb</code>
     */
    private void makeBlbDialog() {
        outputInit();
        makeBlb();
    }
    
    private void makeBlb() {
        Blc blc = new Blc(resolveAbsolutePath(config.getWorkingDirectory(), config.getBlcPath()));
        try {
            blc.verify();
            outputAppend(blc.run(
                    makeFile(getSourcePath(), "blc"),
                    makeFile(getSourcePath(), "blb"),
                    getSourcePath().substring(0, getSourcePath().lastIndexOf(File.separator))
                    ));
        } catch(ProgramMissingException ex) {
            compilerMissingMessage(resolveAbsolutePath(config.getWorkingDirectory(), config.getBlcPath()));
        } catch(InterruptedException ex) {
            System.out.println("ERROR make blorb: " + ex.getMessage());
        } catch(IOException ex) {
            System.out.println("ERROR make blorb: " + ex.getMessage());
        }
    }
    
    /**
     * Run the BRES utility program to make a resource file (GLULX MODE ONLY).
     * This is equivalent to to the following command line:
     * <code>BRES source</code>
     */
    private void makeResourcesDialog() {
        outputInit();
        makeResources();
    }
    
    private void makeResources() {
        Bres bres = new Bres(resolveAbsolutePath(config.getWorkingDirectory(), config.getBresPath()));
        try {
            bres.verify();
            outputAppend(bres.run(
                    makeName(getSourcePath()),
                    resolveAbsolutePath(config.getWorkingDirectory(), config.getGamePath())));
        } catch (ProgramMissingException ex) {
            compilerMissingMessage(resolveAbsolutePath(config.getWorkingDirectory(), config.getBresPath()));
        } catch (InterruptedException ex) {
            System.err.println("Unable to make resources: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Unable to make resources: " + ex.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    /**
     * If working path + test path exists this method returns working path +
     * test path else will return the test path
     *
     * @return resolved path
     */
    public String resolveAbsolutePath(String workingDirectory, String testpath) {
        File f = new File(workingDirectory + testpath);
        if (f.exists()) {
            return workingDirectory + testpath;
        } else {
            return testpath;
        }
    }

    // --- User interface update methods ---------------------------------------
    
    // --- All
    
    private void updateTitle() {
        setTitle(
                Constants.TITLE
                + "   "
                + getModeName()
                + getBufferName());
    }

    // --- Buffer
    
    private void updateBuffer() {
        updateFileComponents();
        updateTitle();
    }

    private void updateFile(JifFileName fileName) {
        projectModel.updateFile(fileName);
    }

    private void updateFileComponents() {
        if (getFileTabCount() == 0 || getSelectedIndex() == -1) {
            disableFileComponents();
        } else {
            enableFileComponents();
        }
    }

    // --- Configuration
    
    private void updateDocuments() {
        // Apply color and font changes to all open documents
        for (int i = 0; i < getFileTabCount(); i++) {
            JifScrollPane sp = getScrollPaneAt(i);
            sp.setLineNumbers(config.getNumberLines());
            sp.setLineNumberFont(config.getFont());

            JifTextPane tp = sp.getTextPane();
            tp.setBackground(config.getBackground());
            tp.setCaretColor(config.getForeground(InformSyntax.Normal));
            tp.setWrap(config.getWrapLines());
        }
    }

    private void updateLastProject() {
        if (config.getLastProject() == null) {
            return;
        }
        lastProjectMenuItem.setText(
                java.util.ResourceBundle.getBundle("JIF").getString("MENUITEM_OPEN")
                + " (" + config.getLastProjectName() + ")");
    }

    private void updateMenues() {
        // Insert menu
        insertNewMenu.removeAll();
        for (Iterator i = config.menuIterator(); i.hasNext();) {
            String elem = (String) i.next();
            JMenu menu = new JMenu(elem);
            menu.setName(elem);
            menu.setFont(new Font("Dialog", 0, 12));

            // Sub menues
            for (Iterator sub = config.getSubMenu(elem).iterator(); sub.hasNext();) {
                String submenu = (String) sub.next();
                JMenuItem mi = new JMenuItem(submenu);
                mi.setName(submenu);
                mi.setFont(new Font("Dialog", 0, 12));
                menu.add(mi).addMouseListener(menuListener);
            }
            insertNewMenu.add(menu);
        }
    }

    private void updateOutputView() {
        outputCheckBoxMenuItem.setSelected(config.isOutputVisible());
        if (config.isOutputVisible()) {
            outputShow();
        } else {
            outputHide();
        }
    }

    private void updateRecentFiles() {
        // Recent file menu
        recentFilesMenu.removeAll();
        for (Iterator i = config.recentFileIterator(); i.hasNext();) {
            String file = (String) i.next();
            JMenuItem mi = new JMenuItem(file);
            mi.setName(file);
            mi.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    fileOpen(((javax.swing.JMenuItem) evt.getSource()).getText());
                }
            });
            recentFilesMenu.add(mi);
        }
    }

    private void updateSymbols() {
        symbolList.setListData(config.getSymbolsAlpha().toArray());
    }

    private void updateToolbarView() {
        toolbarCheckBoxMenuItem.setSelected(config.isToolbarVisible());
        if (config.isToolbarVisible()) {
            toolbarShow();
        } else {
            toolbarHide();
        }
    }

    private void updateTreeView() {
        treeCheckBoxMenuItem.setSelected(config.isTreeVisible());
        if (config.isTreeVisible()) {
            treeShow();
        } else {
            treeHide();
        }
    }
    
    private void updateView() {
        toggleFullscreenCheckBoxMenuItem.setState(config.isFullScreen());
        updateOutputView();
        updateToolbarView();
        updateTreeView();
    }
    
    // --- Mode
    
    private void updateMode() {
        updateModeMenu();
        updateModeComponents();
        updateTitle();
    }
    
    private void updateModeComponents() {
        if (isInformMode() || getFileTabCount() == 0) {
            disableGlulxMenu();
        } else {
            enableGlulxMenu();
        }
    }
    
    private void updateModeMenu() {
        glulxModeCheckBoxMenuItem.setState(isGlulxMode());
        informModeCheckBoxMenuItem.setState(isInformMode());
    }
    
    // --- Project
    
    private void updateProjectComponents() {
        if (project.isClosed()) {
            disableProjectComponents();
        } else {
            enableProjectComponents();
        }
    }
    
    private void updateProjectList() {
        getProjectListModel().replaceList(project.getFiles());
    }
    
    private void updateProjectMain() {
        mainFileLabel.setText("Main: " + project.getMainName());
    }
    
    private void updateProjectTitle() {
        TitledBorder tb = new TitledBorder("Project: " + project.getTitle());
        tb.setTitleFont(new Font("Dialog", Font.PLAIN, 11));
        projectScrollPane.setBorder(tb);
    }
    
    // --- JifConfigurationObserver implementation -----------------------------
    
    @Override
    public void updateConfiguration() {
        updateDocuments();
        updateLastProject();
        updateRecentFiles();
        updateMenues();
        updateSymbols();
        updateView();
    }

    // --- JifProjectObserver implementation -----------------------------------

    @Override
    public void updateProject() {
        updateProjectTitle();
        updateProjectList();
        updateProjectMain();
        updateProjectComponents();
        updateMode();
    }
    
    // --- Helper methods for file editors -------------------------------------
    
    JifScrollPane getSelectedScrollPane() {
        return (JifScrollPane) fileTabbedPane.getSelectedComponent();
    }
    
    JifFileName getSelectedFileName() {
        return getSelectedScrollPane().getFileName();
    }
    
    String getSelectedContentType() {
        return getSelectedFileName().getContentType();
    }
    
    String getSelectedName() {
        return getSelectedFileName().getName();
    }
    
    String getSelectedPath() {
        return getSelectedScrollPane().getPath();
    }
    
    void setSelectedFileName(String path) {
        getSelectedScrollPane().setPath(path);
    }
    
    JifTextPane getSelectedTextPane() {
        return getSelectedScrollPane().getTextPane();
    }
    
    ActionMap getActionMap() {
        return getSelectedTextPane().getActionMap();
    }
    
    Action getAction(String name) {
        return getActionMap().get(name);
    }
    
    int getCaretPosition() {
        return getSelectedTextPane().getCaretPosition();
    }
    
    void setCaretPosition(int position) {
        getSelectedTextPane().setCaretPosition(position);
    }
    
    CharBuffer getCharBuffer() {
        return getSelectedTextPane().getCharBuffer();
    }
    
    JifDocument getDocument() {
        return (JifDocument) getSelectedTextPane().getDocument();
    }
    
    Element getElement(int index) {
        return getSelectedTextPane().getElement(index);
    }
    
    void removeHighlighter() {
        getSelectedTextPane().removeHighlighter();
    }
    
    void jumpTo(int pos) {
        getSelectedTextPane().jumpToPosition(pos);
        selectedRequestFocus();
    }
    
    void selectedRequestFocus() {
        getSelectedTextPane().requestFocus();
    }
    
    void selectedRepaint() {
        getSelectedTextPane().repaint();
    }
    
    int getLength() {
        return getDocument().getLength();
    }
    
    void insertString(String text) throws BadLocationException {
        getDocument().insertString(
                getCaretPosition(),
                text,
                new SimpleAttributeSet()
                );
    }
    
    String getSelectedText() {
        return getSelectedTextPane().getSelectedText();
    }
    
    void setSelectionEnd(int pos) {
        getSelectedTextPane().setSelectionEnd(pos);
    }
    
    void setSelectionStart(int pos) {
        getSelectedTextPane().setSelectionStart(pos);
    }
    
    void replaceSelection(String text) {
        getSelectedTextPane().replaceSelection(text);
    }
    
    String getSelectedTitle() {
        return fileTabbedPane.getTitleAt(getSelectedIndex());
    }
    
    void setSelectedTitle(String title) {
        fileTabbedPane.setTitleAt(getSelectedIndex(), title);
    }
    
    String getText() {
        return getSelectedTextPane().getText();
    }
    
    void setText(String text) {
        getSelectedTextPane().setText(text);
    }
    
    final String getTitleAt(int index) {
        return fileTabbedPane.getTitleAt(index);
    }
    
    JifFileName getFileNameAt(int index) {
        return getScrollPaneAt(index).getFileName();
    }
    
    String getPathAt(int index) {
        return getScrollPaneAt(index).getPath();
    }
    
    JifScrollPane getScrollPaneAt(int index) {
        return (JifScrollPane) fileTabbedPane.getComponentAt(index);
    }
    
    JifTextPane getTextPaneAt(int index) {
        return getScrollPaneAt(index).getTextPane();
    }
    
    int getSelectedIndex() {
        return fileTabbedPane.getSelectedIndex();
    }
    
    void setSelectedIndex(int index) {
        fileTabbedPane.setSelectedIndex(index);
    }
    
    int getFileTabCount() {
        return fileTabbedPane.getTabCount();
    }
    
    JifProjectListModel getProjectListModel() {
        return (JifProjectListModel) projectList.getModel();
    }
    
    JifFileName getSelectedProjectFile() {
        return (JifFileName) projectList.getSelectedValue();
    }
    
    // --- Accessor methods ----------------------------------------------------

    
    
    
    // --- Alt keys
    
    boolean isAltKey(String key) {
        return config.containsAltKey(key);
    }
    
    String getAltKey(String key) {
        return config.getAltKey(key);
    }
    
    // --- Buffer
    
    String getBufferName() {
        return (getFileTabCount() == 0 || getSelectedIndex() == -1)
                ? ""
                : " - " + getSelectedPath();
    }

    public boolean isOpen(JifFileName fileName) {
        return buffer.containsKey(fileName);
    }

    boolean isSelected(String path) {
        if (getFileTabCount() == 0) {
            return false;
        }
        return getSelectedPath().equals(path);
    }
    
    // --- Execute commands
    
    boolean isExecuteCommand(String key) {
        return config.containsExecuteCommand(key);
    }
    
    String getExecuteCommand(String key) {
        return config.getExecuteCommand(key);
    }
    
    // --- Find
    
    String getFindText() {
        return findTextField.getText();
    }
    
    void setFindText(String text) {
        findTextField.setText(text);
    }
    
    // --- Help code
    
    String getHelpCode(String key) {
        return config.getHelpCode(key);
    }
    
    boolean isHelpCodeLive() {
        return config.getHelpedCode();
    }
    
    // --- Jif version
    
    String getJifVersion() {
        return jifVersion;
    }
    
    void setJifVersion(String jifVersion) {
        this.jifVersion = jifVersion;
    }
    
    // --- Mapping
    
    String getMapping(String key) {
        return config.getMapping(key);
    }
    
    boolean isMapping(String key) {
        return config.isMapping(key);
    }

    // --- Mode
    
    private String getModeName() {
        return isInformMode() ? "Inform" : "Glulx";
    }
    
    boolean isInformMode() {
        return (project.isClosed()) ?
            config.getInformMode() :
            project.getInformMode();
    }
    
    boolean isGlulxMode() {
        return (project.isClosed()) ?
            !config.getInformMode() :
            !project.getInformMode();
    }
    
    private void setMode(boolean inform) {
        if (project.isClosed()) {
            config.setInformMode(inform);
        } else {
            project.setInformMode(inform);
        }
        updateMode();
    }
    
    // --- Operations
    
    String getOperation(String key) {
        return config.getOperation(key);
    }
    
    // --- Output
    
    boolean isOutputVisible() {
        return config.isOutputVisible();
    }
    
    // --- Project
    
    public boolean isMain(JifFileName fileName) {
        return project.isMain(fileName);
    }
    
    // --- Switches
    
    Map getSwitches() {
        return (project.isClosed()) ?
            config.getSwitches() :
            project.getSwitches();
    }
    
    // --- Symbols
    
    JDialog getSymbolDialog() {
        return symbolDialog;
    }
    
    boolean isSymbolsVisible() {
        return symbolDialog.isVisible();
    }
    
    // --- Syntax
    
    boolean isSyntaxHighlighting() {
        return config.getSyntaxHighlighting();
    }

    // ---

    boolean isSaved() {
        for (int i=0; i < getFileTabCount(); i++) {
            if (getTitleAt(i).endsWith("*")) {
                return false;
            }
        }
        return true;
    }

    private void chooseDirectory(JTextField field) {
        JFileChooser chooser = new JFileChooser(config.getWorkingDirectory());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
            return;
        }
        field.setText(chooser.getSelectedFile().getAbsolutePath());
    }
    
    private void choosePath(JTextField field) {
        JFileChooser chooser = new JFileChooser(config.getWorkingDirectory());
        if (chooser.showOpenDialog(this) == JFileChooser.CANCEL_OPTION) {
            return;
        }
        field.setText(chooser.getSelectedFile().getAbsolutePath());
    }
    
// =============================================================================
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aboutControlPanel;
    private javax.swing.JDialog aboutDialog;
    private javax.swing.JLabel aboutLabel;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton aboutOKButton;
    private javax.swing.JTabbedPane aboutTabbedPane;
    private javax.swing.JMenuItem addFileToProjectMenuItem;
    private javax.swing.JMenuItem addFileToProjectPopupMenuItem;
    private javax.swing.JMenuItem addNewToProjectMenuItem;
    private javax.swing.JMenuItem addNewToProjectPopupMenuItem;
    private javax.swing.JCheckBox adventInLibCheckBox;
    private javax.swing.JButton attributeColorButton;
    private javax.swing.JLabel attributeColorLabel;
    private javax.swing.JPanel attributeColorjPanel;
    private javax.swing.JButton backgroundColorButton;
    private javax.swing.JLabel backgroundColorLabel;
    private javax.swing.JPanel backgroundColorPanel;
    private javax.swing.JButton blcPathButton;
    private javax.swing.JLabel blcPathLabel;
    private javax.swing.JPanel blcPathPanel;
    private javax.swing.JTextField blcPathTextField;
    private javax.swing.JButton bookmarkColorButton;
    private javax.swing.JLabel bookmarkColorLabel;
    private javax.swing.JPanel bookmarkColorPanel;
    private javax.swing.JButton bracketCheckButton;
    private javax.swing.JButton bracketColorButton;
    private javax.swing.JLabel bracketColorLabel;
    private javax.swing.JPanel bracketColorPanel;
    private javax.swing.JButton bresPathButton;
    private javax.swing.JLabel bresPathLabel;
    private javax.swing.JPanel bresPathPanel;
    private javax.swing.JTextField bresPathTextField;
    private javax.swing.JButton buildAllButton;
    private javax.swing.JMenuItem buildAllGlulxMenuItem;
    private javax.swing.JMenuItem buildAllMenuItem;
    private javax.swing.JMenu buildMenu;
    private javax.swing.JMenuItem changelogMenuItem;
    private javax.swing.JMenuItem clearAllMenuItem;
    private javax.swing.JMenuItem clearPopupMenuItem;
    private javax.swing.JMenuItem clearRecentFilesMenuItem;
    private javax.swing.JButton closeAllButton;
    private javax.swing.JMenuItem closeAllMenuItem;
    private javax.swing.JMenuItem closeAllPopupMenuItem;
    private javax.swing.JButton closeButton;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JMenuItem closePopupMenuItem;
    private javax.swing.JMenuItem closeProjectMenuItem;
    private javax.swing.JMenuItem closeProjectPopupMenuItem;
    private static javax.swing.JTree codeTree;
    private javax.swing.JEditorPane colorEditorPane;
    private javax.swing.JPanel colorFontPanel;
    private javax.swing.JPanel colorHighlightPanel;
    private javax.swing.JPanel colorPanel;
    private javax.swing.JButton commentButton;
    private javax.swing.JButton commentColorButton;
    private javax.swing.JLabel commentColorLabel;
    private javax.swing.JPanel commentColorPanel;
    private javax.swing.JMenuItem commentSelectionMenuItem;
    private javax.swing.JMenuItem compileMenuItem;
    private javax.swing.JButton compilerPathButton;
    private javax.swing.JLabel compilerPathLabel;
    private javax.swing.JPanel compilerPathPanel;
    private javax.swing.JTextField compilerPathTextField;
    private javax.swing.JPanel complierPanel;
    private javax.swing.JButton configCloseButton;
    private javax.swing.JPanel configControlPanel;
    private javax.swing.JDialog configDialog;
    private javax.swing.JMenuItem configFileMenuItem;
    private javax.swing.JLabel configLabel;
    private javax.swing.JPanel configLabelPanel;
    private javax.swing.JButton configSaveButton;
    private javax.swing.JScrollPane configScrollPane;
    private javax.swing.JTextArea configTextArea;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem copyPopupMenuItem;
    private javax.swing.JCheckBox createNewFileCheckBox;
    private javax.swing.JScrollPane creditsScrollPane;
    private javax.swing.JTextArea creditsTextArea;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem cutPopupMenuItem;
    private javax.swing.JPanel defaultColorPanel;
    private javax.swing.JButton defaultDarkColorButton;
    private javax.swing.JLabel defaultDarkColorLabel;
    private javax.swing.JPanel defaultDarkColorPanel;
    private javax.swing.JButton defaultDarkHighlightButton;
    private javax.swing.JLabel defaultDarkHighlightLabel;
    private javax.swing.JPanel defaultDarkhighlightPanel;
    private javax.swing.JPanel defaultHighlightPanel;
    private javax.swing.JButton defaultLightColorButton;
    private javax.swing.JLabel defaultLightColorLabel;
    private javax.swing.JPanel defaultLightColorPanel;
    private javax.swing.JButton defaultLightHighlightButton;
    private javax.swing.JLabel defaultLightHighlightLabel;
    private javax.swing.JPanel defaultLightHighlightPanel;
    private javax.swing.JButton definitionButton;
    private javax.swing.JPanel definitionPanel;
    private javax.swing.JTextField definitionTextField;
    private javax.swing.JPanel donate;
    private javax.swing.JMenu editMenu;
    private javax.swing.JButton errorColorButton;
    private javax.swing.JLabel errorColorLabel;
    private javax.swing.JPanel errorColorPanel;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenuItem extractStringsMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPanel filePanel;
    public javax.swing.JPopupMenu filePopupMenu;
    private static final javax.swing.JTabbedPane fileTabbedPane = new javax.swing.JTabbedPane();
    private javax.swing.JButton findButton;
    public javax.swing.JTextField findTextField;
    private javax.swing.JLabel fontLabel;
    private javax.swing.JComboBox fontNameComboBox;
    private javax.swing.JPanel fontPanel;
    private javax.swing.JComboBox fontSizeComboBox;
    private javax.swing.JButton gamePathButton;
    private javax.swing.JLabel gamePathLabel;
    private javax.swing.JPanel gamePathPanel;
    private javax.swing.JTextField gamePathTextField;
    private javax.swing.JMenuItem garbageCollectionMenuItem;
    private javax.swing.JPanel generalPanel;
    private javax.swing.JMenu glulxMenu;
    private javax.swing.JCheckBoxMenuItem glulxModeCheckBoxMenuItem;
    private javax.swing.JPanel glulxPanel;
    private javax.swing.JButton glulxPathButton;
    private javax.swing.JLabel glulxPathLabel;
    private javax.swing.JPanel glulxPathPanel;
    private javax.swing.JTextField glulxPathTextField;
    private javax.swing.JMenu helpMenu;
    public javax.swing.JCheckBox helpedCodeCheckBox;
    private javax.swing.JEditorPane highlightEditorPane;
    private javax.swing.JPanel highlightPanel;
    private javax.swing.JComboBox highlightSelectedComboBox;
    private javax.swing.JLabel highlightSelectedLabel;
    private javax.swing.JPanel highlightSelectedPanel;
    private javax.swing.JButton infoCloseButton;
    private javax.swing.JPanel infoControlPanel;
    private javax.swing.JDialog infoDialog;
    private javax.swing.JScrollPane infoScrollPane;
    private javax.swing.JTextArea infoTextArea;
    private javax.swing.JCheckBoxMenuItem informModeCheckBoxMenuItem;
    private javax.swing.JMenuItem insertFileMenuItem;
    private javax.swing.JMenuItem insertFilePopupMenuItem;
    private javax.swing.JMenu insertNewMenu;
    private javax.swing.JButton insertSymbolButton;
    private javax.swing.JMenuItem insertSymbolMenuItem;
    private javax.swing.JMenuItem insertSymbolPopupMenuItem;
    private javax.swing.JButton interpreterButton;
    private javax.swing.JButton interpreterPathButton;
    private javax.swing.JLabel interpreterPathLabel;
    private javax.swing.JPanel interpreterPathPanel;
    private javax.swing.JTextField interpreterPathTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator19;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTextArea jTextArea1;
    private static final javax.swing.JToolBar jToolBarCommon = new javax.swing.JToolBar();
    private javax.swing.JButton jumpToColorButton;
    private javax.swing.JLabel jumpToColorLabel;
    private javax.swing.JPanel jumpToColorPanel;
    private javax.swing.JMenuItem jumpToSourceMenuItem;
    private javax.swing.JButton keywordColorButton;
    private javax.swing.JLabel keywordColorLabel;
    private javax.swing.JPanel keywordColorPanel;
    private javax.swing.JMenuItem lastProjectMenuItem;
    private javax.swing.JTabbedPane leftTabbedPane;
    private javax.swing.JPanel libraryPanel;
    private javax.swing.JButton libraryPath0Button;
    private javax.swing.JLabel libraryPath0Label;
    private javax.swing.JPanel libraryPath0Panel;
    private javax.swing.JTextField libraryPath0TextField;
    private javax.swing.JButton libraryPath1Button;
    private javax.swing.JLabel libraryPath1Label;
    private javax.swing.JPanel libraryPath1Panel;
    private javax.swing.JTextField libraryPath1TextField;
    private javax.swing.JButton libraryPath2Button;
    private javax.swing.JLabel libraryPath2Label;
    private javax.swing.JPanel libraryPath2Panel;
    private javax.swing.JTextField libraryPath2TextField;
    private javax.swing.JButton libraryPath3Button;
    private javax.swing.JLabel libraryPath3Label;
    private javax.swing.JPanel libraryPath3Panel;
    private javax.swing.JTextField libraryPath3TextField;
    private javax.swing.JLabel mainFileLabel;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JMenuItem makeBlbMenuItem;
    private javax.swing.JCheckBox makeResourceCheckBox;
    private javax.swing.JMenuItem makeResourceMenuItem;
    public javax.swing.JCheckBox mappingLiveCheckBox;
    private javax.swing.JMenu modeMenu;
    private javax.swing.JButton newButton;
    private javax.swing.JMenuItem newMenuItem;
    private javax.swing.JMenuItem newProjectMenuItem;
    private javax.swing.JMenuItem newProjectPopupMenuItem;
    private javax.swing.JMenuItem nextBookmarkMenuItem;
    private javax.swing.JButton normalColorButton;
    private javax.swing.JLabel normalColorLabel;
    private javax.swing.JPanel normalColorPanel;
    private javax.swing.JButton numberColorButton;
    private javax.swing.JLabel numberColorLabel;
    private javax.swing.JPanel numberColorPanel;
    private javax.swing.JCheckBox numberLinesCheckBox;
    private javax.swing.JButton openButton;
    private javax.swing.JCheckBox openLastFileCheckBox;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem openProjectMenuItem;
    private javax.swing.JMenuItem openProjectPopupMenuItem;
    private javax.swing.JMenuItem openSelectedFilesPopupMenuItem;
    private javax.swing.JButton optionCancelButton;
    private javax.swing.JPanel optionControlPanel;
    private javax.swing.JButton optionDefaultButton;
    public javax.swing.JDialog optionDialog;
    private javax.swing.JButton optionSaveButton;
    private javax.swing.JTabbedPane optionTabbedPane;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JCheckBoxMenuItem outputCheckBoxMenuItem;
    private javax.swing.JScrollPane outputScrollPane;
    private static final javax.swing.JTabbedPane outputTabbedPane = new javax.swing.JTabbedPane();
    public javax.swing.JTextArea outputTextArea;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenu pastePopupMenu;
    private javax.swing.JMenuItem printMenuItem;
    private javax.swing.JMenuItem printPopupMenuItem;
    private javax.swing.JList projectList;
    private javax.swing.JMenu projectMenu;
    private javax.swing.JCheckBox projectOpenAllFilesCheckBox;
    private javax.swing.JPanel projectPanel;
    private javax.swing.JPopupMenu projectPopupMenu;
    private javax.swing.JButton projectPropertiesCloseButton;
    private javax.swing.JPanel projectPropertiesControlPanel;
    private javax.swing.JDialog projectPropertiesDialog;
    private javax.swing.JMenuItem projectPropertiesMenuItem;
    private javax.swing.JButton projectPropertiesSaveButton;
    private javax.swing.JScrollPane projectPropertiesScrollPane;
    private javax.swing.JTextArea projectPropertiesTextArea;
    private javax.swing.JScrollPane projectScrollPane;
    private javax.swing.JButton projectSwitchesCloseButton;
    private javax.swing.JPanel projectSwitchesControlPanel;
    private javax.swing.JDialog projectSwitchesDialog;
    private javax.swing.JMenuItem projectSwitchesMenuItem;
    private javax.swing.JPanel projectSwitchesPanel;
    private javax.swing.JButton projectSwitchesSaveButton;
    private javax.swing.JButton propertyColorButton;
    private javax.swing.JLabel propertyColorLabel;
    private javax.swing.JPanel propertyColorPanel;
    private javax.swing.JMenuItem readMeMenuItem;
    private javax.swing.JMenu recentFilesMenu;
    private javax.swing.JButton redoButton;
    private javax.swing.JMenuItem removeFromProjectMenuItem;
    private javax.swing.JMenuItem removeFromProjectPopupMenuItem;
    private javax.swing.JMenuItem removeMainPopupMenuItem;
    private javax.swing.JButton replaceAllButton;
    private javax.swing.JButton replaceButton;
    private javax.swing.JButton replaceCloseButton;
    private javax.swing.JPanel replaceControlPanel;
    private javax.swing.JDialog replaceDialog;
    private javax.swing.JButton replaceFindButton;
    private javax.swing.JLabel replaceFindLabel;
    private javax.swing.JTextField replaceFindTextField;
    private javax.swing.JMenuItem replaceMenuItem;
    private javax.swing.JPanel replacePanel;
    private javax.swing.JButton replaceReplaceButton;
    private javax.swing.JLabel replaceReplaceLabel;
    private javax.swing.JTextField replaceReplaceTextField;
    private javax.swing.JMenuItem runBlbMenuItem;
    private javax.swing.JButton runButton;
    private javax.swing.JMenuItem runMenuItem;
    private javax.swing.JMenuItem runUlxMenuItem;
    private javax.swing.JButton saveAllButton;
    private javax.swing.JMenuItem saveAllMenuItem;
    private javax.swing.JButton saveAsButton;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JButton saveButton;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem saveProjectMenuItem;
    private javax.swing.JMenuItem saveProjectPopupMenuItem;
    private javax.swing.JCheckBox scanProjectFilesCheckBox;
    private javax.swing.JMenuItem searchAllMenuItem;
    private javax.swing.JMenuItem searchMenuItem;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JButton searchProjectButton;
    private javax.swing.JPanel searchProjectPanel;
    public javax.swing.JTextField searchProjectTextField;
    private javax.swing.JMenuItem selectAllMenuItem;
    private javax.swing.JMenuItem setBookmarkMenuItem;
    private javax.swing.JMenuItem setMainPopupMenuItem;
    private javax.swing.JButton settingsButton;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JButton stringColorButton;
    private javax.swing.JLabel stringColorLabel;
    private javax.swing.JPanel stringColorPanel;
    private javax.swing.JButton switchManagerButton;
    private javax.swing.JButton switchesCloseButton;
    private javax.swing.JPanel switchesControlPanel;
    private javax.swing.JDialog switchesDialog;
    private javax.swing.JPanel switchesLowerPanel;
    private javax.swing.JMenuItem switchesMenuItem;
    private javax.swing.JPanel switchesPanel;
    private javax.swing.JButton switchesSaveButton;
    private javax.swing.JPanel switchesUpperPanel;
    private javax.swing.JDialog symbolDialog;
    private javax.swing.JList symbolList;
    private javax.swing.JScrollPane symbolScrollPane;
    public javax.swing.JCheckBox syntaxCheckBox;
    private javax.swing.JButton tabLeftButton;
    private javax.swing.JMenuItem tabLeftMenuItem;
    private javax.swing.JButton tabRightButton;
    private javax.swing.JMenuItem tabRightMenuItem;
    private javax.swing.JLabel tabSizeLabel;
    private javax.swing.JPanel tabSizePanel;
    private javax.swing.JTextField tabSizeTextField;
    private javax.swing.JButton textCloseButton;
    private javax.swing.JPanel textControlPanel;
    private javax.swing.JDialog textDialog;
    private javax.swing.JLabel textLabel;
    private javax.swing.JScrollPane textScrollPane;
    private javax.swing.JTextArea textTextArea;
    private javax.swing.JCheckBoxMenuItem toggleFullscreenCheckBoxMenuItem;
    private javax.swing.JCheckBoxMenuItem toolbarCheckBoxMenuItem;
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JMenuItem translateMenuItem;
    private javax.swing.JCheckBoxMenuItem treeCheckBoxMenuItem;
    private javax.swing.JPanel treePanel;
    private javax.swing.JScrollPane treeScrollPane;
    private javax.swing.JPanel tutorialControlPanel;
    private javax.swing.JDialog tutorialDialog;
    private javax.swing.JEditorPane tutorialEditorPane;
    private javax.swing.JLabel tutorialLabel;
    private javax.swing.JButton tutorialOKButton;
    private javax.swing.JButton tutorialPrintButton;
    private javax.swing.JScrollPane tutorialScrollPane;
    private javax.swing.JButton uncommentButton;
    private javax.swing.JMenuItem uncommentSelectionMenuItem;
    private javax.swing.JButton undoButton;
    private javax.swing.JSplitPane upperSplitPane;
    private javax.swing.JButton verbColorButton;
    private javax.swing.JLabel verbColorLabel;
    private javax.swing.JPanel verbColorPanel;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JButton warningColorButton;
    private javax.swing.JLabel warningColorLabel;
    private javax.swing.JPanel warningColorPanel;
    private javax.swing.JButton wordColorButton;
    private javax.swing.JLabel wordColorLabel;
    private javax.swing.JPanel wordColorPanel;
    public javax.swing.JCheckBox wrapLinesCheckBox;
    // End of variables declaration//GEN-END:variables

// =============================================================================
    
    private MouseListener popupListenerProject = new PopupListenerProject();
    private MouseListener menuListener = new MenuListener();
    
    // Buffer maps file name to file editor
    private Map buffer = new HashMap();
    
    // Configuration
    private JifConfiguration config;
    
    // Option control settings
    private boolean adventInLib            = false;
    private boolean createNewFile          = false;
    private boolean helpedCode             = true;
    private boolean makeResource           = true;
    private boolean mappingLive            = true;
    private boolean numberLines            = true;
    private boolean openLastFile           = false;
    private boolean openProjectFiles       = false;
    private boolean scanProjectFiles       = false;
    private boolean syntaxHighlighting     = true;
    private boolean wrapLines              = false;
    
    // Option executeables
    private String blc                     = null;
    private String bres                    = null;
    private String compiler                = null;
    private String interpreterGlulx        = null;
    private String interpreterZcode        = null;

    // Option paths
    private String game                    = null;
    private String library []              = {null, null, null, null};
    
    // Option dialog colours, font and tab size
    private InformContext optionContext   = new InformContext();
    private int           optionTabSize   = JifEditorKit.getTabSize();
    private HighlightText optionHighlight = null;
    
    // Output pane highlighters
    private HighlightText hlighterOutputErrors;
    private HighlightText hlighterOutputWarnings;
    
    // Project
    private JifProject project = new JifProject();
    
    // Project list model
    private JifProjectListModel projectModel;
    
    // Tree builder pre-defined nodes
    private DefaultTreeModel treeModel;
    private InformTreeNode top;
    private InformTreeNode classTree;
    private InformTreeNode constantTree;
    private InformTreeNode functionTree;
    private InformTreeNode globalTree;
    private InformTreeNode objectTree;
    
    // Tree regular expressions
    private Pattern classPattern = Pattern.compile(
            "(?:^|;)(?:\\s|(?:!.*\n))*\n+\\s*Class\\s+(\\w+)",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private Pattern classToClassPattern = Pattern.compile(
            "(?:^|;)\\s*Class\\s+(\\w+)(?:\\s|,|(?:\\(.+\\)))+class\\s+(\\w+)",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private Pattern constantPattern = Pattern.compile(
            "(?:^|;)\\s*Constant\\s+(\\w+)",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private Pattern functionPattern = Pattern.compile(
            ";(?:\\s|(?:!.*\n))*\n+\\s*\\[\\s*(\\w+)",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private Pattern globalPattern = Pattern.compile(
            "(?:^|;)\\s*(?:Array|Global)\\s+(\\w+)",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private Pattern objectPattern = Pattern.compile(
            "(?:^|;)\\s*(?:Object|Nearby)\\s+(?:->\\s+)*(\\w+)",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private Pattern objectToClassPattern = Pattern.compile(
            "(?:^|;)\\s*(?:Object|Nearby)\\s+(?:->\\s+)*(\\w+)\\s+(?:\"[^\"]+\")*\\s*(?:\\w+)*(?:\\s|,)*class\\s+(\\w+)",
            Pattern.MULTILINE|Pattern.CASE_INSENSITIVE
            );
    
    // --- Tree builder maps
    
    // String name to Inform source code asset (Class, Object)
    private Map fileAssets = new TreeMap();
    // String name to Inform object details
    private Map fileObjects = new TreeMap();
    // String name to tree node (Class, Object)
    private Map fileNodes = new TreeMap();
    
    // New Files name counter
    private int countNewFile = 0;
    // titolo di JIF, serve per aggiungerci il nome del progetto aperto
    private String jifVersion = Constants.TITLE;
    
    // --- Nested classes ------------------------------------------------------
     class PopupListenerProject extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                projectPopupMenu.show(
                        e.getComponent(),
                        e.getX(),
                        e.getY());
            }
        }
    }

    class MenuListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            String id = ((JMenuItem) e.getSource()).getText();
            try {
                //se non trovo nessun carattere "§" non vado a capo
                if (getOperation(id).indexOf("§") == -1) {
                    // inserisco la stringa senza andare a capo
                    insertString(getOperation(id));
                } else {
                    StringTokenizer st = new StringTokenizer(getOperation(id), "§");
                    while (st.hasMoreTokens()) {
                        insertString(st.nextToken() + "\n");
                    }
                }
            } catch (Exception ex) {
                System.err.println("Menu mouse press: " + ex.getMessage());
            }
        }
    }
}