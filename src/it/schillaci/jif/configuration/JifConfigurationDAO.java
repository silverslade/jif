package it.schillaci.jif.configuration;

/*
 * JifConfigurationDAO.java
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
import it.schillaci.jif.core.JifDAO;
import it.schillaci.jif.inform.InformContext;
import it.schillaci.jif.inform.InformSyntax;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.InputStream;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Data access object for JIF configuration
 *
 * @author Peter Piggott
 * @version 2.0
 * @since JIF 3.2
 */
public class JifConfigurationDAO {
    
    /**
     * Type safe enumeration for project file keywords
     */
    public static class Keyword {
        /**
         * Creates a new <code>Keyword</code> with the specified name.
         * 
         * @param name
         *            the name of the keyword
         */
        protected Keyword(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "JifConfigurationDAO.Keyword[Name: " + name + "]";
        }

        // JifProject keyword name
        private String name;

        // --- Inform Keyords --------------------------------------------------
        
        private static final Keyword ALTKEYS          = new Keyword("[ALTKEYS]");
        private static final Keyword EXECUTE          = new Keyword("[EXECUTE]");
        private static final Keyword HELPEDCODE       = new Keyword("[HELPEDCODE]");
        private static final Keyword MAPPING          = new Keyword("[MAPPING]");
        private static final Keyword MENU             = new Keyword("[MENU]");
        private static final Keyword SWITCH           = new Keyword("[SWITCH]");
        private static final Keyword SYNTAX           = new Keyword("[SYNTAX]");
        private static final Keyword ATTRIBUTE        = new Keyword("[attribute]");
        private static final Keyword KEYWORD          = new Keyword("[keyword]");
        private static final Keyword PROPERTY         = new Keyword("[property]");
        private static final Keyword VERB             = new Keyword("[verb]");
        private static final Keyword SYMBOL           = new Keyword("[SYMBOLS]");
        private static final Keyword PATH             = new Keyword("[PATH]");
        private static final Keyword LIBRARY          = new Keyword("[LIBRAYPATH]");
        private static final Keyword LIBRARY1         = new Keyword("[LIBRAYPATHSECONDARY1]");
        private static final Keyword LIBRARY2         = new Keyword("[LIBRAYPATHSECONDARY2]");
        private static final Keyword LIBRARY3         = new Keyword("[LIBRAYPATHSECONDARY3]");
        private static final Keyword COMPILED         = new Keyword("[COMPILEDPATH]");
        private static final Keyword INTERPRETERZCODE = new Keyword("[INTERPRETERZCODEPATH]");
        private static final Keyword INTERPRETERGLULX = new Keyword("[INTERPRETERGLULXPATH]");
        private static final Keyword COMPILER         = new Keyword("[COMPILERPATH]");
        private static final Keyword BRES             = new Keyword("[BRESPATH]");
        private static final Keyword BLC              = new Keyword("[BLCPATH]");
        private static final Keyword SETTING          = new Keyword("[SETTINGS]");
        private static final Keyword WRAPLINES        = new Keyword("[WRAPLINES]");
        private static final Keyword SYNTAXCHECK      = new Keyword("[SYNTAXCHECK]");
        private static final Keyword HELPEDCODECHECK  = new Keyword("[HELPEDCODECHECK]");
        private static final Keyword MAPPINGCODE      = new Keyword("[MAPPINGCODE]");
        private static final Keyword NUMBERLINES      = new Keyword("[NUMBERLINES]");
        private static final Keyword SCANPROJECT      = new Keyword("[PROJECTSCANFORCLASSES]");
        private static final Keyword OPENPROJECTFILES = new Keyword("[PROJECTOPENALLFILES]");
        private static final Keyword USECOMPILED      = new Keyword("[USECOMPILEDPATH]");
        private static final Keyword OPENLASTFILE     = new Keyword("[OPENLASTFILE]");
        private static final Keyword CREATENEWFILE    = new Keyword("[CREATENEWFILE]");
        private static final Keyword MAKERESOURCE     = new Keyword("[MAKEALWAYSRESOURCE]");
        private static final Keyword TABSIZE          = new Keyword("[TABSIZE]");
        private static final Keyword COLORBOOKMARK    = new Keyword("[COLORBOOKMARK]");
        private static final Keyword COLORBRACKET     = new Keyword("[COLORBRACKET]");
        private static final Keyword COLORERROR       = new Keyword("[COLORERROR]");
        private static final Keyword COLORJUMPTO      = new Keyword("[COLORJUMPTO]");
        private static final Keyword COLORWARNING     = new Keyword("[COLORWARNING]");
        private static final Keyword COLORATTRIBUTE   = new Keyword("[COLORATTRIBUTE]");
        private static final Keyword COLORCOMMENT     = new Keyword("[COLORCOMMENT]");
        private static final Keyword COLORKEYWORD     = new Keyword("[COLORKEYWORD]");
        private static final Keyword COLORNORMAL      = new Keyword("[COLORNORMAL]");
        private static final Keyword COLORNUMBER      = new Keyword("[COLORNUMBER]");
        private static final Keyword COLORPROPERTY    = new Keyword("[COLORPROPERTY]");
        private static final Keyword COLORSTRING      = new Keyword("[COLORSTRING]");
        private static final Keyword COLORVERB        = new Keyword("[COLORVERB]");
        private static final Keyword COLORWHITE       = new Keyword("[COLORWHITE]");
        private static final Keyword COLORWORD        = new Keyword("[COLORWORD]");
        private static final Keyword COLORBACKGROUND  = new Keyword("[COLORBACKGROUND]");
        private static final Keyword FONT             = new Keyword("[DEFAULTFONT]");
        private static final Keyword LOCATIONX        = new Keyword("[LOCATIONX]");
        private static final Keyword LOCATIONY        = new Keyword("[LOCATIONY]");
        private static final Keyword WIDTH            = new Keyword("[WIDTH]");
        private static final Keyword HEIGHT           = new Keyword("[HEIGHT]");
        private static final Keyword MODE             = new Keyword("[MODE]");
        private static final Keyword OUTPUT           = new Keyword("[OUTPUT]");
        private static final Keyword TOOLBAR          = new Keyword("[JTOOLBAR]");
        private static final Keyword TREE             = new Keyword("[JTREE]");
        private static final Keyword FULLSCREEN       = new Keyword("[FULLSCREEN]");
        private static final Keyword DIVIDER1         = new Keyword("[DIVIDER1]");
        private static final Keyword DIVIDER3         = new Keyword("[DIVIDER3]");
        private static final Keyword LASTFILE         = new Keyword("[LASTFILE]");
        private static final Keyword LASTPROJECT      = new Keyword("[LASTPROJECT]");
        private static final Keyword RECENTFILES      = new Keyword("[RECENTFILES]");
        

