package me.lawrenceli.utils;

/**
 * @author lawrence
 * @since 2022/3/5
 */
public class StringPattern {

    private StringPattern() {
    }

    public static String replacePatternBreaking(String string) {
        return string.replaceAll("[\n\r\t]", "_");
    }
}
