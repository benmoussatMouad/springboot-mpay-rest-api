package com.springboot.mpaybackend.utils;

import java.util.Base64;

public class Base64Checker {
    public static boolean isBase64(String input) {
        try {
            Base64.getDecoder().decode(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
