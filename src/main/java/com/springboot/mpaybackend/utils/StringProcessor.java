package com.springboot.mpaybackend.utils;

public class StringProcessor {
    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?"); // Matches numeric pattern
    }

    public static boolean isAlphaNumeric(String str) {
        return str.matches("^[a-zA-Z0-9 ]*$"); // Matches alphanumeric pattern
    }

    public static boolean isAlphaNumericWithSpecialChars(String substring) {
        return substring.matches( "^[a-zA-Z0-9!@#$%^&*()-=_+`~[\\\\]{}|;:'\\\",<.>/? ]+$" );
    }
}
