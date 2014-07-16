package it.schillaci.jif.inform;

/*
 * InformLibrary.java
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
 * WeB   : http://www.slade.altervista.org/JIF/
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

import java.util.HashMap;
import java.util.Map;

/**
 * Constants used in the <code>InformDocument</code>. These are basically
 * Inform language keyword definitions and Inform library names.
 * 
 * @author  Peter Piggott
 * @version 1.0
 * @since   JIF 3.6
 */
public class InformLibrary {

    /**
     * Type safe enumeration for Inform language keywords and Inform Library
     * predefined keywords.
     * 
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
            return "Inform.Keyword[Name: " + name + "]";
        }

        // Inform keyword name
        private String name;

        // --- Inform Keyords -----------------------------------------------
        
        public static final Keyword ALC = new Keyword("a");

        public static final Keyword AUC = new Keyword("A");

        public static final Keyword Abbreviate = new Keyword("abbreviate");

        public static final Keyword Absent = new Keyword("absent");

        public static final Keyword AddToScope = new Keyword("add_to_scope");

        public static final Keyword Additive = new Keyword("additive");

        public static final Keyword Address = new Keyword("address");

        public static final Keyword After = new Keyword("after");

        public static final Keyword Alias = new Keyword("alias");

        public static final Keyword An = new Keyword("an");

        public static final Keyword Animate = new Keyword("animate");

        public static final Keyword Answer = new Keyword("Answer");

        public static final Keyword Array = new Keyword("array");

        public static final Keyword Article = new Keyword("article");

        public static final Keyword Articles = new Keyword("articles");

        public static final Keyword Ask = new Keyword("Ask");

        public static final Keyword AskFor = new Keyword("AskFor");

        public static final Keyword AskTo = new Keyword("AskTo");

        public static final Keyword Assembly = new Keyword("assembly");

        public static final Keyword Attack = new Keyword("Attack");

        public static final Keyword Attribute = new Keyword("attribute");

        public static final Keyword Before = new Keyword("before");

        public static final Keyword Blow = new Keyword("Blow");

        public static final Keyword Bold = new Keyword("bold");

        public static final Keyword Box = new Keyword("box");

        public static final Keyword Break = new Keyword("break");

        public static final Keyword Buffer = new Keyword("buffer");

        public static final Keyword Burn = new Keyword("Burn");

        public static final Keyword Buy = new Keyword("Buy");

        public static final Keyword CantGo = new Keyword("cant_go");

        public static final Keyword Capacity = new Keyword("capacity");

        public static final Keyword Char = new Keyword("char");

        public static final Keyword Class = new Keyword("class");

        public static final Keyword Climb = new Keyword("Climb");

        public static final Keyword Close = new Keyword("Close");

        public static final Keyword Clothing = new Keyword("clothing");

        public static final Keyword CommandsOff = new Keyword("CommandsOff");

        public static final Keyword CommandsOn = new Keyword("CommandsOn");

        public static final Keyword CommandsRead = new Keyword("CommandsRead");

        public static final Keyword Concealed = new Keyword("concealed");

        public static final Keyword Constant = new Keyword("constant");

        public static final Keyword Consult = new Keyword("Consult");

        public static final Keyword Container = new Keyword("container");

        public static final Keyword Continue = new Keyword("continue");

        public static final Keyword Creature = new Keyword("creature");

        public static final Keyword Cut = new Keyword("Cut");

        public static final Keyword DTo = new Keyword("d_to");

        public static final Keyword Daemon = new Keyword("daemon");

        public static final Keyword Data = new Keyword("data");

        public static final Keyword DefaultLC = new Keyword("default");

        public static final Keyword DefaultUC = new Keyword("Default");

        public static final Keyword Describe = new Keyword("describe");

        public static final Keyword Description = new Keyword("description");

        public static final Keyword Dictionary = new Keyword("dictionary");

        public static final Keyword Dig = new Keyword("Dig");

        public static final Keyword Disrobe = new Keyword("Disrobe");

        public static final Keyword Do = new Keyword("do");

        public static final Keyword Door = new Keyword("door");

        public static final Keyword DoorDir = new Keyword("door_dir");

        public static final Keyword DoorTo = new Keyword("door_to");

        public static final Keyword Drink = new Keyword("Drink");

        public static final Keyword Drop = new Keyword("Drop");

        public static final Keyword ETo = new Keyword("e_to");

        public static final Keyword EachTurn = new Keyword("each_turn");

        public static final Keyword Eat = new Keyword("Eat");

        public static final Keyword Edible = new Keyword("edible");

        public static final Keyword Else = new Keyword("else");

        public static final Keyword Empty = new Keyword("Empty");

        public static final Keyword EmptyT = new Keyword("EmptyT");

        public static final Keyword End = new Keyword("end");

        public static final Keyword Endif = new Keyword("endif");

        public static final Keyword Enter = new Keyword("Enter");

        public static final Keyword Enterable = new Keyword("enterable");

        public static final Keyword Error = new Keyword("error");

        public static final Keyword Examine = new Keyword("Examine");

        public static final Keyword Exit = new Keyword("Exit");

        public static final Keyword Expressions = new Keyword("expressions");

        public static final Keyword Extend = new Keyword("extend");

        public static final Keyword FakeAction = new Keyword("fake_action");

        public static final Keyword False = new Keyword("false");

        public static final Keyword FatalError = new Keyword("fatalerror");

        public static final Keyword Female = new Keyword("female");

        public static final Keyword Fill = new Keyword("Fill");

        public static final Keyword First = new Keyword("first");

        public static final Keyword Fixed = new Keyword("fixed");

        public static final Keyword Font = new Keyword("font");

        public static final Keyword For = new Keyword("for");

        public static final Keyword FoundIn = new Keyword("found_in");

        public static final Keyword From = new Keyword("from");

        public static final Keyword FullScore = new Keyword("FullScore");

        public static final Keyword General = new Keyword("general");

        public static final Keyword GetOff = new Keyword("GetOff");

        public static final Keyword GiveLC = new Keyword("give");

        public static final Keyword GiveUC = new Keyword("Give");

        public static final Keyword Global = new Keyword("global");

        public static final Keyword Grammar = new Keyword("grammar");

        public static final Keyword Go = new Keyword("Go");

        public static final Keyword GoIn = new Keyword("GoIn");

        public static final Keyword Has = new Keyword("has");

        public static final Keyword Hasnt = new Keyword("hasnt");

        public static final Keyword Held = new Keyword("held");

        public static final Keyword If = new Keyword("if");

        public static final Keyword Ifdef = new Keyword("ifdef");

        public static final Keyword Iffalse = new Keyword("iffalse");

        public static final Keyword Ifndef = new Keyword("ifndef");

        public static final Keyword Ifnot = new Keyword("ifnot");

        public static final Keyword Iftrue = new Keyword("iftrue");

        public static final Keyword Ifv3 = new Keyword("ifv3");

        public static final Keyword Ifv5 = new Keyword("ifv5");

        public static final Keyword Import = new Keyword("import");

        public static final Keyword In = new Keyword("in");

        public static final Keyword InTo = new Keyword("in_to");

        public static final Keyword Include = new Keyword("include");

        public static final Keyword Initial = new Keyword("initial");

        public static final Keyword InitStr = new Keyword("initstr");

        public static final Keyword Insert = new Keyword("Insert");

        public static final Keyword InsideDescription = new Keyword("inside_description");

        public static final Keyword Inv = new Keyword("Inv");

        public static final Keyword InvTall = new Keyword("InvTall");

        public static final Keyword InvWide = new Keyword("InvWide");

        public static final Keyword Invent = new Keyword("invent");

        public static final Keyword Inversion = new Keyword("inversion");

        public static final Keyword JumpLC = new Keyword("jump");

        public static final Keyword JumpUC = new Keyword("Jump");

        public static final Keyword JumpOver = new Keyword("JumpOver");

        public static final Keyword Kiss = new Keyword("Kiss");

        public static final Keyword Last = new Keyword("last");

        public static final Keyword LetGo = new Keyword("LetGo");

        public static final Keyword Life = new Keyword("life");

        public static final Keyword Light = new Keyword("light");

        public static final Keyword Lines = new Keyword("lines");

        public static final Keyword Link = new Keyword("link");

        public static final Keyword Linker = new Keyword("linker");

        public static final Keyword Listen = new Keyword("Listen");

        public static final Keyword ListMiscellany = new Keyword("ListMiscellany");

        public static final Keyword ListTogether = new Keyword("list_together");

        public static final Keyword LMode1 = new Keyword("LMode1");

        public static final Keyword LMode2 = new Keyword("LMode2");

        public static final Keyword LMode3 = new Keyword("LMode3");

        public static final Keyword Lock = new Keyword("Lock");

        public static final Keyword Lockable = new Keyword("lockable");

        public static final Keyword Locked = new Keyword("locked");

        public static final Keyword Long = new Keyword("long");

        public static final Keyword Look = new Keyword("Look");

        public static final Keyword LookUnder = new Keyword("LookUnder");

        public static final Keyword Lowstring = new Keyword("lowstring");

        public static final Keyword Male = new Keyword("male");

        public static final Keyword Message = new Keyword("message");

        public static final Keyword Meta = new Keyword("meta");

        public static final Keyword Mild = new Keyword("Mild");

        public static final Keyword Miscellany = new Keyword("Miscellany");

        public static final Keyword Move = new Keyword("move");

        public static final Keyword Moved = new Keyword("moved");

        public static final Keyword Multi = new Keyword("multi");

        public static final Keyword MultiExcept = new Keyword("multiexcept");

        public static final Keyword MultiHeld = new Keyword("multiheld");

        public static final Keyword MultiInside = new Keyword("multiinside");

        public static final Keyword NTo = new Keyword("n_to");

        public static final Keyword Name = new Keyword("name");

        public static final Keyword NETo = new Keyword("ne_to");

        public static final Keyword Near = new Keyword("near");

        public static final Keyword Nearby = new Keyword("nearby");

        public static final Keyword Neuter = new Keyword("neuter");

        public static final Keyword Newline = new Keyword("new_line");

        public static final Keyword No = new Keyword("No");

        public static final Keyword NotifyOff = new Keyword("NotifyOff");

        public static final Keyword NotifyOn = new Keyword("NotifyOn");

        public static final Keyword NotIn = new Keyword("notin");

        public static final Keyword NotUnderstood = new Keyword("NotUnderstood");

        public static final Keyword Noun = new Keyword("noun");

        public static final Keyword Number = new Keyword("number");

        public static final Keyword NWTo = new Keyword("nw_to");

        public static final Keyword Object = new Keyword("object");

        public static final Keyword Objectloop = new Keyword("objectloop");

        public static final Keyword Objects = new Keyword("Objects");

        public static final Keyword OfClass = new Keyword("ofclass");

        public static final Keyword Off = new Keyword("off");

        public static final Keyword On = new Keyword("on");

        public static final Keyword Only = new Keyword("only");

        public static final Keyword OpenLC = new Keyword("open");

        public static final Keyword OpenUC = new Keyword("Open");

        public static final Keyword Openable = new Keyword("openable");

        public static final Keyword Or = new Keyword("or");

        public static final Keyword Order = new Keyword("Order");

        public static final Keyword Orders = new Keyword("orders");

        public static final Keyword OutTo = new Keyword("out_to");

        public static final Keyword ParseName = new Keyword("parse_name");

        public static final Keyword Places = new Keyword("Places");

        public static final Keyword Plural = new Keyword("plural");

        public static final Keyword PluralFound = new Keyword("PluralFound");

        public static final Keyword Pluralname = new Keyword("pluralname");

        public static final Keyword Pray = new Keyword("Pray");

        public static final Keyword Print = new Keyword("print");

        public static final Keyword Printret = new Keyword("print_ret");

        public static final Keyword Private = new Keyword("private");

        public static final Keyword Prompt = new Keyword("Prompt");

        public static final Keyword Pronouns = new Keyword("Pronouns");

        public static final Keyword Proper = new Keyword("proper");

        public static final Keyword Property = new Keyword("property");

        public static final Keyword Provides = new Keyword("provides");

        public static final Keyword Pull = new Keyword("Pull");

        public static final Keyword Push = new Keyword("Push");

        public static final Keyword PushDir = new Keyword("PushDir");

        public static final Keyword PutOn = new Keyword("PutOn");

        public static final Keyword QuitLC = new Keyword("quit");

        public static final Keyword QuitUC = new Keyword("Quit");

        public static final Keyword ReactAfter = new Keyword("react_after");

        public static final Keyword ReactBefore = new Keyword("react_before");

        public static final Keyword Read = new Keyword("read");

        public static final Keyword Receive = new Keyword("Receive");

        public static final Keyword Release = new Keyword("release");

        public static final Keyword RemoveLC = new Keyword("remove");

        public static final Keyword RemoveUC = new Keyword("Remove");

        public static final Keyword Replace = new Keyword("replace");

        public static final Keyword Restart = new Keyword("Restart");

        public static final Keyword Restore = new Keyword("Restore");

        public static final Keyword Return = new Keyword("return");

        public static final Keyword Reverse = new Keyword("reverse");

        public static final Keyword Rfalse = new Keyword("rfalse");

        public static final Keyword Roman = new Keyword("roman");

        public static final Keyword Rtrue = new Keyword("rtrue");

        public static final Keyword Rub = new Keyword("Rub");

        public static final Keyword STo = new Keyword("s_to");

        public static final Keyword SaveLC = new Keyword("save");

        public static final Keyword SaveUC = new Keyword("Save");

        public static final Keyword Scenery = new Keyword("scenery");

        public static final Keyword Scope = new Keyword("scope");

        public static final Keyword ScoreLC = new Keyword("score");

        public static final Keyword ScoreUC = new Keyword("Score");

        public static final Keyword Scored = new Keyword("scored");

        public static final Keyword ScriptOff = new Keyword("ScriptOff");

        public static final Keyword ScriptOn = new Keyword("ScriptOn");

        public static final Keyword SETo = new Keyword("se_to");

        public static final Keyword Search = new Keyword("Search");

        public static final Keyword Self = new Keyword("self");

        public static final Keyword Serial = new Keyword("serial");

        public static final Keyword Set = new Keyword("Set");

        public static final Keyword SetTo = new Keyword("SetTo");

        public static final Keyword ShortName = new Keyword("short_name");

        public static final Keyword ShortNameIndef = new Keyword("short_name_indef");

        public static final Keyword Show = new Keyword("Show");

        public static final Keyword Sing = new Keyword("Sing");

        public static final Keyword Sleep = new Keyword("Sleep");

        public static final Keyword Smell = new Keyword("Smell");

        public static final Keyword Sorry = new Keyword("Sorry");

        public static final Keyword Spaces = new Keyword("spaces");

        public static final Keyword Special = new Keyword("special");

        public static final Keyword Squeeze = new Keyword("Squeeze");

        public static final Keyword Static = new Keyword("static");

        public static final Keyword Statusline = new Keyword("statusline");

        public static final Keyword String = new Keyword("string");

        public static final Keyword Strong = new Keyword("Strong");

        public static final Keyword Stub = new Keyword("stub");

        public static final Keyword Style = new Keyword("style");

        public static final Keyword Supporter = new Keyword("supporter");

        public static final Keyword SWTo = new Keyword("sw_to");

        public static final Keyword Swim = new Keyword("Swim");

        public static final Keyword Swing = new Keyword("Swing");

        public static final Keyword Switch = new Keyword("switch");

        public static final Keyword Switchable = new Keyword("switchable");

        public static final Keyword Switches = new Keyword("switches");

        public static final Keyword SwitchOff = new Keyword("SwitchOff");

        public static final Keyword Switchoff = new Keyword("Switchoff");

        public static final Keyword SwitchOn = new Keyword("SwitchOn");

        public static final Keyword Switchon = new Keyword("Switchon");

        public static final Keyword Symbols = new Keyword("symbols");

        public static final Keyword Systemfile = new Keyword("system_file");

        public static final Keyword Table = new Keyword("table");

        public static final Keyword Take = new Keyword("Take");

        public static final Keyword Talkable = new Keyword("talkable");

        public static final Keyword Taste = new Keyword("Taste");

        public static final Keyword Tell = new Keyword("Tell");

        public static final Keyword Terminating = new Keyword("terminating");

        public static final Keyword TheLC = new Keyword("the");

        public static final Keyword TheUC = new Keyword("The");

        public static final Keyword TheSame = new Keyword("TheSame");

        public static final Keyword Think = new Keyword("Think");

        public static final Keyword ThrowAt = new Keyword("ThrowAt");

        public static final Keyword ThrownAt = new Keyword("ThrownAt");

        public static final Keyword Tie = new Keyword("Tie");

        public static final Keyword Time = new Keyword("time");

        public static final Keyword TimeLeft = new Keyword("time_left");

        public static final Keyword TimeOut = new Keyword("time_out");

        public static final Keyword To = new Keyword("to");

        public static final Keyword Tokens = new Keyword("tokens");

        public static final Keyword Topic = new Keyword("topic");

        public static final Keyword Touch = new Keyword("Touch");

        public static final Keyword Trace = new Keyword("trace");

        public static final Keyword Transfer = new Keyword("Transfer");

        public static final Keyword Transparent = new Keyword("transparent");

        public static final Keyword True = new Keyword("true");

        public static final Keyword Turn = new Keyword("Turn");

        public static final Keyword UTo = new Keyword("u_to");

        public static final Keyword Underline = new Keyword("underline");

        public static final Keyword Unlock = new Keyword("Unlock");

        public static final Keyword Until = new Keyword("until");

        public static final Keyword VagueGo = new Keyword("VagueGo");

        public static final Keyword Verb = new Keyword("verb");

        public static final Keyword Verbs = new Keyword("verbs");

        public static final Keyword Verify = new Keyword("Verify");

        public static final Keyword VersionLC = new Keyword("version");

        public static final Keyword VersionUC = new Keyword("Version");

        public static final Keyword Visited = new Keyword("visited");

        public static final Keyword WTo = new Keyword("w_to");

        public static final Keyword Wait = new Keyword("Wait");

        public static final Keyword Wake = new Keyword("Wake");

        public static final Keyword WakeOther = new Keyword("WakeOther");

        public static final Keyword Warning = new Keyword("warning");

        public static final Keyword Wave = new Keyword("Wave");

        public static final Keyword WaveHands = new Keyword("WaveHands");

        public static final Keyword Wear = new Keyword("Wear");

        public static final Keyword WhenClosed = new Keyword("when_closed");

        public static final Keyword WhenOff = new Keyword("when_off");

        public static final Keyword WhenOn = new Keyword("when_on");

        public static final Keyword WhenOpen = new Keyword("when_open");

        public static final Keyword While = new Keyword("while");

        public static final Keyword With = new Keyword("with");

        public static final Keyword WithKey = new Keyword("with_key");

        public static final Keyword Workflag = new Keyword("workflag");

        public static final Keyword Worn = new Keyword("worn");

        public static final Keyword Yes = new Keyword("Yes");

        public static final Keyword Zcharacter = new Keyword("zcharacter");

        // Inform library defined attributes (case sensitive)
        static final Keyword[] attributes = { Absent, Animate, Clothing,
                Concealed, Container, Door, Edible, Enterable, Female, General,
                Light, Lockable, Locked, Male, Moved, Neuter, On, OpenLC,
                Openable, Pluralname, Proper, Scenery, Scored, Static,
                Supporter, Switchable, Talkable, Transparent, Visited,
                Workflag, Worn };

        // Inform language conditions (case sensitive)
        static final Keyword[] conditions = { Has, Hasnt, In, NotIn, OfClass,
                Or, Provides };

        // Inform language directives (not case sensitive)
        static final Keyword[] directives = { Abbreviate, Array, Attribute,
                Class, Constant, Continue, DefaultLC, Dictionary, End, Endif,
                Extend, FakeAction, Global, Ifdef, Iffalse, Ifndef, Ifnot,
                Iftrue, Ifv3, Ifv5, Import, Include, Link, Lowstring, Message,
                Nearby, Object, Property, Release, Replace, Serial, Statusline,
                Stub, Style, Switches, Systemfile, Trace, Verb, Zcharacter };

        // Inform language keywords (case sensitive)
        static final Keyword[] keywords = { ALC, AUC, Abbreviate, Additive,
                Address, Alias, An, Array, Assembly, Attribute, Bold, Box,
                Break, Buffer, Char, Class, Constant, Continue, Creature, Data,
                DefaultLC, Dictionary, Do, Else, End, Endif, Error,
                Expressions, Extend, FakeAction, False, FatalError, First,
                Fixed, Font, For, From, GiveLC, Global, Has, Hasnt, Held, If,
                Ifdef, Iffalse, Ifndef, Ifnot, Iftrue, Ifv3, Ifv5, Import, In,
                Include, Initial, InitStr, Inversion, JumpLC, Last, Lines, Link,
                Linker, Long, Lowstring, Message, Meta, Move, Multi,
                MultiExcept, MultiHeld, MultiInside, Name, Near, Nearby,
                Newline, NotIn, Noun, Number, Object, Objectloop, Objects,
                OfClass, Off, On, Only, Or, Print, Printret, Private, Property,
                Provides, QuitLC, Read, Release, RemoveLC, Replace, Restore,
                Return, Reverse, Rfalse, Roman, Rtrue, SaveLC, Scope, Self,
                Serial, Spaces, Special, Statusline, String, Stub, Style,
                Switch, Switches, Symbols, Systemfile, Table, Terminating,
                TheLC, TheUC, Time, To, Tokens, Topic, Trace, True, Underline,
                Until, Verb, Verbs, VersionLC, Warning, While, With,
                Zcharacter };

        // Inform language miscellaneous keywords (case sensitive)
        static final Keyword[] miscellaneous = { ALC, AUC, Address, An, Bold,
                Char, Fixed, From, Name, Near, Number, Object, Off, On,
                Property, Reverse, Roman, String, TheLC, TheUC, To, Underline };

        // Inform library defined properties (case sensitive)
        static final Keyword[] properties = { AddToScope, After, Article,
                Articles, Before, CantGo, Capacity, DTo, Daemon, Describe,
                Description, DoorDir, DoorTo, ETo, EachTurn, FoundIn, Grammar,
                InTo, Initial, InsideDescription, Invent, Life, ListTogether,
                NTo, Name, NETo, Number, NWTo, Orders, OutTo, ParseName,
                Plural, ReactAfter, ReactBefore, STo, SETo, ShortName,
                ShortNameIndef, SWTo, TimeLeft, TimeOut, UTo, WTo, WhenClosed,
                WhenOff, WhenOn, WhenOpen, WithKey };

        // Inform language statements (case sensitive)
        static final Keyword[] statements = { Box, Break, Continue, Do, Else,
                Font, For, GiveLC, If, Inversion, JumpLC, Move, Newline,
                Objectloop, Print, Printret, QuitLC, Read, RemoveLC, Restore,
                Return, Rfalse, Rtrue, SaveLC, Spaces, String, Style, Switch,
                Until, While };

        // Inform library defined verbs (case sensitive)
        static final Keyword[] verbs = { Answer, Ask, AskFor, AskTo, Attack,
                Blow, Burn, Buy, Climb, Close, CommandsOff, CommandsOn,
                CommandsRead, Consult, Cut, DefaultUC, Dig, Disrobe, Drink, 
                Drop, Eat, Empty, EmptyT, Enter, Examine, Exit, Fill, FullScore,
                GetOff, GiveUC, Go, GoIn, Insert, Inv, InvTall, InvWide, JumpUC,
                JumpOver, Kiss, LetGo, Listen, ListMiscellany, LMode1, LMode2, 
                LMode3, Lock, Look, LookUnder, Mild, Miscellany, No, NotifyOff,
                NotifyOn, NotUnderstood, Objects, OpenUC, Order, Places, 
                PluralFound, Pray, Prompt, Pronouns, Pull, Push, PushDir, PutOn,
                QuitUC, Receive, RemoveUC, Restart, Restore, Rub, SaveUC,
                ScoreUC, ScriptOff, ScriptOn, Search, Set, SetTo, Show, Sing,
                Sleep, Smell, Sorry, Squeeze, Strong, Swim, Swing, Switchoff,
                SwitchOff, Switchon, SwitchOn, Take, Taste, Tell, TheSame,
                Think, ThrowAt, ThrownAt, Tie, Touch, Transfer, Turn, Unlock, 
                VagueGo, Verify, VersionUC, Wait, Wake, WakeOther, Wave,
                WaveHands, Wear, Yes};

        static {
            // Force Inform.Keyword's static initialise to be loaded.
            boolean temp = isKeyword("abbreviate");
        }
    }

    // Map to hold Inform library attributes
    private static final Map attributes = new HashMap();
    // Map to hold Inform language condition keywords
    private static final Map conditions = new HashMap();
    // Map to hold Inform language directive keywords
    private static final Map directives = new HashMap();
    // Map to hold Inform language keyords
    private static final Map keywords = new HashMap();
    // Map to hold Inform library miscellaneous keywords
    private static final Map miscellaneous = new HashMap();
    // Map to hold Inform library propertes
    private static final Map properties = new HashMap();
    // Map to hold Inform language statement keywords
    private static final Map statements = new HashMap();
    // Map to hold Inform library verbs
    private static final Map verbs = new HashMap();

    // Initialisation
    static {
        // Inform library attributes
        for (int i = 0; i < Keyword.attributes.length; i++) {
            attributes.put(Keyword.attributes[i].getName(),
                    Keyword.attributes[i]);
        }
        // Inform language conditions
        for (int i = 0; i < Keyword.conditions.length; i++) {
            conditions.put(Keyword.conditions[i].getName(),
                    Keyword.conditions[i]);
        }
        // Inform language directives
        for (int i = 0; i < Keyword.directives.length; i++) {
            directives.put(Keyword.directives[i].getName(),
                    Keyword.directives[i]);
        }
        // Inform language keywords
        for (int i = 0; i < Keyword.keywords.length; i++) {
            keywords.put(Keyword.keywords[i].getName(), Keyword.keywords[i]);
        }
        // Inform library miscellaneous keywords
        for (int i = 0; i < Keyword.miscellaneous.length; i++) {
            miscellaneous.put(Keyword.miscellaneous[i].getName(),
                    Keyword.miscellaneous[i]);
        }
        // Inform library properties
        for (int i = 0; i < Keyword.properties.length; i++) {
            properties.put(Keyword.properties[i].getName(),
                    Keyword.properties[i]);
        }
        // Inform language statements
        for (int i = 0; i < Keyword.statements.length; i++) {
            statements.put(Keyword.statements[i].getName(),
                    Keyword.statements[i]);
        }
        // Inform library verbs
        for (int i = 0; i < Keyword.verbs.length; i++) {
            verbs.put(Keyword.verbs[i].getName(), Keyword.verbs[i]);
        }
    }

    /**
     * Returns <tt>true</tt> if the specified symbol is an Inform library attribute
     * 
     * @param symbol
     *            Symbol to be tested as an Inform library attribute
     * @return <tt>true</tt> if this is an Inform library attribute
     */
    public static boolean isAttribute(String symbol) {
        return attributes.containsKey(symbol);
    }

