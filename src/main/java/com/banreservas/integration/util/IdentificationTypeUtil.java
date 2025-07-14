package com.banreservas.integration.util;

import java.util.Arrays;
import java.util.List;

public class IdentificationTypeUtil {

    private static final List<String> VALID_VALUES = Arrays.asList("RNC", "Cedula");

    public static String normalize(String rawValue) {
        if (rawValue == null || rawValue.isEmpty()) {
            return null;
        }

        for (String valid : VALID_VALUES) {
            if (valid.equalsIgnoreCase(rawValue)) {
                return valid;
            }
        }

        return null;
    }

    public static boolean isValid(String rawValue) {
        return normalize(rawValue) != null;
    }
}