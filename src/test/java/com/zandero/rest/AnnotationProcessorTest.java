package com.zandero.rest;

import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.test.TestRest;
import io.vertx.core.http.HttpMethod;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class AnnotationProcessorTest {


	@Test
	public void getDefinitions() {

		Map<RouteDefinition, Method> definitions = AnnotationProcessor.get(TestRest.class);

		assertEquals(9, definitions.size());

		// check
		for (RouteDefinition definition : definitions.keySet()) {

			if (definition.getPath().equals("/test/echo")) {

				Method method = definitions.get(definition);

				assertEquals(HttpMethod.GET, definition.getMethod());
				assertNotNull(definition.getProduces());
				assertEquals(1, definition.getProduces().length);
				assertEquals("text/html", MediaTypeHelper.toString(definition.getProduces()[0]));

				assertEquals("echo", method.getName());
			}

			if (definition.getPath().equals("/test/jax")) {

				Method method = definitions.get(definition);

				assertEquals(HttpMethod.GET, definition.getMethod());
				assertNotNull(definition.getProduces());
				assertEquals(1, definition.getProduces().length);
				assertEquals("application/json", MediaTypeHelper.toString(definition.getProduces()[0]));

				assertEquals("jax", method.getName());
			}
		}
	}
}