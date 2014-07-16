package it.schillaci.jif.inform;

/*
 * InformParser.java
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

import java.util.Stack;

/**
 * InformParser: Used to split a string of Inform source code into a series of
 * tokens identifying classes, elements globals, objects, properties,
 * relationships and verbs.
 * 
 * @author Peter Piggott
 * @version 2.0
 * @since 3.2
 */

public class InformParser {

    private State baseState;
    private State attributeState;
    private State attributeAliasState;
    private State classState;
    private State fakeState;
    private State grammarState;
    private State grammarActionState;
    private State objectState;
    private State propertyState;
    private State verbState;
    private State state = baseState;
    
    private Callback callback;
    private InformLexer lex;
    
    private Stack stack = new Stack();
    
    private InformToken token = null;
    private InformToken.Lexeme tokenType = null;
    private String tokenContent = "";

    /**
     * Creates a new instance of InformParser
     */
    public InformParser(Callback callback, InformLexer lex) {
        this.callback = callback;
        this.lex = lex;
        initStates();
    }

    private void initStates() {
        baseState = new BaseState(this);
        attributeState = new AttributeState(this);
        attributeAliasState = new AttributeAliasState(this);
        classState = new ClassState(this);
        fakeState = new FakeState(this);
        grammarState = new GrammarState(this);
        grammarActionState = new GrammarActionState(this);
        objectState = new ObjectState(this);
        propertyState = new PropertyState(this);
        verbState = new VerbState(this);
        state = baseState;
    }

    public void parse() {

        token = lex.nextElement();

        while (token.getType() != InformToken.EOS) {
            parseElement(token);
            token = lex.nextElement();
        }
    }

    void parseElement(InformToken token) {

        if (token.getType() == InformToken.COMMENT
                || token.getType() == InformToken.WHITESPACE) {
            callback.handleToken(token);
            return;
        }

        if (token.getType() == InformToken.NUMBER
                || token.getType() == InformToken.STRING
                || token.getType() == InformToken.WORD) {
            callback.handleToken(token);
            state.parseToken();
        }
    }

    void push(InformToken token) {
        stack.push(token);
    }

    InformToken peek() {
        return (InformToken) stack.peek();
    }

    InformToken pop() {
        return (InformToken) stack.pop();
    }

    void shift(State state) {
        push(token);
        setState(state);
    }

    void setState(State state) {
        this.state = state;
    }

    private boolean isAttribute(String name) {
        return InformLibrary.isAttribute(name) || callback.isAttribute(name);
    }

    private boolean isDirective(String name) {
        return InformLibrary.isDirective(name);
    }

    private boolean isKeyword(String name) {
        return InformLibrary.isKeyword(name) || InformLibrary.isDirective(name);
    }

    private boolean isProperty(String name) {
        return InformLibrary.isProperty(name) || callback.isProperty(name);
    }

    private boolean isStatement(String name) {
        return InformLibrary.isStatement(name);
    }

    private boolean isVerb(String name) {
        return InformLibrary.isVerb(name) || callback.isVerb(name);
    }

    // --- Interfaces ----------------------------------------------------------
    /**
     * Users of the parser must implement this callback interface
     */
    public interface Callback {
        // Basic syntax items

        public void handleToken(InformToken token);

        public boolean isAttribute(String token);

        public boolean isProperty(String token);

        public boolean isVerb(String token);
    }

    private interface State {
//        void parseSymbol();

        void parseToken();
    }

    // --- Nested classes ------------------------------------------------------
    /* The parser can start at any point in the source so default syntax 
     * coloring actions are taken until a directive is found.
     */
    private class BaseState implements State {

        InformParser parser;

        public BaseState(InformParser parser) {
            this.parser = parser;
        }

        private void parseKeyword() {
            if (token.getContent().equalsIgnoreCase("attribute")) {
                setState(attributeState);
            } else if (token.getContent().equalsIgnoreCase("property")) {
                setState(propertyState);
            } else if (token.getContent().equalsIgnoreCase("extend")) {
                setState(verbState);
            } else if (token.getContent().equalsIgnoreCase("verb")) {
                setState(verbState);
            }
        }

