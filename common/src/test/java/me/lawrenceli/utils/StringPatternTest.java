package me.lawrenceli.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringPatternTest {

    @Test
    void testReplacePatternBreaking() { // NOSONAR
        assertEquals("String", StringPattern.replacePatternBreaking("String"));
        assertEquals("_", StringPattern.replacePatternBreaking("\t"));
        assertEquals("[___]", StringPattern.replacePatternBreaking("[\n\r\t]"));
        assertEquals("__", StringPattern.replacePatternBreaking("\t\t"));
        assertEquals("_[___]", StringPattern.replacePatternBreaking("\t[\n\r\t]"));
        assertEquals("__", StringPattern.replacePatternBreaking("\t_"));
        assertEquals("_String", StringPattern.replacePatternBreaking("\tString"));
        assertEquals("_42", StringPattern.replacePatternBreaking("\t42"));
        assertEquals("[___]_", StringPattern.replacePatternBreaking("[\n\r\t]\t"));
        assertEquals("[___][___]", StringPattern.replacePatternBreaking("[\n\r\t][\n\r\t]"));
        assertEquals("[___]_", StringPattern.replacePatternBreaking("[\n\r\t]_"));
        assertEquals("[___]String", StringPattern.replacePatternBreaking("[\n\r\t]String"));
        assertEquals("[___]42", StringPattern.replacePatternBreaking("[\n\r\t]42"));
        assertEquals("__", StringPattern.replacePatternBreaking("_\t"));
        assertEquals("_[___]", StringPattern.replacePatternBreaking("_[\n\r\t]"));
        assertEquals("String_", StringPattern.replacePatternBreaking("String\t"));
        assertEquals("String[___]", StringPattern.replacePatternBreaking("String[\n\r\t]"));
        assertEquals("42_", StringPattern.replacePatternBreaking("42\t"));
        assertEquals("42[___]", StringPattern.replacePatternBreaking("42[\n\r\t]"));
        assertEquals("___", StringPattern.replacePatternBreaking("\t\t\t"));
        assertEquals("__[___]", StringPattern.replacePatternBreaking("\t\t[\n\r\t]"));
        assertEquals("___", StringPattern.replacePatternBreaking("\t\t_"));
        assertEquals("__String", StringPattern.replacePatternBreaking("\t\tString"));
        assertEquals("__42", StringPattern.replacePatternBreaking("\t\t42"));
        assertEquals("_[___]_", StringPattern.replacePatternBreaking("\t[\n\r\t]\t"));
        assertEquals("_[___][___]", StringPattern.replacePatternBreaking("\t[\n\r\t][\n\r\t]"));
        assertEquals("_[___]_", StringPattern.replacePatternBreaking("\t[\n\r\t]_"));
        assertEquals("_[___]String", StringPattern.replacePatternBreaking("\t[\n\r\t]String"));
        assertEquals("_[___]42", StringPattern.replacePatternBreaking("\t[\n\r\t]42"));
        assertEquals("___", StringPattern.replacePatternBreaking("\t_\t"));
        assertEquals("__[___]", StringPattern.replacePatternBreaking("\t_[\n\r\t]"));
        assertEquals("___", StringPattern.replacePatternBreaking("\t__"));
        assertEquals("__String", StringPattern.replacePatternBreaking("\t_String"));
        assertEquals("__42", StringPattern.replacePatternBreaking("\t_42"));
        assertEquals("_String_", StringPattern.replacePatternBreaking("\tString\t"));
        assertEquals("_String[___]", StringPattern.replacePatternBreaking("\tString[\n\r\t]"));
        assertEquals("_String_", StringPattern.replacePatternBreaking("\tString_"));
        assertEquals("_StringString", StringPattern.replacePatternBreaking("\tStringString"));
        assertEquals("_String42", StringPattern.replacePatternBreaking("\tString42"));
        assertEquals("_42_", StringPattern.replacePatternBreaking("\t42\t"));
        assertEquals("_42[___]", StringPattern.replacePatternBreaking("\t42[\n\r\t]"));
        assertEquals("_42_", StringPattern.replacePatternBreaking("\t42_"));
        assertEquals("_42String", StringPattern.replacePatternBreaking("\t42String"));
        assertEquals("_4242", StringPattern.replacePatternBreaking("\t4242"));
        assertEquals("[___]__", StringPattern.replacePatternBreaking("[\n\r\t]\t\t"));
        assertEquals("[___]_[___]", StringPattern.replacePatternBreaking("[\n\r\t]\t[\n\r\t]"));
        assertEquals("[___]__", StringPattern.replacePatternBreaking("[\n\r\t]\t_"));
        assertEquals("[___]_String", StringPattern.replacePatternBreaking("[\n\r\t]\tString"));
        assertEquals("[___]_42", StringPattern.replacePatternBreaking("[\n\r\t]\t42"));
        assertEquals("[___][___]_", StringPattern.replacePatternBreaking("[\n\r\t][\n\r\t]\t"));
        assertEquals("[___][___][___]", StringPattern.replacePatternBreaking("[\n\r\t][\n\r\t][\n\r\t]"));
        assertEquals("[___][___]_", StringPattern.replacePatternBreaking("[\n\r\t][\n\r\t]_"));
        assertEquals("[___][___]String", StringPattern.replacePatternBreaking("[\n\r\t][\n\r\t]String"));
        assertEquals("[___][___]42", StringPattern.replacePatternBreaking("[\n\r\t][\n\r\t]42"));
        assertEquals("[___]__", StringPattern.replacePatternBreaking("[\n\r\t]_\t"));
        assertEquals("[___]_[___]", StringPattern.replacePatternBreaking("[\n\r\t]_[\n\r\t]"));
        assertEquals("[___]__", StringPattern.replacePatternBreaking("[\n\r\t]__"));
        assertEquals("[___]_String", StringPattern.replacePatternBreaking("[\n\r\t]_String"));
        assertEquals("[___]_42", StringPattern.replacePatternBreaking("[\n\r\t]_42"));
        assertEquals("[___]String_", StringPattern.replacePatternBreaking("[\n\r\t]String\t"));
        assertEquals("[___]String[___]", StringPattern.replacePatternBreaking("[\n\r\t]String[\n\r\t]"));
        assertEquals("[___]String_", StringPattern.replacePatternBreaking("[\n\r\t]String_"));
        assertEquals("[___]StringString", StringPattern.replacePatternBreaking("[\n\r\t]StringString"));
        assertEquals("[___]String42", StringPattern.replacePatternBreaking("[\n\r\t]String42"));
        assertEquals("[___]42_", StringPattern.replacePatternBreaking("[\n\r\t]42\t"));
        assertEquals("[___]42[___]", StringPattern.replacePatternBreaking("[\n\r\t]42[\n\r\t]"));
        assertEquals("[___]42_", StringPattern.replacePatternBreaking("[\n\r\t]42_"));
        assertEquals("[___]42String", StringPattern.replacePatternBreaking("[\n\r\t]42String"));
        assertEquals("[___]4242", StringPattern.replacePatternBreaking("[\n\r\t]4242"));
        assertEquals("___", StringPattern.replacePatternBreaking("_\t\t"));
        assertEquals("__[___]", StringPattern.replacePatternBreaking("_\t[\n\r\t]"));
        assertEquals("___", StringPattern.replacePatternBreaking("_\t_"));
        assertEquals("__String", StringPattern.replacePatternBreaking("_\tString"));
        assertEquals("__42", StringPattern.replacePatternBreaking("_\t42"));
        assertEquals("_[___]_", StringPattern.replacePatternBreaking("_[\n\r\t]\t"));
        assertEquals("_[___][___]", StringPattern.replacePatternBreaking("_[\n\r\t][\n\r\t]"));
        assertEquals("_[___]_", StringPattern.replacePatternBreaking("_[\n\r\t]_"));
        assertEquals("_[___]String", StringPattern.replacePatternBreaking("_[\n\r\t]String"));
        assertEquals("_[___]42", StringPattern.replacePatternBreaking("_[\n\r\t]42"));
        assertEquals("___", StringPattern.replacePatternBreaking("__\t"));
        assertEquals("__[___]", StringPattern.replacePatternBreaking("__[\n\r\t]"));
        assertEquals("_String_", StringPattern.replacePatternBreaking("_String\t"));
        assertEquals("_String[___]", StringPattern.replacePatternBreaking("_String[\n\r\t]"));
        assertEquals("_42_", StringPattern.replacePatternBreaking("_42\t"));
        assertEquals("_42[___]", StringPattern.replacePatternBreaking("_42[\n\r\t]"));
        assertEquals("String__", StringPattern.replacePatternBreaking("String\t\t"));
        assertEquals("String_[___]", StringPattern.replacePatternBreaking("String\t[\n\r\t]"));
        assertEquals("String__", StringPattern.replacePatternBreaking("String\t_"));
        assertEquals("String_String", StringPattern.replacePatternBreaking("String\tString"));
        assertEquals("String_42", StringPattern.replacePatternBreaking("String\t42"));
        assertEquals("String[___]_", StringPattern.replacePatternBreaking("String[\n\r\t]\t"));
        assertEquals("String[___][___]", StringPattern.replacePatternBreaking("String[\n\r\t][\n\r\t]"));
        assertEquals("String[___]_", StringPattern.replacePatternBreaking("String[\n\r\t]_"));
        assertEquals("String[___]String", StringPattern.replacePatternBreaking("String[\n\r\t]String"));
        assertEquals("String[___]42", StringPattern.replacePatternBreaking("String[\n\r\t]42"));
        assertEquals("String__", StringPattern.replacePatternBreaking("String_\t"));
        assertEquals("String_[___]", StringPattern.replacePatternBreaking("String_[\n\r\t]"));
        assertEquals("StringString_", StringPattern.replacePatternBreaking("StringString\t"));
        assertEquals("StringString[___]", StringPattern.replacePatternBreaking("StringString[\n\r\t]"));
        assertEquals("String42_", StringPattern.replacePatternBreaking("String42\t"));
        assertEquals("String42[___]", StringPattern.replacePatternBreaking("String42[\n\r\t]"));
        assertEquals("42__", StringPattern.replacePatternBreaking("42\t\t"));
        assertEquals("42_[___]", StringPattern.replacePatternBreaking("42\t[\n\r\t]"));
        assertEquals("42__", StringPattern.replacePatternBreaking("42\t_"));
        assertEquals("42_String", StringPattern.replacePatternBreaking("42\tString"));
        assertEquals("42_42", StringPattern.replacePatternBreaking("42\t42"));
        assertEquals("42[___]_", StringPattern.replacePatternBreaking("42[\n\r\t]\t"));
        assertEquals("42[___][___]", StringPattern.replacePatternBreaking("42[\n\r\t][\n\r\t]"));
        assertEquals("42[___]_", StringPattern.replacePatternBreaking("42[\n\r\t]_"));
        assertEquals("42[___]String", StringPattern.replacePatternBreaking("42[\n\r\t]String"));
        assertEquals("42[___]42", StringPattern.replacePatternBreaking("42[\n\r\t]42"));
        assertEquals("42__", StringPattern.replacePatternBreaking("42_\t"));
        assertEquals("42_[___]", StringPattern.replacePatternBreaking("42_[\n\r\t]"));
        assertEquals("42String_", StringPattern.replacePatternBreaking("42String\t"));
        assertEquals("42String[___]", StringPattern.replacePatternBreaking("42String[\n\r\t]"));
        assertEquals("4242_", StringPattern.replacePatternBreaking("4242\t"));
        assertEquals("4242[___]", StringPattern.replacePatternBreaking("4242[\n\r\t]"));
    }
}

