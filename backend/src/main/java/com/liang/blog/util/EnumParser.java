package com.liang.blog.util;

public final class EnumParser {

    private EnumParser() {}

    public static <E extends Enum<E>> E parseOrDefault(Class<E> enumClass, String value, E defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }
}
