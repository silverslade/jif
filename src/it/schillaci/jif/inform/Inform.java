package it.schillaci.jif.inform;

/*
 * Inform.java
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

import it.schillaci.jif.core.Constants;
import it.schillaci.jif.core.GamePathMissingException;
import it.schillaci.jif.core.ProgramMissingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.Vector;

/**
 * Inform compiler
 * @author Peter Piggott
 * @version 1.0
 * @since Jif 3.6
 */
public class Inform {

    private String ok = java.util.ResourceBundle.getBundle("JIF").getString("OK_COMPILER2");
    private String path;
    private String game;
    
    /**
     * Creates a new instance of an Inform compiler
     * @param path
     *              The path of the Inform compiler
     * @param game
     *              The directory to use for any compiled game produced
     */
    public Inform(String path, String game) {
        this.path = path;
        this.game = game;
    }
    
    /**
     * Runs the Inform compiler with the specified compiler switches, libraries,
     * source file and destination file. Equivalent to the command line:
     * <code>Inform path switch... libraries source dest</code>  
     *
     * @param switches
     *              Set of compiler switches for compiler run
     * @param libraries
     *              Included libraries for compiler run
     * @param source
     *              Inform source file to compile
     * @param dest
     *              Game file name
     * @throws IOException
     */
    public String run(Set switches, String libraries, String source, String dest) throws IOException {
        
        StringBuffer output = new StringBuffer();
        
        Vector auxV = new Vector(6);
        auxV.add(path);
        auxV.addAll(switches);
        auxV.add(libraries);
        auxV.add(source);
        auxV.add(dest);
        
        String command[] = new String[auxV.size()];
        for (int i=0; i<auxV.size(); i++) {
            command[i] = (String)auxV.get(i);
            output.append(command[i] + " ");
            output.append(i==auxV.size()-1 ? "\n" : " ");
        }
        
        Process proc = Runtime.getRuntime().exec(
                command,
                null,
                new File(game)
                );
        
        BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(), Constants.fileFormat));
        String line = "";
        
        // in caso di errore o warning metto il cancelletto #
        while ((line = br.readLine()) != null) {
            if ( (line.indexOf("Error:") != -1) || (line.indexOf("error:") != -1)) {
                output.append(Constants.TOKENCOMMENT + line + "\n");
            } else if ( (line.indexOf("Warning:")!=-1) || (line.indexOf("warning:")!=-1)) {
                output.append(Constants.TOKENCOMMENT + line + "\n");
            } else {
                output.append(line + "\n");
            }
        }
        
        output.append("\n");
        output.append(ok + "\n");
        
        return output.toString();
    }
    
    /**
     * Verify the Inform compiler exists and output path exists
     * @throws ProgramMissingException
     * @throws GamePathMissingException
     */
    public void verify() throws ProgramMissingException, GamePathMissingException {
        File test = new File(path);
        if (!test.exists()) {
            throw new ProgramMissingException("Inform program does not exist");
        }

        test = new File(path);
        if (!test.exists()) {
            throw new GamePathMissingException("Game path does not exist");
        }
    }
}
