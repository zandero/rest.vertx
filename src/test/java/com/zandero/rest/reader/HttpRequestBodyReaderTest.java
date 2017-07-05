package com.zandero.rest.reader;

import com.zandero.rest.test.reader.DummyBodyReader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class HttpRequestBodyReaderTest {

	@Test
	public void getGenericTypeTest() {

		assertNull(HttpRequestBodyReader.getGenericType(DummyBodyReader.class)); // type erasure ... we can't tell
	//	assertEquals(Dummy.class, DummyBodyReader.class.getMethods()[0].getParameters()[1].getType());
		assertEquals(Integer.class, HttpRequestBodyReader.getGenericType(IntegerBodyReader.class)); // at least we know so much

	}
}