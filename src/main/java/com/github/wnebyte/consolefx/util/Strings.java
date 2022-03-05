package com.github.wnebyte.consolefx.util;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * This class declares utility-methods for working with instances of {@link String}.
 */
public final class Strings {

    public static final String EMPTY = "";

    public static final String WHITESPACE = " ";

    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";

    public static final String LINE_SEPARATOR_UNIX = "\n";

    public static final List<String> LINE_SEPARATORS
            = Arrays.asList(LINE_SEPARATOR_WINDOWS, LINE_SEPARATOR_UNIX);

    /**
     * Determines whether the specified <code>String</code> is empty.
     * @param s a String.
     * @return <code>true</code> if the specified String is empty,
     * otherwise <code>false</code>.
     */
    public static boolean isEmpty(String s) {
        return (s != null) && (s.equals(EMPTY));
    }

    /**
     * Determines whether the specified <code>String</code> is <code>null</code> or empty.
     * @param s a String.
     * @return <code>true</code> if the specified String is <code>null</code> or empty,
     * otherwise <code>false</code>.
     */
    public static boolean isNullOrEmpty(String s) {
        return (s == null || s.equals(EMPTY));
    }

    /**
     * Determines whether the specified <code>String</code> is not empty.
     * @param s a String.
     * @return <code>true</code> if the specified String is not empty,
     * otherwise <code>false</code>.
     */
    public static boolean isNotEmpty(String s) {
        return (s != null) && !(s.equals(EMPTY));
    }

    /**
     * Determines whether the specified <code>String</code> contains a line separator character.
     * @param s a String.
     * @return <code>true</code> if the specified String contains a line separator character,
     * otherwise <code>false</code>.
     */
    public static boolean containsLineSeparator(String s) {
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
     * Removes any occurrence of <code>"\r\n"</code> and <code>"\n"</code> from the specified <code>String</code>.
     * @param s a String.
     * @return the resulting String.
     */
    public static String removeLineSeparators(String s) {
        if (s == null) {
            return EMPTY;
        }
        for (String ls : LINE_SEPARATORS) {
            s = s.replace(ls, EMPTY);
        }
        return s;
    }

    /**
     * Replaces <code>"\r\n"</code> with <code>"\n"</code>.
     * @param s a String.
     * @return the resulting String.
     */
    public static String normalizeLineSeparators(String s) {
        if (s == null) {
            return EMPTY;
        }
        return s.replace(LINE_SEPARATOR_WINDOWS, LINE_SEPARATOR_UNIX);
    }

    public static List<String> split(String s) {
        List<String> elements = new ArrayList<>();
        if (s == null) {
            return elements;
        }
        s = normalizeLineSeparators(s);
        char[] arr = s.toCharArray();
        int start = 0;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == Chars.LINE_SEPARATOR_UNIX) {
                String substring = s.substring(start, i);
                if (substring.length() != 0) {
                    elements.add(substring);
                }
                elements.add(LINE_SEPARATOR_UNIX);
                start = i + 1;
            }
        }
        String substring = s.substring(start);
        if (substring.length() != 0) {
            elements.add(substring);
        }
        return elements;
    }

    /**
     Replaces the last substring in the specified <code>s</code> that consists of an uninterrupted sequence of the specified
     <code>c</code>, with the specified <code>replacement</code>.
     * @param s a String.
     * @param replacement a replacement String.
     * @param c a char.
     * @return the resulting String.
     */
    public static String replaceSequence(String s, String replacement, char c) {
        if (s == null) {
            return EMPTY;
        }
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
