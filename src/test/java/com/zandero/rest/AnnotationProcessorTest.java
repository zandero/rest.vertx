package com.zandero.rest;

import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.reader.IntegerBodyReader;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.reader.DummyBodyReader;
import io.vertx.core.http.HttpMethod;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.*;

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
				assertEquals("text/html", definition.getProduces()[0].toString());

				assertEquals("echo", method.getName());
			}

			if (definition.getPath().equals("/test/jax")) {

				Method method = definitions.get(definition);

				assertEquals(HttpMethod.GET, definition.getMethod());
				/*assertNotNull(definition.getProduces());
				assertEquals(1, definition.getProduces().length);
				assertEquals("application/json", definition.getProduces()[0]);

				assertEquals("echo", method.getName());*/
			}
		}
	}

	@Test
	public void getGenericTypeTest() {

		assertNull(AnnotationProcessor.getGenericType(DummyBodyReader.class)); // type erasure ... we can't tell
		//	assertEquals(Dummy.class, DummyBodyReader.class.getMethods()[0].getParameters()[1].getType());
		assertEquals(Integer.class, AnnotationProcessor.getGenericType(IntegerBodyReader.class)); // at least we know so much

	}
}