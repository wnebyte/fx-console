package com.github.wnebyte.console.util;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * This class declares utility-methods for working with instances of {@link String}.
 */
public final class StringUtils {

    public static final List<String> LINE_SEPARATORS = Arrays.asList(
            "\r\n",
            "\n"
    );

    /**
     * @param s the string.
     * @return <code>true</code> if the specified string is <code>null</code> or empty,
     * otherwise <code>false</code>.
     */
    public static boolean isNullOrEmpty(final String s) {
        return (s == null) || (s.equals(""));
    }

    public static boolean isNotEmpty(final String s) {
        return (s != null) && !(s.equals(""));
    }

    public static boolean containsLineSeparator(final String s) {
        if (s == null) {
            return false;
        }
        for (String ls : LINE_SEPARATORS) {
            if (s.contains(ls)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes any occurrences of <code>"\r\n"</code> or <code>"\n"</code> from the specified <code>String</code>.
     * @param s the string.
     * @return the specified string not containing <code>"\r\n"</code> or <code>"\n"</code>.
     */
    public static String removeLineSeparators(String s) {
        if (s == null) {
            return "";
        }
        for (String ls : LINE_SEPARATORS) {
            s = s.replace(ls, "");
        }
        return s;
    }

    /**
     * Replaces <code>"\r\n"</code> with <code>"\n"</code>.
     * @param s the string.
     * @return the result.
     */
    public static String normalizeString(final String s) {
        if (s == null) {
            return "";
        }
        return s.replace(LINE_SEPARATORS.get(0), LINE_SEPARATORS.get(1));
    }

    public static List<String> split(String s) {
        List<String> elements = new ArrayList<>();
        if (s == null) {
            return elements;
        }
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

    public static String replaceSequence(final String s, final String replacement, final char sequenceChar) {
        char[] arr = s.toCharArray();
        boolean match = false;

        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == sequenceChar) {
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
