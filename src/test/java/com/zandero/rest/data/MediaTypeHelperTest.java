package com.zandero.rest.data;

import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static com.zandero.utils.junit.AssertFinalClass.isWellDefined;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class MediaTypeHelperTest {

	@Test
	public void isWellDefinedTest() {
		isWellDefined(MediaTypeHelper.class);
	}

	@Test
	public void getKeyTest() {

		assertEquals("*/*", MediaTypeHelper.getKey(null));
		assertEquals("application/json", MediaTypeHelper.getKey(MediaType.APPLICATION_JSON_TYPE));
		assertEquals("application/xml", MediaTypeHelper.getKey(MediaType.APPLICATION_XML_TYPE));
	}

	@Test
	public void getMediaTypeTest() {

		assertNull(MediaTypeHelper.valueOf(null));
		assertNull(MediaTypeHelper.valueOf(""));
		assertNull(MediaTypeHelper.valueOf("  "));
		assertNull(MediaTypeHelper.valueOf(" tralala "));

		assertEquals(MediaType.APPLICATION_JSON_TYPE, MediaTypeHelper.valueOf("application/json"));
	}
}