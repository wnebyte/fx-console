package com.github.wnebyte.fxconsole.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public final class CollectionUtils {

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(final Collection<T> collection, final Class<T> cls) {
        if ((collection == null) || (cls == null)) { return null; }
        T[] arr = (T[]) Array.newInstance(cls, collection.size());
        return collection.toArray(arr);
    }

    public static <T> LinkedList<T> toLinkedList(final T[] arr) {
        if (arr == null) { return null; }
        return new LinkedList<T>(Arrays.asList(arr));
    }

    public static char[] toCharArray(final Collection<Character> collection) {
        if (collection == null) { return null; }
        char[] arr = new char[collection.size()];

        int i = 0;
        for (char c : collection) {
            arr[i++] = c;
        }
        return arr;
    }
}