    /**
     * Returns <tt>true</tt> if the specified symbol is an Inform condition keyword
     * 
     * @param symbol
     *            Symbol to be tested as an Inform condition keyword
     * @return <tt>true</tt> if this is an Inform condition keyword
     */
    public static boolean isCondition(String symbol) {
        return conditions.containsKey(symbol);
    }

    /**
     * Returns <tt>true</tt> if the specified symbol is an Inform directive keyword
     * 
     * @param symbol
     *            Symbol to be tested as an Inform directive keyword
     * @return <tt>true</tt> if this is an Inform directive keyword
     */
    public static boolean isDirective(String symbol) {
        return directives.containsKey(symbol.toLowerCase());
    }

    /**
     * Returns <tt>true</tt> if the specified symbol is an Inform keyword
     * 
     * @param symbol
     *            Symbol to be tested as an Inform keyword
     * @return <tt>true</tt> if this is an Inform keyword
     */
    public static boolean isKeyword(String symbol) {
        return keywords.containsKey(symbol);
    }

    /**
     * Returns <tt>true</tt> if the specified symbol is an Inform library property
     * 
     * @param symbol
     *            Symbol to be tested as an Inform library property
     * @return <tt>true</tt> if this is an Inform library property
     */
    public static boolean isProperty(String symbol) {
        return properties.containsKey(symbol);
    }

    /**
     * Returns <tt>true</tt> if the specified symbol is an Inform statement keyword
     * 
     * @param symbol
     *            Symbol to be tested as an Inform statement keyword
     * @return <tt>true</tt> if this is an Inform statement keyword
     */
    public static boolean isStatement(String symbol) {
        return statements.containsKey(symbol);
    }

    /**
     * Returns <tt>true</tt> if the specified symbol is an Inform library verb
     * 
     * @param symbol
     *            Symbol to be tested as an Inform library verb
     * @return <tt>true</tt> if this is an Inform library verb
     */
    public static boolean isVerb(String symbol) {
        return verbs.containsKey(symbol);
    }

}
