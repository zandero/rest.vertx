package com.zandero.rest.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ArrayUtilsTest {

	@Test
	public void joinTest() {

		Integer[] one = new Integer[] {1, 2, 3};
		Integer[] two = new Integer[] {4, 5, 6};

		Integer[] out = ArrayUtils.join(one, two);

		// check order
		for (int i = 1; i <= 6; i++) {
			assertEquals(i, out[i - 1].intValue());
		}
	}

	@Test
	public void joinWithNullTest() {

		Integer[] one = null;
		Integer[] two = new Integer[] {1, 2, 3};

		Integer[] out = ArrayUtils.join(one, two);

		// check order
		for (int i = 1; i <= 3; i++) {
			assertEquals(i, out[i - 1].intValue());
		}
	}
}