package com.zandero.rest;

import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.exception.WebApplicationExceptionHandler;
import com.zandero.rest.reader.IntegerBodyReader;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.handler.HandleRestException;
import com.zandero.rest.test.reader.DummyBodyReader;
import com.zandero.rest.test.writer.IllegalArgumentExceptionWriter;
import io.vertx.core.http.HttpMethod;
import org.junit.Test;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
				assertNotNull(definition.getProduces());
				assertEquals(1, definition.getProduces().length);
				assertEquals("application/json", definition.getProduces()[0].toString());

				assertEquals("jax", method.getName());
			}
		}
	}

	@Test
	public void getGenericTypeTest() {

		assertNull(AnnotationProcessor.getGenericType(DummyBodyReader.class)); // type erasure ... we can't tell

		assertEquals(Integer.class, AnnotationProcessor.getGenericType(IntegerBodyReader.class)); // at least we know so much

		assertEquals(IllegalArgumentException.class, AnnotationProcessor.getGenericType(IllegalArgumentExceptionWriter.class)); // at least we know so much

		assertEquals(IllegalArgumentException.class, AnnotationProcessor.getGenericType(HandleRestException.class)); // at least we know so much

		assertEquals(WebApplicationException.class, AnnotationProcessor.getGenericType(WebApplicationExceptionHandler.class)); // at least we know so much
	}

	@Test
	public void typeAreCompatibleTest() {

		Type type = AnnotationProcessor.getGenericType(HandleRestException.class);
		try {
			AnnotationProcessor.checkIfCompatibleTypes(IllegalArgumentException.class, type, "Fail");
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void inheritedTypeAreCompatibleTest() {

		Type type = AnnotationProcessor.getGenericType(WebApplicationExceptionHandler.class);
		try {
			AnnotationProcessor.checkIfCompatibleTypes(WebApplicationException.class, type, "Fail");
		}
		catch (Exception e) {
			fail(e.getMessage());
		}

		try {
			AnnotationProcessor.checkIfCompatibleTypes(NotAllowedException.class, type, "Fail");
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
}