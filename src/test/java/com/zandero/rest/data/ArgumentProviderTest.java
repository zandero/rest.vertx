package com.zandero.rest.data;

import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.JsonUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 */
public class ArgumentProviderTest {

	@Test
	public void convertPrimitiveTypes() {

		assertEquals(1, ArgumentProvider.stringToPrimitiveType(int.class, "1"));
		assertEquals(false, ArgumentProvider.stringToPrimitiveType(boolean.class, "FALSE"));
		assertEquals('a', ArgumentProvider.stringToPrimitiveType(char.class, "a"));
		assertEquals((short)100, ArgumentProvider.stringToPrimitiveType(short.class, "100"));
		assertEquals(100_100_100L, ArgumentProvider.stringToPrimitiveType(long.class, "100100100"));
		assertEquals((float)100100.98, ArgumentProvider.stringToPrimitiveType(float.class, "100100.98"));
		assertEquals(100100.987, ArgumentProvider.stringToPrimitiveType(double.class, "100100.987"));
	}

	@Test
	public void convertNullableTypes() {

		assertEquals(1, ArgumentProvider.stringToPrimitiveType(Integer.class, "1"));
		assertEquals(false, ArgumentProvider.stringToPrimitiveType(Boolean.class, "FALSE"));
		assertEquals('a', ArgumentProvider.stringToPrimitiveType(Character.class, "a"));
		assertEquals((short)100, ArgumentProvider.stringToPrimitiveType(Short.class, "100"));
		assertEquals(100_100_100L, ArgumentProvider.stringToPrimitiveType(Long.class, "100100100"));
		assertEquals((float)100100.98, ArgumentProvider.stringToPrimitiveType(Float.class, "100100.98"));
		assertEquals(100100.987, ArgumentProvider.stringToPrimitiveType(Double.class, "100100.987"));
	}

	@Test
	public void convertToJson() {

		Dummy test = new Dummy("Hello", "World");
		String value = JsonUtils.toJson(test);

		Object item = ArgumentProvider.convert(Dummy.class, value, null);
		assertTrue(item instanceof Dummy);

		Dummy dummy = (Dummy)item;
		assertEquals("Hello", dummy.name);
		assertEquals("World", dummy.value);
	}
}