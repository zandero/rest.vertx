package com.zandero.rest.reader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class GenericBodyReaderTest {

	@Test
	public void convertPrimitiveTypes() {

		assertEquals(1, GenericBodyReader.stringToPrimitiveType("1", int.class));
		assertEquals(false, GenericBodyReader.stringToPrimitiveType("FALSE", boolean.class));
		assertEquals('a', GenericBodyReader.stringToPrimitiveType("a", char.class));
		assertEquals((short)100, GenericBodyReader.stringToPrimitiveType("100", short.class));
		assertEquals(100_100_100L, GenericBodyReader.stringToPrimitiveType("100100100", long.class));
		assertEquals((float)100100.98, GenericBodyReader.stringToPrimitiveType("100100.98", float.class));
		assertEquals(100100.987, GenericBodyReader.stringToPrimitiveType("100100.987", double.class));
	}

	@Test
	public void convertNullableTypes() {

		assertEquals(1, GenericBodyReader.stringToPrimitiveType("1", Integer.class));
		assertEquals(false, GenericBodyReader.stringToPrimitiveType("FALSE", Boolean.class));
		assertEquals('a', GenericBodyReader.stringToPrimitiveType("a", Character.class));
		assertEquals((short)100, GenericBodyReader.stringToPrimitiveType("100", Short.class));
		assertEquals(100_100_100L, GenericBodyReader.stringToPrimitiveType("100100100", Long.class));
		assertEquals((float)100100.98, GenericBodyReader.stringToPrimitiveType("100100.98", Float.class));
		assertEquals(100100.987, GenericBodyReader.stringToPrimitiveType("100100.987", Double.class));
	}
}