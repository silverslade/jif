package it.schillaci.jif.core;

/*
 * JifDAO.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

/**
 * Data access object
 *
 * @author Peter Piggott
 * @version 2.0
 * @since JIF 3.2
 */
public class JifDAO {

    /**
     * Creates a new instance of JifProjectDAO
     */
    private JifDAO() {
    }
        
    /*
     * Buffers a <code>File</code> into a <code>CharBuffer</code> in the 
     * standard JIF character encoding.
     *
     * @param file
     *          <code>File</code> to convert to buffer
     * @return <code>CharBuffer</code> representation of the <code>String</code>
     * @throws CharacterCodingException
     */
    public static CharBuffer buffer(File file)
            throws CharacterCodingException, IOException {
        
        return buffer(read(file));
    }
    
    /*
     * Buffers a <code>String</code> into a <code>CharBuffer</code> in the 
     * standard JIF character encoding.
     *
     * @param s
     *          <code>String</code> to convert to buffer
     * @return <code>CharBuffer</code> representation of the <code>String</code>
     * @throws CharacterCodingException
     */
    public static CharBuffer buffer(String s)
            throws CharacterCodingException {
        
        Charset charset = Charset.forName(Constants.fileFormat);
        CharsetEncoder encoder = charset.newEncoder();
        CharsetDecoder decoder = charset.newDecoder();
        ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(s));
        CharBuffer cb = decoder.decode(bbuf);
        return cb;
    }
    
    /**
     * Reads a <code>File</code> into a <code>String</code>
     *
     * @param file
     *              <code>File</code> to read string from
     * @return <codeString</code> representation of the <code>File</code>
     * @throws IOException
     */
    public static String read(File file)
            throws IOException {
        return read(new FileInputStream(file));
    }
    
    /**
     * Reads an <code>InputStream</code> into a <code>String</code>
     *
     * @param is
     *              <code>InputStream</code> to read string from
     * @return <code>String</code> representation of the <code>InputStream</code>
     * @throws IOException
     */
    public static String read(InputStream is) 
            throws IOException {
        return read(new InputStreamReader(is, Constants.fileFormat));
    }
    
    /**
     * Reads an <code>Reader</code> into a <code>String</code>
     *
     * @param in
     *              <code>Reader</code> to read string from
     * @return <code>String</code> representation of the <code>InputStream</code>
     * @throws IOException
     */
    public static String read(Reader in) 
            throws IOException {
        BufferedReader br = new BufferedReader(in);
        StringBuilder sb = new StringBuilder();
        String line = "";
        
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        
        br.close();

        return sb.toString();
    }
    
    /**
     * Saves a <code>String</code> to the specified <code>File</code> 
     *
     * @param file
     *              <code>File</code> destination for storing the text
     * @param text
     *              <code>String</code> representation of the file
     * @throws IOException
     */
    public static void save(File file, String text)
            throws IOException {
        
        save(new FileOutputStream(file), text);
    }
    
    /**
     * Saves a <code>String</code> into the specified <code>OutputStream</code> 
     *
     * @param os
     *              <code>OutputStream</code> destination for storing the text
     * @param text
     *              <code>String</code> representation of the file
     * @throws IOException
     */
    public static void save(OutputStream os, String text)
            throws IOException {
        
        Writer out = new OutputStreamWriter(os, Constants.fileFormat );
        out.write(text);
        out.flush();
        out.close();
    }
    
}