package com.zandero.rest;

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

		assertEquals(1, definitions.size());

		// check
		RouteDefinition definition = definitions.keySet().iterator().next();
		Method method = definitions.get(definition);

		assertEquals(HttpMethod.GET, definition.getMethod());
		assertEquals("/test/echo", definition.getPath());
		assertNotNull(definition.getProduces());
		assertEquals(1, definition.getProduces().length);
		assertEquals("application/json", definition.getProduces()[0]);

		assertEquals("echo", method.getName());
	}
}