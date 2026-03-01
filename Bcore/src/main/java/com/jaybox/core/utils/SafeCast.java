package com.jaybox.core.utils;

/**
 * Safe type casting utilities for Android 16 compatibility.
 * On Android 16, Binder IPC may serialize numbers as Long instead of Integer.
 */
public class SafeCast {
    public static int toInt(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        throw new IllegalArgumentException("Expected numeric type, got: " + 
            (obj == null ? "null" : obj.getClass().getName()));
    }
    
    public static int toInt(Object obj, int defaultValue) {
        if (obj == null) return defaultValue;
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        return defaultValue;
    }
}