        // JifProject file keywords
        static final Keyword[] keywords = { 
            ALTKEYS, EXECUTE, HELPEDCODE, MAPPING, MENU, SWITCH, SYNTAX,
            ATTRIBUTE, KEYWORD, PROPERTY, VERB, SYMBOL, PATH, LIBRARY, LIBRARY1,
            LIBRARY2, LIBRARY3, COMPILED, INTERPRETERZCODE, INTERPRETERGLULX,
            COMPILER, BRES, BLC, SETTING, WRAPLINES, SYNTAXCHECK, 
            HELPEDCODECHECK, MAPPINGCODE, NUMBERLINES, SCANPROJECT, 
            OPENPROJECTFILES, USECOMPILED, OPENLASTFILE, CREATENEWFILE,
            MAKERESOURCE, TABSIZE, COLORBOOKMARK, COLORBRACKET, COLORERROR,
            COLORJUMPTO, COLORWARNING, COLORATTRIBUTE, COLORCOMMENT,
            COLORKEYWORD, COLORNORMAL, COLORNUMBER, COLORPROPERTY, COLORSTRING,
            COLORVERB, COLORWHITE, COLORWORD, COLORBACKGROUND, FONT, LOCATIONX, 
            LOCATIONY, WIDTH, HEIGHT, MODE, OUTPUT, TOOLBAR, TREE, FULLSCREEN,
            DIVIDER1, DIVIDER3, LASTFILE, LASTPROJECT, RECENTFILES };

        static {
            // Force JifProjectDAO.Keyword's static initialise to be loaded.
            boolean temp = isKeyword("[MODE]");
        }
    }

    // Map to hold JifProject file keyords
    private static final Map keywords = new HashMap();

    // Initialisation
    static {
        // JifProject Filee keywords
        for (int i = 0; i < Keyword.keywords.length; i++) {
            keywords.put(Keyword.keywords[i].getName(), Keyword.keywords[i]);
        }
    }

