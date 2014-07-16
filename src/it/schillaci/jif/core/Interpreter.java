package it.schillaci.jif.core;

/*
 * Interpreter.java
 *
 * This file is part of JIF.
 *
 * Jif is substantially an editor entirely written in java that allows the
 * file management for the creation of text-adventures based on Graham
 * Nelson's Inform standard [a programming language for Interactive Fiction].
 * With Jif, it's possible to edit, compile and run a Text Adventure in
 * Inform 6 format.
 *
 * Copyright (C) 2004-2013  Alessandro Schillaci
 *
 * WeB   : http://www.slade.altervista.org/JIF/
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

import java.io.File;
import java.io.IOException;

/**
 * Interpreter
 * @author Peter Piggott
 * @version 1.0
 * @since Jif 3.6
 */
public class Interpreter {
    
    private String ok = java.util.ResourceBundle.getBundle("JIF").getString("OK_COMPILER2");
    private String path;
    
    /**
     * Creates a new instance of Interpreter.
     * @param path
     *              The path of the interpreter
     */
    public Interpreter(String path) {
        this.path = path;
    }
    
    /**
     * Run a game with the interpreter.
     * 
     * @param game
     *              The path of the game file to be executed
     */
    public String run(String game) throws IOException {
        
        StringBuilder output = new StringBuilder();
        
        String command[] = new String[2];
        command[0] = path;
        command[1] = game;
        output.append(command[0]).append(" ").append(command[1]).append("\n");
        
        Runtime.getRuntime().exec(command);
            
        output.append(ok).append("\n");
        
        return output.toString();
    }
    
    /**
     * Runs the interpreter without a specified game file.
     */
    public String run() throws IOException {
        
        StringBuilder output = new StringBuilder();
        
        String command[] = new String[1];
        command[0] = path;
        output.append(command[0]).append("\n");
        
        Runtime.getRuntime().exec(command);
        
        output.append(ok).append("\n");
        
        return output.toString();
    }
    
    /**
     * Verify the interpreter exists
     * @throws ProgramMissingException
     */
    public void verify() throws ProgramMissingException {
        File test = new File(path);
        if (!test.exists()) {
            throw new ProgramMissingException("Interpreter does not exist");
        }
    }
    
}
