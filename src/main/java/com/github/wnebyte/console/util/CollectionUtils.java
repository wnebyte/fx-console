package com.github.wnebyte.console.util;

import java.util.Collection;

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
