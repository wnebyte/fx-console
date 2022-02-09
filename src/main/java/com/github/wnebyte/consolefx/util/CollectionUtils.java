package com.github.wnebyte.consolefx.util;

import java.util.Collection;

/**
 * This class declares utility-methods for working with instances of {@link Collection}.
 */
public final class CollectionUtils {

    public static char[] toCharArray(final Collection<Character> c) {
        char[] arr = new char[(c == null) ? 0 : c.size()];
        if (c == null) {
            return arr;
        }
        int i = 0;
        for (char ch : c) {
            arr[i++] = ch;
        }
        return arr;
    }
}
