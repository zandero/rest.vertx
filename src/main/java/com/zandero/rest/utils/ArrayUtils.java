package com.zandero.rest.utils;

import java.lang.reflect.Array;

/**
 *
 */
public final class ArrayUtils {

	/**
	 * joins two arrays and preserves array order
	 * array1 items are followed by array2 items as given
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
}