        @Override
        public void parseToken() {
            callback.handleToken(token);
        }
    }

    private class DirectiveState implements State {

        InformParser parser;

        public DirectiveState(InformParser parser) {
            this.parser = parser;
        }

        private void parseKeyword() {
            if (token.getContent().equalsIgnoreCase("attribute")) {
                setState(attributeState);
            } else if (token.getContent().equalsIgnoreCase("class")) {
                setState(classState);
            } else if (token.getContent().equalsIgnoreCase("object")) {
                setState(objectState);
            } else if (token.getContent().equalsIgnoreCase("property")) {
                setState(propertyState);
            } else if (token.getContent().equalsIgnoreCase("extend")) {
                setState(verbState);
            } else if (token.getContent().equalsIgnoreCase("verb")) {
                setState(verbState);
            }
        }

        @Override
        public void parseToken() {
            callback.handleToken(token);
        }
    }

    // --- Attribute directive -------------------------------------------------
    private class AttributeState implements State {

        InformParser parser;

        public AttributeState(InformParser parser) {
            this.parser = parser;
        }

        @Override
        public void parseToken() {
            if (token.getType() == InformToken.SEMICOLON) {
                setState(baseState);
            }
        }
    }

    private class AttributeAliasState implements State {

        InformParser parser;

        public AttributeAliasState(InformParser parser) {
            this.parser = parser;
        }

        @Override
        public void parseToken() {
            if (token.getType() == InformToken.SEMICOLON) {
                setState(baseState);
            }
        }
    }

    // --- Class directive -----------------------------------------------------
    private class ClassState implements State {

        InformParser parser;

        public ClassState(InformParser parser) {
            this.parser = parser;
        }

        @Override
        public void parseToken() {
        }
    }

    // --- Fake_action directive -----------------------------------------------
    private class FakeState implements State {

        InformParser parser;

        public FakeState(InformParser parser) {
            this.parser = parser;
        }

        @Override
        public void parseToken() {
            if (token.getType() == InformToken.SEMICOLON) {
                setState(baseState);
            }
        }
    }

    // --- Object directive ----------------------------------------------------
    private class ObjectState implements State {

        InformParser parser;

        public ObjectState(InformParser parser) {
            this.parser = parser;
        }

        @Override
        public void parseToken() {
        }
    }

    // --- Property directive --------------------------------------------------
    private class PropertyState implements State {

        InformParser parser;

        public PropertyState(InformParser parser) {
            this.parser = parser;
        }

        @Override
        public void parseToken() {
            if (token.getType() == InformToken.SEMICOLON) {
                setState(baseState);
            }
        }
    }

    // --- Trace directive -----------------------------------------------------
    private class TraceState implements State {

        InformParser parser;

        public TraceState(InformParser parser) {
            this.parser = parser;
        }

        @Override
        public void parseToken() {
            if (token.getType() == InformToken.SEMICOLON) {
                setState(baseState);
            }
        }
    }

    // --- Extend, Verb directives ---------------------------------------------
    private class VerbState implements State {

        InformParser parser;

        public VerbState(InformParser parser) {
            this.parser = parser;
        }

        @Override
        public void parseToken() {
            if (token.getType() == InformToken.TIMES) {
                setState(grammarState);
            } else if (token.getType() == InformToken.SEMICOLON) {
                setState(baseState);
            }
        }
    }

    private class GrammarState implements State {

        InformParser parser;

        public GrammarState(InformParser parser) {
            this.parser = parser;
        }

        @Override
        public void parseToken() {
            if (token.getType() == InformToken.MINUSARROW) {
                setState(grammarActionState);
            } else if (token.getType() == InformToken.SEMICOLON) {
                setState(baseState);
            }
        }
    }

    private class GrammarActionState implements State {

        InformParser parser;

        public GrammarActionState(InformParser parser) {
            this.parser = parser;
        }

        @Override
        public void parseToken() {
            if (token.getType() == InformToken.TIMES) {
                setState(grammarState);
            } else if (token.getType() == InformToken.SEMICOLON) {
                setState(baseState);
            }
        }
    }
}