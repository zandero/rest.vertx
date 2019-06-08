package com.zandero.rest.data;


import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static com.zandero.utils.junit.AssertFinalClass.isWellDefined;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 */
class MediaTypeHelperTest {

	@Test
	void isWellDefinedTest() {
		isWellDefined(MediaTypeHelper.class);
	}

	@Test
	void getKeyTest() {

		assertEquals("*/*", MediaTypeHelper.getKey(null));
		assertEquals("application/json", MediaTypeHelper.getKey(MediaType.APPLICATION_JSON_TYPE));
		assertEquals("application/xml", MediaTypeHelper.getKey(MediaType.APPLICATION_XML_TYPE));
	}

	@Test
	void getMediaTypeInvalidCharset() {
		MediaType type = MediaTypeHelper.valueOf("application/json;UTF-8");
		assertEquals(MediaTypeHelper.toString(MediaType.APPLICATION_JSON_TYPE), MediaTypeHelper.toString(type));
	}

	@Test
	void getMediaTypeTest() {

		assertNull(MediaTypeHelper.valueOf(null));
		assertNull(MediaTypeHelper.valueOf(""));
		assertNull(MediaTypeHelper.valueOf("  "));
		assertNull(MediaTypeHelper.valueOf(" tralala "));

		MediaType type = MediaTypeHelper.valueOf("application/json");
		assertEquals(MediaTypeHelper.toString(MediaType.APPLICATION_JSON_TYPE), MediaTypeHelper.toString(type));

		type = MediaTypeHelper.valueOf("application/json;charset=UTF-8");
		assertEquals(MediaTypeHelper.toString(MediaType.APPLICATION_JSON_TYPE.withCharset("UTF-8")), MediaTypeHelper.toString(type));
	}
}