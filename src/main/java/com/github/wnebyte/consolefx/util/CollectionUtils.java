package com.github.wnebyte.consolefx.util;

import java.util.Collection;

/**
 * This class declares utility-methods for working with instances of {@link Collection}.
 */
public final class CollectionUtils {

    /**
     * Returns a new <code>boolean[]</code> populated with the elements contained within the
     * specified <code>c</code>.
     * @param c the Collection of elements.
     * @return a new <code>boolean[]</code> populated with the specified elements,
     * or an empty array if the specified <code>c</code> is <code>null</code>.
     */
    public static boolean[] toBooleanArray(final Collection<Boolean> c) {
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
     * Returns a new <code>byte[]</code> populated with the elements contained within the
     * specified <code>c</code>.
     * @param c the Collection of elements.
     * @return a new <code>byte[]</code> populated with the specified elements,
     * or an empty array if the specified <code>c</code> is <code>null</code>.
     */
    public static byte[] toByteArray(final Collection<Byte> c) {
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
     * Returns a new <code>char[]</code> populated with the elements contained within the
     * specified <code>c</code>.
     * @param c the Collection of elements.
     * @return a new <code>char[]</code> populated with the specified elements,
     * or an empty array if the specified <code>c</code> is <code>null</code>.
     */
    public static char[] toCharArray(final Collection<Character> c) {
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
     * Returns a new <code>double[]</code> populated with the elements contained within the
     * specified <code>c</code>.
     * @param c the Collection of elements.
     * @return a new <code>double[]</code> populated with the specified elements,
     * or an empty array if the specified <code>c</code> is <code>null</code>.
     */
    public static double[] toDoubleArray(final Collection<Double> c) {
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
     * Returns a new <code>float[]</code> populated with the elements contained within the
     * specified <code>c</code>.
     * @param c the Collection of elements.
     * @return a new <code>float[]</code> populated with the specified elements,
     * or an empty array if the specified <code>c</code> is <code>null</code>.
     */
    public static float[] toFloatArray(final Collection<Float> c) {
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
     * Returns a new <code>long[]</code> populated with the elements contained within the
     * specified <code>c</code>.
     * @param c the Collection of elements.
     * @return a new <code>long[]</code> populated with the specified elements,
     * or an empty array if the specified <code>c</code> is <code>null</code>.
     */
    public static long[] toLongArray(final Collection<Long> c) {
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
     * Returns a new <code>int[]</code> populated with the elements contained within the
     * specified <code>c</code>.
     * @param c the Collection of elements.
     * @return a new <code>int[]</code> populated with the specified elements,
     * or an empty array if the specified <code>c</code> is <code>null</code>.
     */
    public static int[] toIntArray(final Collection<Integer> c) {
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
     * Returns a new <code>short[]</code> populated with the elements contained within the
     * specified <code>c</code>.
     * @param c the Collection of elements.
     * @return a new <code>short[]</code> populated with the specified elements,
     * or an empty array if the specified <code>c</code> is <code>null</code>.
     */
    public static short[] toShortArray(final Collection<Short> c) {
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
