package com.github.wnebyte.console.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * This class declares utility-methods for working on strings.
 */
public final class StringUtils {

    /**
     * @param s the string.
     * @return <code>true</code> if the specified string is <code>null</code> or empty,
     * otherwise <code>false</code>.
     */
    public static boolean isNullOrEmpty(final String s) {
        return (s == null) || (s.equals(""));
    }

    public static boolean nonEmpty(final String s) {
        return (s != null) && !(s.equals(""));
    }

    public static boolean containsLineSeparator(final String s) {
        return (s != null) && (s.contains(System.lineSeparator()));
    }

    private static String encodeDecode(final String s) {
        ByteBuffer buffer = Charset.defaultCharset().encode(s);
        return Charset.defaultCharset().decode(buffer).toString();
    }

    /**
     * Removes the occurrence of any <code>lineSeparator</code> characters from the specified string.
     * @param s the string.
     * @return the specified string not containing <code>"\r\n"</code> or <code>"\n"</code>.
     */
    public static String removeLineSeparators(final String s) {
        return (s != null) ? s.replace(System.lineSeparator(), "").replace("\n", "") : "";
    }
}