    private static final Pattern altKeysPattern = Pattern.compile("\n\\[ALTKEYS\\]([^,]+),([^\n]+)");
    private static final Pattern executePattern = Pattern.compile("\n\\[EXECUTE\\]([^,]+),([^\n]+)");
    private static final Pattern helpedCodePattern = Pattern.compile("\n\\[HELPEDCODE\\]([^,]+),([^\n]+)");
    private static final Pattern mappingPattern = Pattern.compile("\n\\[MAPPING\\]([^,]+),([^\n]+)");
    private static final Pattern menuPattern = Pattern.compile("\n\\[MENU\\]\\[(.+)\\]\\*");
    private static final Pattern subMenuPattern = Pattern.compile("\n\\[MENU\\]\\[(.+)\\]([^,*]+),([^\n]+)");
    private static final Pattern switchPattern = Pattern.compile("\n\\[SWITCH\\]([^,]+),([^\n]+)");
    private static final Pattern attributePattern = Pattern.compile("\n\\[SYNTAX\\]\\[attribute\\](.+)");
    private static final Pattern keywordPattern = Pattern.compile("\n\\[SYNTAX\\]\\[keyword\\](.+)");
    private static final Pattern propertyPattern = Pattern.compile("\n\\[SYNTAX\\]\\[property\\](.+)");
    private static final Pattern verbPattern = Pattern.compile("\n\\[SYNTAX\\]\\[verb\\](.+)");
    private static final Pattern symbolPattern = Pattern.compile("\n\\[SYMBOLS\\](.+)");
    private static final Pattern libraryPathPattern = Pattern.compile("\n\\[LIBRAYPATH\\](.+)");
    private static final Pattern libraryPath1Pattern = Pattern.compile("\n\\[LIBRAYPATHSECONDARY1\\](.+)");
    private static final Pattern libraryPath2Pattern = Pattern.compile("\n\\[LIBRAYPATHSECONDARY2\\](.+)");
    private static final Pattern libraryPath3Pattern = Pattern.compile("\n\\[LIBRAYPATHSECONDARY3\\](.+)");
    private static final Pattern compiledPathPattern = Pattern.compile("\n\\[COMPILEDPATH\\](.+)");
    private static final Pattern interpreterZcodePathPattern = Pattern.compile("\n\\[INTERPRETERZCODEPATH\\](.+)");
    private static final Pattern interpreterGlulxPathPattern = Pattern.compile("\n\\[INTERPRETERGLULXPATH\\](.+)");
    private static final Pattern compilerPathPattern = Pattern.compile("\n\\[COMPILERPATH\\](.+)");
    private static final Pattern bresPathPattern = Pattern.compile("\n\\[BRESPATH\\](.+)");
    private static final Pattern blcPathPattern = Pattern.compile("\n\\[BLCPATH\\](.+)");
    private static final Pattern wrapLinesPattern = Pattern.compile("\n\\[WRAPLINES\\](.+)");
    private static final Pattern syntaxCheckPattern = Pattern.compile("\n\\[SYNTAXCHECK\\](.+)");
    private static final Pattern helpedCodeCheckPattern = Pattern.compile("\n\\[HELPEDCODECHECK\\](.+)");
    private static final Pattern mappingCodePattern = Pattern.compile("\n\\[MAPPINGCODE\\](.+)");
    private static final Pattern numberLinesPattern = Pattern.compile("\n\\[NUMBERLINES\\](.+)");
    private static final Pattern scanProjectFilesPattern = Pattern.compile("\n\\[PROJECTSCANFORCLASSES\\](.+)");
    private static final Pattern openProjectFilesPattern = Pattern.compile("\n\\[PROJECTOPENALLFILES\\](.+)");
    private static final Pattern useCompiledPathPattern = Pattern.compile("\n\\[USECOMPILEDPATH\\](.+)");
    private static final Pattern openLastFilePattern = Pattern.compile("\n\\[OPENLASTFILE\\](.+)");
    private static final Pattern createNewPattern = Pattern.compile("\n\\[CREATENEWFILE\\](.+)");
    private static final Pattern makeResourcePattern = Pattern.compile("\n\\[MAKEALWAYSRESOURCE\\](.+)");
    private static final Pattern tabSizePattern = Pattern.compile("\n\\[TABSIZE\\](.+)");
    private static final Pattern colorBookmarkPattern = Pattern.compile("\n\\[COLORBOOKMARK\\](.+),(.+),(.+)");
    private static final Pattern colorBracketPattern = Pattern.compile("\n\\[COLORBRACKET\\](.+),(.+),(.+)");
    private static final Pattern colorErrorPattern = Pattern.compile("\n\\[COLORERROR\\](.+),(.+),(.+)");
    private static final Pattern colorJumpToPattern = Pattern.compile("\n\\[COLORJUMPTO\\](.+),(.+),(.+)");
    private static final Pattern colorWarningPattern = Pattern.compile("\n\\[COLORWARNING\\](.+),(.+),(.+)");
    private static final Pattern colorAttributePattern = Pattern.compile("\n\\[COLORATTRIBUTE\\](.+),(.+),(.+)");
    private static final Pattern colorCommentPattern = Pattern.compile("\n\\[COLORCOMMENT\\](.+),(.+),(.+)");
    private static final Pattern colorKeywordPattern = Pattern.compile("\n\\[COLORKEYWORD\\](.+),(.+),(.+)");
    private static final Pattern colorNormalPattern = Pattern.compile("\n\\[COLORNORMAL\\](.+),(.+),(.+)");
    private static final Pattern colorNumberPattern = Pattern.compile("\n\\[COLORNUMBER\\](.+),(.+),(.+)");
    private static final Pattern colorPropertyPattern = Pattern.compile("\n\\[COLORPROPERTY\\](.+),(.+),(.+)");
    private static final Pattern colorStringPattern = Pattern.compile("\n\\[COLORSTRING\\](.+),(.+),(.+)");
    private static final Pattern colorVerbPattern = Pattern.compile("\n\\[COLORVERB\\](.+),(.+),(.+)");
    private static final Pattern colorWhitePattern = Pattern.compile("\n\\[COLORWHITE\\](.+),(.+),(.+)");
    private static final Pattern colorWordPattern = Pattern.compile("\n\\[COLORWORD\\](.+),(.+),(.+)");
    private static final Pattern colorBackgroundPattern = Pattern.compile("\n\\[COLORBACKGROUND\\](.+),(.+),(.+)");
    private static final Pattern fontPattern = Pattern.compile("\n\\[DEFAULTFONT\\](.+),(.+),(.+)"); 
    private static final Pattern locationXPattern = Pattern.compile("\n\\[LOCATIONX\\](.+)");
    private static final Pattern locationYPattern = Pattern.compile("\n\\[LOCATIONY\\](.+)");
    private static final Pattern widthPattern = Pattern.compile("\n\\[WIDTH\\](.+)");
    private static final Pattern heightPattern = Pattern.compile("\n\\[HEIGHT\\](.+)");
    private static final Pattern modePattern = Pattern.compile("\n\\[MODE\\](.+)");
    private static final Pattern outputPattern = Pattern.compile("\n\\[OUTPUT\\](.+)");
    private static final Pattern toolbarPattern = Pattern.compile("\n\\[JTOOLBAR\\](.+)");
    private static final Pattern treePattern = Pattern.compile("\n\\[JTREE\\](.+)");
    private static final Pattern fullScreenPattern = Pattern.compile("\n\\[FULLSCREEN\\](.+)");
    private static final Pattern divider1Pattern = Pattern.compile("\n\\[DIVIDER1\\](.+)");
    private static final Pattern divider3Pattern = Pattern.compile("\n\\[DIVIDER3\\](.+)");
    private static final Pattern lastFilePattern = Pattern.compile("\n\\[LASTFILE\\]([^\n]+)");
    private static final Pattern lastProjectPattern = Pattern.compile("\n\\[LASTPROJECT\\]([^\n]+)");
    private static final Pattern recentFilesPattern = Pattern.compile("\n\\[RECENTFILES\\](.+)");
    
