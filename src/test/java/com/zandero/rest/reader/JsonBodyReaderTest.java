package com.zandero.rest.reader;

import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.JsonUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class JsonBodyReaderTest {

	@Test
	public void convertToJson() {

		Dummy test = new Dummy("Hello", "World");
		String value = JsonUtils.toJson(test);

		JsonBodyReader reader = new JsonBodyReader();
		Object item = reader.read(value, Dummy.class);

		assertTrue(item instanceof Dummy);

		Dummy dummy = (Dummy)item;
		assertEquals("Hello", dummy.name);
		assertEquals("World", dummy.value);
	}

}