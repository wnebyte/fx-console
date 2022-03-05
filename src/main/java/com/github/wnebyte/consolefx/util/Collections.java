package com.github.wnebyte.consolefx.util;

import java.util.Collection;

/**
 * This class declares utility-methods for working with instances of {@link Collection}.
 */
public final class Collections {

    /**
     * Returns a new array containing the elements contained within the
     * specified <code>Collection</code>.
     * @param c a Collection.
     * @return a new array containing the elements contained within the specified Collection,
     * or an empty array if the specified Collection was <code>null</code>.
     */
    public static boolean[] toBooleanArray(Collection<Boolean> c) {
        boolean[] arr = new boolean[(c == null) ? 0 : c.size()];
        if (c == null) {
            return arr;
        }
        int i = 0;
        for (boolean e : c) {
            arr[i++] = e;
        }
        return arr;
    }

    /**
     * Returns a new array containing the elements contained within the
     * specified <code>Collection</code>.
     * @param c a Collection.
     * @return a new array containing the elements contained within the specified Collection,
     * or an empty array if the specified Collection was <code>null</code>.
     */
    public static byte[] toByteArray(Collection<Byte> c) {
        byte[] arr = new byte[(c == null) ? 0 : c.size()];
        if (c == null) {
            return arr;
        }
        int i = 0;
        for (byte e : c) {
            arr[i++] = e;
        }
        return arr;
    }

    /**
     * Returns a new array containing the elements contained within the
     * specified <code>Collection</code>.
     * @param c a Collection.
     * @return a new array containing the elements contained within the specified Collection,
     * or an empty array if the specified Collection was <code>null</code>.
     */
    public static char[] toCharArray(Collection<Character> c) {
        char[] arr = new char[(c == null) ? 0 : c.size()];
        if (c == null) {
            return arr;
        }
        int i = 0;
        for (char e : c) {
            arr[i++] = e;
        }
        return arr;
    }

    /**
     * Returns a new array containing the elements contained within the
     * specified <code>Collection</code>.
     * @param c a Collection.
     * @return a new array containing the elements contained within the specified Collection,
     * or an empty array if the specified Collection was <code>null</code>.
     */
    public static double[] toDoubleArray(Collection<Double> c) {
        double[] arr = new double[(c == null) ? 0 : c.size()];
        if (c == null) {
            return arr;
        }
        int i = 0;
        for (double e : c) {
            arr[i++] = e;
        }
        return arr;
    }

    /**
     * Returns a new array containing the elements contained within the
     * specified <code>Collection</code>.
     * @param c a Collection.
     * @return a new array containing the elements contained within the specified Collection,
     * or an empty array if the specified Collection was <code>null</code>.
     */
    public static float[] toFloatArray(Collection<Float> c) {
        float[] arr = new float[(c == null) ? 0 : c.size()];
        if (c == null) {
            return arr;
        }
        int i = 0;
        for (float e : c) {
            arr[i++] = e;
        }
        return arr;
    }

    /**
     * Returns a new array containing the elements contained within the
     * specified <code>Collection</code>.
     * @param c a Collection.
     * @return a new array containing the elements contained within the specified Collection,
     * or an empty array if the specified Collection was <code>null</code>.
     */
    public static long[] toLongArray(Collection<Long> c) {
        long[] arr = new long[(c == null) ? 0 : c.size()];
        if (c == null) {
            return arr;
        }
        int i = 0;
        for (long e : c) {
            arr[i++] = e;
        }
        return arr;
    }

    /**
     * Returns a new array containing the elements contained within the
     * specified <code>Collection</code>.
     * @param c a Collection.
     * @return a new array containing the elements contained within the specified Collection,
     * or an empty array if the specified Collection was <code>null</code>.
     */
    public static int[] toIntArray(Collection<Integer> c) {
        int[] arr = new int[(c == null) ? 0 : c.size()];
        if (c == null) {
            return arr;
        }
        int i = 0;
        for (int e : c) {
            arr[i++] = e;
        }
        return arr;
    }

    /**
     * Returns a new array containing the elements contained within the
     * specified <code>Collection</code>.
     * @param c a Collection.
     * @return a new array containing the elements contained within the specified Collection,
     * or an empty array if the specified Collection was <code>null</code>.
     */
    public static short[] toShortArray(Collection<Short> c) {
        short[] arr = new short[(c == null) ? 0 : c.size()];
        if (c == null) {
            return arr;
        }
        int i = 0;
        for (short e : c) {
            arr[i++] = e;
        }
        return arr;
    }
}