    // File used to store Jif configuration data
    private static String directory;
    
    /**
     * Creates a new Jif.cfg file from the default version stored in the jar file
     */
    public static void create(File file) 
            throws JifConfigurationException {

        try {
            // Load configuration from jar file
            InputStream is = ClassLoader.getSystemClassLoader().getResource(Constants.configFileName).openStream();
            String configuration = JifDAO.read(is);
            
            // Save configuration as a new configuration file
            JifDAO.save(file, configuration);
            
        } catch (Exception ex) {
            throw new JifConfigurationException("Create (" + file + "): " + ex.getMessage());
        }
    }
    
    /**
     * Load a new configuration object from persistent storage
     * 
     * @param file
     *              the persistent storage from which to load the configuration
     * @throws JifConfigurationException
     */
    public static JifConfiguration load(File file)
            throws JifConfigurationException {

        JifConfiguration config = new JifConfiguration();
        InformContext context = config.getContext();
        
        config.setWorkingDirectory(JifConfigurationDAO.directory);
        config.setFile(file.getAbsolutePath());

        try {
            CharBuffer cb = JifDAO.buffer(file);
            
            Matcher m;
            
            // Alternate key assignments
            m = altKeysPattern.matcher(cb);
            while (m.find()) {
                config.addAltKey(m.group(1), m.group(2));
            }
            
            // Execute command assignments
            m = executePattern.matcher(cb);
            while (m.find()) {
                config.addExecuteCommand(m.group(1), m.group(2));
            }
            
            // Help code assignments
            
            m = helpedCodePattern.matcher(cb);
            while (m.find()) {
                config.addHelpCode(m.group(1), m.group(2));
            }

            // Mapping assignments
            m = mappingPattern.matcher(cb);
            while (m.find()) {
                config.addMapping(m.group(1), m.group(2));
            }
            
            // Menus
            m = menuPattern.matcher(cb);
            while (m.find()) {
                config.addMenu(m.group(1));
            }
            
            // Sub-menus + operations
            m = subMenuPattern.matcher(cb);
            while (m.find()) {
                config.addSubMenu(m.group(1), m.group(2));
                config.addOperation(m.group(2), m.group(3));
            }
    
            // Compiler switch settings
            m = switchPattern.matcher(cb);
            while (m.find()) {
                config.addSwitch(m.group(1), m.group(2));
            }
            
            // Syntax - attribute
            m = attributePattern.matcher(cb);
            while (m.find()) {
                config.addAttribute(m.group(1));
            }
            
            // Syntax - keyword
            m = keywordPattern.matcher(cb);
            while (m.find()) {
                config.addKeyword(m.group(1));
            }
            
            // Syntax - property
            m = propertyPattern.matcher(cb);
            while (m.find()) {
                config.addProperty(m.group(1));
            }
            
            // Syntax - verb
            m = verbPattern.matcher(cb);
            while (m.find()) {
                config.addVerb(m.group(1));
            }

            // Symbols
            m = symbolPattern.matcher(cb);
            while (m.find()) {
                config.addSymbol(m.group(1));
            }
            
            // Library path - primary
            m = libraryPathPattern.matcher(cb);
            while (m.find()) {
                config.setLibraryPath(m.group(1).trim());
            }
            // Library path - secondary 1
            m = libraryPath1Pattern.matcher(cb);
            while (m.find()) {
                config.setLibraryPath1(m.group(1).trim());
            }
            // Library path - secondary 2
            m = libraryPath2Pattern.matcher(cb);
            while (m.find()) {
                config.setLibraryPath2(m.group(1).trim());
            }
            // Library path - secondary 3
            m = libraryPath3Pattern.matcher(cb);
            while (m.find()) {
                config.setLibraryPath3(m.group(1).trim());
            }
            
            // Compiled zcode path
            m = compiledPathPattern.matcher(cb);
            while (m.find()) {
                config.setGamePath(m.group(1).trim());
            }
            
            // Zcode interpreter path
            m = interpreterZcodePathPattern.matcher(cb);
            while (m.find()) {
                config.setInterpreterZcodePath(m.group(1).trim());
            }
            
            // Glulx interpreter path
            m = interpreterGlulxPathPattern.matcher(cb);
            while (m.find()) {
                config.setInterpreterGlulxPath(m.group(1).trim());
            }
            
            // Inform compiler path
            m = compilerPathPattern.matcher(cb);
            while (m.find()) {
                config.setCompilerPath(m.group(1).trim());
            }
            
            // Bres tool path
            m = bresPathPattern.matcher(cb);
            while (m.find()) {
                config.setBresPath(m.group(1).trim());
            }
            
            // Blc tool path
            m = blcPathPattern.matcher(cb);
            while (m.find()) {
                config.setBlcPath(m.group(1).trim());
            }
            
            // Wrap lines setting
            m = wrapLinesPattern.matcher(cb);
            while (m.find()) {
                config.setWrapLines(m.group(1).equals("true"));
            }
            
            // Syntax check setting
            m = syntaxCheckPattern.matcher(cb);
            while (m.find()) {
                config.setSyntaxHighlighting(m.group(1).equals("true"));
            }
            
            // Help code setting
            m = helpedCodeCheckPattern.matcher(cb);
            while (m.find()) {
                config.setHelpedCode(m.group(1).equals("true"));
            }
            
            // Mapping code setting
            m = mappingCodePattern.matcher(cb);
            while (m.find()) {
                config.setMappingLive(m.group(1).equals("true"));
            }
            
            // Number lines setting
            m = numberLinesPattern.matcher(cb);
            while (m.find()) {
                config.setNumberLines(m.group(1).equals("true"));
            }
            
            // Scan project for classes setting
            m = scanProjectFilesPattern.matcher(cb);
            while (m.find()) {
                config.setScanProjectFiles(m.group(1).equals("true"));
            }
            
            // Open project files setting
            m = openProjectFilesPattern.matcher(cb);
            while (m.find()) {
                config.setOpenProjectFiles(m.group(1).equals("true"));
            }
            
            // Use compiled path setting
            m = useCompiledPathPattern.matcher(cb);
            while (m.find()) {
                config.setAdventInLib(m.group(1).equals("true"));
            }
            
            // Open last file setting
            m = openLastFilePattern.matcher(cb);
            while (m.find()) {
                config.setOpenLastFile(m.group(1).equals("true"));
            }
            
            // Create new file setting
            m = createNewPattern.matcher(cb);
            while (m.find()) {
                config.setCreateNewFile(m.group(1).equals("true"));
            }
            
            // Always make resource setting
            m = makeResourcePattern.matcher(cb);
            while (m.find()) {
                config.setMakeResource(m.group(1).equals("true"));
            }
    
            // Settings - TABSIZE
            m = tabSizePattern.matcher(cb);
            while (m.find()) {
                int tabSize;
                try {
                    tabSize = Integer.parseInt(m.group(1));
                } catch (Exception e) {
                    tabSize = 4;
                }
                config.setTabSize(tabSize);
            }

            // Bookmark color
            m = colorBookmarkPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3))
                            );
                    context.setForeground(InformSyntax.Bookmarks, color);
                } catch (Exception ex) {}
            }
            
            // Bracket color
            m = colorBracketPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3))
                            );
                    context.setForeground(InformSyntax.Brackets, color);
                } catch (Exception ex) {}
            }
            
            // Error color
            m = colorErrorPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3))
                            );
                    context.setForeground(InformSyntax.Errors, color);
                } catch (Exception ex) {}
            }
            
            // Jump to color
            m = colorJumpToPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)));
                    context.setForeground(InformSyntax.JumpTo, color);
                } catch (Exception ex) {}
            }
            
            // Warning color
            m = colorWarningPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)));
                    context.setForeground(InformSyntax.Attribute, color);
                } catch (Exception ex) {}
            }
            
            // Attribute color
            m = colorAttributePattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)));
                    context.setForeground(InformSyntax.Attribute, color);
                } catch (Exception ex) {}
            }
            
            // Comment color
            m = colorCommentPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)));
                    context.setForeground(InformSyntax.Comment, color);
                } catch (Exception ex) {}
            }
            
            // Keyword color
            m = colorKeywordPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)));
                    context.setForeground(InformSyntax.Keyword, color);
                } catch (Exception ex) {}
            }
            
            // Normal text color
            m = colorNormalPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3))
                            );
                    context.setForeground(InformSyntax.Normal, color);
                } catch (Exception ex) {}
            }
            
            // Number color
            m = colorNumberPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3))
                            );
                    context.setForeground(InformSyntax.Number, color);
                } catch (Exception ex) {}
            }
            
            // Property color
            m = colorPropertyPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3))
                            );
                    context.setForeground(InformSyntax.Property, color);
                } catch (Exception ex) {}
            }
            
            // String color
            m = colorStringPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3))
                            );
                    context.setForeground(InformSyntax.String, color);
                } catch (Exception ex) {}
            }
            
            // Verb color
            m = colorVerbPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3))
                            );
                    context.setForeground(InformSyntax.Verb, color);
                } catch (Exception ex) {}
            }
            
            // White space color (shows errors)
            m = colorWhitePattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3))
                            );
                    context.setForeground(InformSyntax.White, color);
                } catch (Exception ex) {}
            }
            
            // Word color
            m = colorWordPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3))
                            );
                    context.setForeground(InformSyntax.Word, color);
                } catch (Exception ex) {}
            }
            
            // Background color
            m = colorBackgroundPattern.matcher(cb);
            while (m.find()) {
                try {
                    Color color = new Color(
                            Integer.parseInt(m.group(1)),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)));
                    context.setBackground(color);
                } catch (Exception ex) {}
            }
            
            // Font
            m = fontPattern.matcher(cb);
            while (m.find()) {
                try {
                    Font font = new Font(
                            m.group(1),
                            Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3))
                            );
                    context.setFont(font);
                } catch (Exception ex) {}
            }
            
            // Frame X co-ordinate setting
            m = locationXPattern.matcher(cb);
            while (m.find()) {
                try {
                    config.setFrameX(Integer.parseInt(m.group(1)));
                } catch (Exception ex) {}
            }
            
            // Frame Y co-ordinate setting
            m = locationYPattern.matcher(cb);
            while (m.find()) {
                try {
                    config.setFrameY(Integer.parseInt(m.group(1)));
                } catch (Exception ex) {}
            }
            
            // Frame width setting
            m = widthPattern.matcher(cb);
            while (m.find()) {
                try {
                    config.setFrameWidth(Integer.parseInt(m.group(1)));
                } catch (Exception ex) {}
            }
            
            // Frame height setting
            m = heightPattern.matcher(cb);
            while (m.find()) {
                try {
                    config.setFrameHeight(Integer.parseInt(m.group(1)));
                } catch (Exception ex) {}
            }
            
            // Inform mode (Inform/Glulx)
            m = modePattern.matcher(cb);
            while (m.find()) {
                config.setInformMode(m.group(1).equalsIgnoreCase("inform"));
            }
            
            // Output window shown 
            m = outputPattern.matcher(cb);
            while (m.find()) {
                config.setOutputVisible(m.group(1).equals("true")?true:false);
            }
            
            // Toolbar shown
            m = toolbarPattern.matcher(cb);
            while (m.find()) {
                config.setToolbarVisible(m.group(1).equals("true")?true:false);
            }
            
            // Project/Tree/Search window shown
            m = treePattern.matcher(cb);
            while (m.find()) {
                config.setTreeVisible(m.group(1).equals("true")?true:false);
            }
            
            // Fullscreen setting
            m = fullScreenPattern.matcher(cb);
            while (m.find()) {
                config.setFullScreen(m.group(1).equals("true")?true:false);
            }
            
            // Frame divider left/right
            m = divider1Pattern.matcher(cb);
            while (m.find()) {
                try {
                    config.setDivider1(Integer.parseInt(m.group(1)));
                } catch (Exception ex) {}
            }
            
            // Frame divider top/bottom
            m = divider3Pattern.matcher(cb);
            while (m.find()) {
                try {
                    config.setDivider3(Integer.parseInt(m.group(1)));
                } catch (Exception ex) {}
            }
            
            // Last file opened
            m = lastFilePattern.matcher(cb);
            while (m.find()) {
                config.setLastFile(m.group(1));
            }

            // Last project opened
            m = lastProjectPattern.matcher(cb);
            while (m.find()) {
                config.setLastProject(m.group(1));
            }
            
            // Recent files
            m = recentFilesPattern.matcher(cb);
            while (m.find()) {
                config.addRecentFile(m.group(1));
            }
 
        } catch (Exception ex) {
            throw new JifConfigurationException("Initialise (" + file + "): " + ex.getMessage());
        }
        return config;
    }

    /**
     * Store a configuration object to persistent storage
     *
     * @param config
     *              the <code>JifConfiguration</code> to be stored
     * @throws JifProjectException
     */
    public static void store(JifConfiguration config)
            throws JifConfigurationException {
        
        File file = new File(config.getFilePath());

        try {
            StringBuilder output = new StringBuilder();
            output
                    .append("############################################################################\n")
                    .append("# Main Jif configuration file                                               \n")
                    .append("############################################################################\n");
                    
            // ALTKEYS Section - alpha sequence
            output.append("\n# ").append(Keyword.ALTKEYS.getName()).append(" Section\n\n");
            for (Iterator i = config.getAltKeysAlpha().iterator(); i.hasNext();) {
                String key = (String) i.next();
                String value = (String) config.getAltKeys().get(key);
                output.append(Keyword.ALTKEYS.getName()).append(key).append(",").append(value).append("\n");
            }
            for (Iterator i = config.getExecuteCommands().keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                String value = (String) config.getExecuteCommands().get(key);
                output.append(Keyword.EXECUTE.getName()).append(key).append(",").append(value).append("\n");
            }
            
            // HELPEDCODE Section - alpha sequence
            output
                    .append("\n# ").append(Keyword.HELPEDCODE.getName()).append(" Section\n\n")
                    .append("# [ret] = Return\n")
                    .append("# [tab] = Tab char\n")
                    .append("# @     = Cursor Position\n\n");
            for (Iterator i = config.getHelpCodesAlpha().iterator(); i.hasNext();) {
                String key = (String) i.next();
                String value = (String) config.getHelpCodes().get(key);
                output.append(Keyword.HELPEDCODE.getName()).append(key).append(",").append(value).append("\n");
            }
            
            // MAPPING Section
            output.append("\n# ").append(Keyword.MAPPING.getName()).append(" Section\n\n");
            for (Iterator i = config.getMappingsAlpha().iterator(); i.hasNext();) {
                String key = (String) i.next();
                String value = (String) config.getMappings().get(key);
                output.append(Keyword.MAPPING.getName()).append(key).append(",").append(value).append("\n");
            }
            
            // MENU Section - entry sequence
            output.append("\n# ").append(Keyword.MENU.getName()).append(" Section\n\n");
            for (Iterator i = config.menuIterator(); i.hasNext();) {
                String menu = (String) i.next();
                output.append(Keyword.MENU.getName()).append("[").append(menu).append("]*\n");
                for (Iterator j = config.getSubMenu(menu).iterator(); j.hasNext();) {
                    String subMenu  = (String) j.next();
                    output.append(Keyword.MENU.getName()).append("[").append(menu).append("]").append(subMenu).append(",").append(config.getOperations().get(subMenu)).append("\n");
                }
            }
            
            // # [SWITCH] Section - entry sequence
            output.append("\n# ").append(Keyword.SWITCH.getName()).append(" Section\n\n");
            for (Iterator i = config.getSwitchesSet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                String value = (String) config.getSwitches().get(key);
                output.append(Keyword.SWITCH.getName()).append(key).append(",").append(value).append("\n");
            }
            
            // # [SYNTAX] Section - alpha sequence
            output.append("\n# ").append(Keyword.SYNTAX.getName()).append(" Section\n\n");
            for (Iterator i = config.getAttributesAlpha().iterator(); i.hasNext();) {
                String key = (String) i.next();
                output.append(Keyword.SYNTAX.getName()).append(Keyword.ATTRIBUTE.getName()).append(key).append("\n");
            }
            for (Iterator i = config.getKeywordsAlpha().iterator(); i.hasNext();) {
                String key = (String) i.next();
                output.append(Keyword.SYNTAX.getName()).append(Keyword.KEYWORD.getName()).append(key).append("\n");
            }
            for (Iterator i = config.getPropertiesAlpha().iterator(); i.hasNext();) {
                String key = (String) i.next();
                output.append(Keyword.SYNTAX.getName()).append(Keyword.PROPERTY.getName()).append(key).append("\n");
            }
            for (Iterator i = config.getVerbsAlpha().iterator(); i.hasNext();) {
                String key = (String) i.next();
                output.append(Keyword.SYNTAX.getName()).append(Keyword.VERB.getName()).append(key).append("\n");
            }
            
            //# [SYMBOLS] Section
            output.append("\n# ").append(Keyword.SYMBOL.getName()).append(" Section\n\n");
            for (Iterator i = config.symbolIterator(); i.hasNext();) {
                String key = (String) i.next();
                output.append(Keyword.SYMBOL.getName()).append(key).append("\n");
            }
            
            // PATHS section
            output
                    .append("\n# ").append(Keyword.PATH.getName()).append(" Section\n\n")
                    .append(Keyword.LIBRARY.getName()).append(config.getLibraryPath()).append("\n")
                    .append(Keyword.LIBRARY1.getName()).append(config.getLibraryPath1()).append("\n")
                    .append(Keyword.LIBRARY2.getName()).append(config.getLibraryPath2()).append("\n")
                    .append(Keyword.LIBRARY3.getName()).append(config.getLibraryPath3()).append("\n")
                    .append(Keyword.COMPILED.getName()).append(config.getGamePath()).append("\n")
                    .append(Keyword.INTERPRETERZCODE.getName()).append(config.getInterpreterZcodePath()).append("\n")
                    .append(Keyword.INTERPRETERGLULX.getName()).append(config.getInterpreterGlulxPath()).append("\n")
                    .append(Keyword.COMPILER.getName()).append(config.getCompilerPath()).append("\n")
                    .append(Keyword.BRES.getName()).append(config.getBresPath()).append("\n")
                    .append(Keyword.BLC.getName()).append(config.getBlcPath()).append("\n");
            
            // SETTINGS section
            InformContext context = config.getContext();
            output
                    .append("\n# ").append(Keyword.SETTING.getName()).append(" Section\n\n")
                    .append(Keyword.WRAPLINES.getName()).append(config.getWrapLines()).append("\n")
                    .append(Keyword.SYNTAXCHECK.getName()).append(config.getSyntaxHighlighting()).append("\n")
                    .append(Keyword.HELPEDCODECHECK.getName()).append(config.getHelpedCode()).append("\n")
                    .append(Keyword.MAPPINGCODE.getName()).append(config.getMappingLive()).append("\n")
                    .append(Keyword.NUMBERLINES.getName()).append(config.getNumberLines()).append("\n")
                    .append(Keyword.SCANPROJECT.getName()).append(config.getScanProjectFiles()).append("\n")
                    .append(Keyword.OPENPROJECTFILES.getName()).append(config.getOpenProjectFiles()).append("\n")
                    .append(Keyword.USECOMPILED.getName()).append(config.getAdventInLib()).append("\n")
                    .append(Keyword.OPENLASTFILE.getName()).append(config.getOpenLastFile()).append("\n")
                    .append(Keyword.CREATENEWFILE.getName()).append(config.getCreateNewFile()).append("\n")
                    .append(Keyword.MAKERESOURCE.getName()).append(config.getMakeResource()).append("\n")
                    .append(Keyword.TABSIZE.getName()).append(config.getTabSize()).append("\n")
                    .append(Keyword.COLORBOOKMARK.getName()).append(context.getForegroundRGB(InformSyntax.Bookmarks)).append("\n")
                    .append(Keyword.COLORBRACKET.getName()).append(context.getForegroundRGB(InformSyntax.Brackets)).append("\n")
                    .append(Keyword.COLORERROR.getName()).append(context.getForegroundRGB(InformSyntax.Errors)).append("\n")
                    .append(Keyword.COLORJUMPTO.getName()).append(context.getForegroundRGB(InformSyntax.JumpTo)).append("\n")
                    .append(Keyword.COLORWARNING.getName()).append(context.getForegroundRGB(InformSyntax.Warnings)).append("\n")
                    .append(Keyword.COLORATTRIBUTE.getName()).append(context.getForegroundRGB(InformSyntax.Attribute)).append("\n")
                    .append(Keyword.COLORCOMMENT.getName()).append(context.getForegroundRGB(InformSyntax.Comment)).append("\n")
                    .append(Keyword.COLORKEYWORD.getName()).append(context.getForegroundRGB(InformSyntax.Keyword)).append("\n")
                    .append(Keyword.COLORNORMAL.getName()).append(context.getForegroundRGB(InformSyntax.Normal)).append("\n")
                    .append(Keyword.COLORNUMBER.getName()).append(context.getForegroundRGB(InformSyntax.Number)).append("\n")
                    .append(Keyword.COLORPROPERTY.getName()).append(context.getForegroundRGB(InformSyntax.Property)).append("\n")
                    .append(Keyword.COLORSTRING.getName()).append(context.getForegroundRGB(InformSyntax.String)).append("\n")
                    .append(Keyword.COLORVERB.getName()).append(context.getForegroundRGB(InformSyntax.Verb)).append("\n")
                    .append(Keyword.COLORWHITE.getName()).append(context.getForegroundRGB(InformSyntax.White)).append("\n")
                    .append(Keyword.COLORWORD.getName()).append(context.getForegroundRGB(InformSyntax.Word)).append("\n")
                    .append(Keyword.COLORBACKGROUND.getName()).append(context.getBackgroundRGB()).append("\n")
                    .append(Keyword.FONT.getName()).append(context.getFontNameStyleSize()).append("\n")
                    .append(Keyword.LOCATIONX.getName()).append(config.getFrameX()).append("\n")
                    .append(Keyword.LOCATIONY.getName()).append(config.getFrameY()).append("\n")
                    .append(Keyword.WIDTH.getName()).append(config.getFrameWidth()).append("\n")
                    .append(Keyword.HEIGHT.getName()).append(config.getFrameHeight()).append("\n")
                    .append(Keyword.MODE.getName()).append((config.getInformMode()?"INFORM":"GLULX") ).append("\n")
                    .append(Keyword.OUTPUT.getName()).append(config.isOutputVisible()).append("\n")
                    .append(Keyword.TOOLBAR.getName()).append(config.isToolbarVisible()).append("\n")
                    .append(Keyword.TREE.getName()).append(config.isTreeVisible()).append("\n")
                    .append(Keyword.FULLSCREEN.getName()).append(config.isFullScreen()).append("\n")
                    .append(Keyword.DIVIDER1.getName()).append(config.getDivider1()).append("\n")
                    .append(Keyword.DIVIDER3.getName()).append(config.getDivider3()).append("\n")
                    .append(Keyword.LASTFILE.getName()).append((config.getLastFile()==null?"":config.getLastFilePath())).append("\n")
                    .append(Keyword.LASTPROJECT.getName()).append((config.getLastProject()==null?"":config.getLastProjectPath())).append("\n");
            
            // Recent files section - entry sequence
            output.append("\n# ").append(Keyword.RECENTFILES.getName()).append(" Section\n\n");
            for (Iterator i = config.recentFileIterator(); i.hasNext(); ) {
                String filePath = (String) i.next();
                output.append(Keyword.RECENTFILES.getName()).append(filePath).append("\n");
            }
            
            JifDAO.save(file, output.toString());
            
        } catch (Exception ex) {
            throw new JifConfigurationException("Persist (" + file + "): " + ex.getMessage());
        }
    }
    
    /**
     * Reloads an existing configuration object from persistent storage
     *
     * @param config
     *              the <code>JifConfiguration</code> to be reloaded
     * @throws JifConfigurationException
     */
    public static void reload(JifConfiguration config) 
            throws JifConfigurationException {

        File file = new File(config.getFilePath());
        JifConfiguration newConfig = JifConfigurationDAO.load(file);

        config.setAdventInLib(newConfig.getAdventInLib());
        config.setAltKeys(newConfig.getAltKeys());
        config.setAttributes(newConfig.getAttributes());
        config.setBlcPath(newConfig.getBlcPath());
        config.setBresPath(newConfig.getBresPath());
        config.setCompilerPath(newConfig.getCompilerPath());
        config.setContext(newConfig.getContext());
        config.setCreateNewFile(newConfig.getCreateNewFile());
        config.setDivider1(newConfig.getDivider1());
        config.setDivider3(newConfig.getDivider3());
        config.setExecuteCommands(newConfig.getExecuteCommands());
        config.setFile(newConfig.getFile().getPath());
        config.setFrameHeight(newConfig.getFrameHeight());
        config.setFrameWidth(newConfig.getFrameWidth());
        config.setFrameX(newConfig.getFrameX());
        config.setFrameY(newConfig.getFrameY());
        config.setGamePath(newConfig.getGamePath());
        config.setHelpCodes(newConfig.getHelpCodes());
        config.setHelpedCode(newConfig.getHelpedCode());
        config.setInformMode(newConfig.getInformMode());
        config.setInterpreterGlulxPath(newConfig.getInterpreterGlulxPath());
        config.setInterpreterZcodePath(newConfig.getInterpreterZcodePath());
        config.setKeywords(newConfig.getKeywords());
        config.setLastFile((newConfig.getLastFile() == null)?"":newConfig.getLastFilePath());
        config.setLastProject((newConfig.getLastProject() == null)?"":newConfig.getLastProjectPath());
        config.setLibraryPath(newConfig.getLibraryPath());
        config.setLibraryPath1(newConfig.getLibraryPath1());
        config.setLibraryPath2(newConfig.getLibraryPath2());
        config.setLibraryPath3(newConfig.getLibraryPath3());
        config.setMakeResource(newConfig.getMakeResource());
        config.setMappings(newConfig.getMappings());
        config.setMappingLive(newConfig.getMappingLive());
        config.setMenus(newConfig.getMenus());
        config.setNumberLines(newConfig.getNumberLines());
        config.setOpenLastFile(newConfig.getOpenLastFile());
        config.setOpenProjectFiles(newConfig.getOpenProjectFiles());
        config.setOperations(newConfig.getOperations());
        config.setOutputVisible(newConfig.isOutputVisible());
        config.setProperties(newConfig.getProperties());
        config.setRecentFiles(newConfig.getRecentFiles());
        config.setScanProjectFiles(newConfig.getScanProjectFiles());
        config.setSwitches(newConfig.getSwitches());
        config.setSymbols(newConfig.getSymbols());
        config.setSyntaxHighlighting(newConfig.getSyntaxHighlighting());
        config.setTabSize(newConfig.getTabSize());
        config.setToolbarVisible(newConfig.isToolbarVisible());
        config.setTreeVisible(newConfig.isTreeVisible());
        config.setVerbs(newConfig.getVerbs());
        config.setWorkingDirectory(newConfig.getWorkingDirectory());
        config.setWrapLines(newConfig.getWrapLines());
    }
    
    /**
     * Returns <tt>true</tt> if the specified symbol is an JifProject file keyword
     * 
     * @param symbol
     *            Symbol to be tested as an JifProject file keyword
     * @return <tt>true</tt> if this is an JifProject file keyword
     */
    public static boolean isKeyword(String symbol) {
        return keywords.containsKey(symbol);
    }

}
