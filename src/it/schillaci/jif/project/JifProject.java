package it.schillaci.jif.project;

/*
 * JifProject.java
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

import it.schillaci.jif.core.InformAsset;
import it.schillaci.jif.core.JifFileName;
import it.schillaci.jif.gui.Inspect;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;

/**
 * JifProject: Class for project information within Jif.
 *
 * @author Peter Piggott
 * @version 2.0
 * @since JIF 3.2
 */
public class JifProject {

    // Closed project title
    private static final String closedTitle = "blank";
    
    // Project observers
    private ArrayList observers = new ArrayList();
    
    // Project save file
    private JifFileName projectFile = null;
    
    // Main file for project compilation
    private JifFileName mainFile = null;
    
    // Mode
    private boolean informMode  = true;
    
    // Files contained in the project
    private Vector files = new Vector();
    
    // Class definitions founsd in project files
    private Map classes = new TreeMap();
    
    // Switches
    private Map switches        = new LinkedHashMap();
    
    // Actual Directory of the project (for the relative paths management)
    private String projectDirectory = null;
    
    // --- Constructors --------------------------------------------------------
    
    /** Creates a new instance of JifProject */
    public JifProject() {
    }

    // --- Methods -------------------------------------------------------------
    
    public void addFile(String filePath) {
        // check for relative paths
        File f = new File(projectFile.getDirectory() + File.separator + filePath);
        JifFileName file = null;
        if (f.exists()) {
            file = new JifFileName(projectFile.getDirectory() + File.separator + filePath);
        } else {
            file = new JifFileName(filePath);
        }
        
        addFile(file);
    }

    public void addFile(JifFileName fileName) {
        files.add(fileName);
        Collections.sort(files);
        notifyObservers();
    }

    public void removeFile(JifFileName file) {
        files.remove(file);
        if (file.equals(mainFile)) {
            mainFile = null;
        }
        clear(file.getPath());
        notifyObservers();
    }
    
    public void clearMain() {
        mainFile = null;
        notifyObservers();
    }
    
    public void clear() {
        projectFile = null;
        mainFile = null;
        files.removeAllElements();
        classes.clear();
        notifyObservers();
    }
    
    public void clear(String name) {
        for (Iterator i=classes.entrySet().iterator(); i.hasNext(); ) {
            Entry e = (Entry) i.next();
            InformAsset ia = (InformAsset) e.getValue();
            String path = ia.getPath();
            if (path!=null && path.equals(name)) {
                clearChildren(ia, name);
                if (ia.hasChildren()) {
                    ia.setLocation(new Inspect(ia.getName(), null, -1));
                } else {
                    i.remove();
                }
            }
        }            
    }
    
    private void clearChildren(InformAsset ia, String name) {
        for (Iterator i=ia.iterator(); i.hasNext(); ) {
            String childName = (String) i.next();
            if (classes.containsKey(childName)) {
                InformAsset child = (InformAsset) classes.get(childName);
                if (child.getPath().equals(name)) {
                    i.remove();
                }
            }
        }
    }
    
    // --- Accessor methods ----------------------------------------------------
    
    public void addClasses(Map classes) {
        this.classes.putAll(classes);
    }
    
    public Map getClasses() {
        return Collections.unmodifiableMap(classes);
    }
    
    // ---
    
    public JifFileName getFile() {
        return projectFile;
    }

    public void setFile(JifFileName projectFile) {
        this.projectFile = projectFile;
        notifyObservers();
    }

    public void setFile(String projectFilePath) {
        projectFile = new JifFileName(projectFilePath);
        notifyObservers();
    }
    
    
    public String getDirectory() {
        return projectFile.getDirectory();
    }
    
    public String getName() {
        return projectFile.getName();
    }
    
    public String getPath() {
        return projectFile.getPath();
    }

    public String getTitle() {
        return (String) ((isClosed()) ? 
            closedTitle :
            projectFile.getName());
    }
    
    public boolean isClosed() {
        return projectFile == null;
    }
    
    public boolean isOpen() {
        return projectFile != null;
    }
    
    // ---
    
    public boolean contains(String path) {
        return contains(new JifFileName(path));
    }
    
    public boolean contains(JifFileName fileName) {
        return files.contains(fileName);
    }
    
    public Iterator iterator() {
        return getFiles().iterator();
    }
    
    public Vector getFiles() {
        return (Vector) files.clone();
    }
    
    public void setFiles(Vector files) {
        this.files = files;
        Collections.sort(files);
        notifyObservers();
    }
    
    public boolean getInformMode() {
        return informMode;
    }
    
    public void setInformMode(boolean informMode) {
        this.informMode = informMode;
    }
    
    public JifFileName getMain() {
        return mainFile;
    }
    
    public void setMain(JifFileName mainFile) {
        this.mainFile = mainFile;
        notifyObservers();
    }
    
    public String getMainName() {
        return (isMainClear()) ? "" : mainFile.getName();
    }
    
    public String getMainPath() {
        return mainFile.getPath();
    }
    
    public boolean isMain(JifFileName file) {
        if (mainFile == null) {
            return false;
        }
        return mainFile.equals(file);
    }
    
    public boolean isMainClear() {
        return mainFile == null;
    }
    
    public boolean isMainSet() {
        return mainFile != null;
    }
    
    // ---
    
    public void addSwitch(String switchName, String setting) {
        switches.put(switchName, setting);
    }
    
    public Map getSwitches() {
        return switches;
    }

    public void setSwitch(String switchName, String setting) {
        if (switches.containsKey(switchName)) {
            switches.put(switchName, setting);
        }
    }
    
    public void setSwitches(Map switches) {
        this.switches.clear();
        this.switches.putAll(switches);
    }

    public String getProjectDirectory() {
        return projectDirectory;
    }

    public void setProjectDirectory(String projectDirectory) {
        this.projectDirectory = projectDirectory;
    }
    
    // --- Project observer methods --------------------------------------------
    
    public void registerObserver(JifProjectObserver o) {
        observers.add(o);
    }

    public void notifyObservers() {
        for (int i=0; i<observers.size(); i++) {
            JifProjectObserver observer = (JifProjectObserver) observers.get(i);
            observer.updateProject();
        }
    }

    public void removeObserver(JifProjectObserver o) {
        int i = observers.indexOf(o);
        if (i >= 0) {
            observers.remove(i);
        }
    }
    
}