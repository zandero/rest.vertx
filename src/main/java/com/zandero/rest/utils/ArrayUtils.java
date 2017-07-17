package com.zandero.rest.utils;

import java.lang.reflect.Array;

/**
 *
 */
public final class ArrayUtils {

	// Copied from: https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/ArrayUtils.java / addAll()
	public static <T> T[] join(final T[] array1, final T... array2) {
		if (array1 == null) {
			return array2;
		} else if (array2 == null) {
			return array1;
		}

		final Class<?> type1 = array1.getClass().getComponentType();
		@SuppressWarnings("unchecked") // OK, because array is of type T
		final T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		try {
			System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		} catch (final ArrayStoreException ase) {
			// Check if problem was due to incompatible types
            /*
             * We do this here, rather than before the copy because:
             * - it would be a wasted check most of the time
             * - safer, in case check turns out to be too strict
             */
			final Class<?> type2 = array2.getClass().getComponentType();
			if (!type1.isAssignableFrom(type2)) {
				throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of "
						                                   + type1.getName(), ase);
			}
			throw ase; // No, so rethrow original
		}
		return joinedArray;
	}
}
