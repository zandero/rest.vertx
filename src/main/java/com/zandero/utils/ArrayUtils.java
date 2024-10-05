package com.zandero.utils;

import java.lang.reflect.Array;
import java.util.Set;

/**
 * Utils providing additional functionality when working with arrays
 */
public final class ArrayUtils {

    private ArrayUtils() {
        // hide constructor
    }

    /**
     * joins two arrays and preserves array order
     * array1 items are followed by array2 items as given
     *
     * @param array1 first array
     * @param array2 second array
     * @param <T>    class type of object in arrays
     * @return joined array holding both arrays in same order as given
     */
    @SafeVarargs
    public static <T> T[] join(final T[] array1, final T... array2) {

        if (isEmpty(array1) && isEmpty(array2)) {
            return null;
        }

        if (isEmpty(array1)) {
            return array2;
        }

        if (isEmpty(array2)) {
            return array1;
        }

        final Class<?> type1 = array1.getClass().getComponentType();

        @SuppressWarnings("unchecked") // OK, because array is of type T
        final T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);

        int index = 0;
        for (T item : array1) {
            joinedArray[index++] = item;
        }

        for (T item : array2) {
            joinedArray[index++] = item;
        }

        return joinedArray;
    }

    private static <T> boolean isEmpty(T[] array1) {

        return array1 == null || array1.length == 0;
    }

    /**
     * Converts an array to a set (copy of function already present in SetUtils)
     * @param array to be converted
     * @param <T> type of class in array
     * @return set of values in array
     */
    @SafeVarargs
    public static <T> Set<T> toSet(final T... array) {
        return SetUtils.from(array);
    }
}