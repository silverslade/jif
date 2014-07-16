package it.schillaci.jif.core;

/*
 * LRUCache.java
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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRUCache: A least recently used cache. When an entry is added and the cache 
 * is already full (as defined by maximum entries), the eldest entry is removed
 * to make space for the new entry.
 *
 * @author Peter Piggott
 * @version 1.0
 * @since JIF 3.2
 */
public class LRUCache extends LinkedHashMap {
    
    private int maxEntries;
    
    /**
     * Creates a new instance of a least recently used cache
     *
     * @param maxEntries
     *          the maximum number of entries the cache can contain
     */
    public LRUCache(int maxEntries) {
        super(maxEntries + 1);
        this.maxEntries = maxEntries;
    }
    
    @Override
    public boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxEntries;
    }

}