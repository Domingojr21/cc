package com.banreservas.integration.util;

/**
 * Utilidad para normalizar valores booleanos de forma case-insensitive.
 * 
 * @author Domingo Ruiz - c-djruiz@banreservas.com
 * @since 10/07/2025
 * @version 1.0.0
 */
public class BooleanValueUtil {

    /**
     * Normaliza un valor string a TRUE o FALSE de forma case-insensitive.
     * 
     * @param rawValue valor original
     * @return "TRUE" o "FALSE" normalizado, null si es inválido
     */
    public static String normalize(String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return null;
        }

        String trimmedValue = rawValue.trim();
        
        if ("true".equalsIgnoreCase(trimmedValue)) {
            return Constants.BOOLEAN_TRUE;
        }
        
        if ("false".equalsIgnoreCase(trimmedValue)) {
            return Constants.BOOLEAN_FALSE;
        }

        return null; // Valor inválido
    }

    /**
     * Valida si un valor string es un booleano válido (case-insensitive).
     * 
     * @param rawValue valor a validar
     * @return true si es válido, false si no
     */
    public static boolean isValid(String rawValue) {
        return normalize(rawValue) != null;
    }
}