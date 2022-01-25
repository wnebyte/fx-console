package com.github.wnebyte.console.util;

import java.util.List;
import java.util.ArrayList;

/**
 * This class declares utility-methods for working with instances of {@link String}.
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

    /**
     * Removes the occurrence of any <code>lineSeparator</code> characters from the specified string.
     * @param s the string.
     * @return the specified string not containing <code>"\r\n"</code> or <code>"\n"</code>.
     */
    public static String removeLineSeparators(final String s) {
        return (s != null) ?
                s.replace(System.lineSeparator(), "").replace("\n", "")
                : "";
    }

    /**
     * Replaces <code>"\r\n"</code> with <code>"\n"</code>.
     * @param s the string.
     * @return the result.
     */
    static String normalizeString(final String s) {
        return (s != null) ? s.replace("\r\n", "\n") : "";
    }

    public static List<String> split(String s) {
        List<String> elements = new ArrayList<>();
        if (s == null) { return elements; }
        s = normalizeString(s);
        char[] arr = s.toCharArray();
        int start = 0;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == '\n') {
                String substring = s.substring(start, i);
                if (substring.length() != 0) {
                    elements.add(substring);
                }
                elements.add("\n");
                start = i + 1;
            }
        }
        String substring = s.substring(start);
        if (substring.length() != 0) {
            elements.add(substring);
        }
        return elements;
    }

    public static String replaceSequence(final String s, final String replacement, final char c) {
        char[] arr = s.toCharArray();
        boolean match = false;

        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == c) {
                match = true;
            } else {
                if (match) {
                    return s.substring(0, i + 1).concat(replacement);
                }
            }
        }
        return replacement;
    }
}
