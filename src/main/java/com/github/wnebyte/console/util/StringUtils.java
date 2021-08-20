package com.github.wnebyte.console.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * This class declares utility-methods for operating on Strings.
 */
public final class StringUtils {

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
}
