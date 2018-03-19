package com.zandero.rest;

import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.rest.data.MethodParameter;
import com.zandero.rest.data.ParameterType;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.rest.reader.CustomWordListReader;
import com.zandero.rest.test.ImplementationRest;
import com.zandero.rest.test.TestReaderRest;
import com.zandero.rest.test.TestRest;
import io.vertx.core.http.HttpMethod;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class AnnotationProcessorTest {


	@Test
	public void getDefinitions() {

		Map<RouteDefinition, Method> definitions = AnnotationProcessor.collect(TestRest.class);

		assertEquals(10, definitions.size());

		// check
		int count = 0;
		for (RouteDefinition definition : definitions.keySet()) {

			if (definition.getPath().equals("/test/echo")) {

				Method method = definitions.get(definition);

				assertEquals(HttpMethod.GET, definition.getMethod());
				assertNotNull(definition.getProduces());
				assertEquals(1, definition.getProduces().length);
				assertEquals("text/html", MediaTypeHelper.toString(definition.getProduces()[0]));

				assertEquals("echo", method.getName());
				count++;
			}

			if (definition.getPath().equals("/test/jax")) {

				Method method = definitions.get(definition);

				assertEquals(HttpMethod.GET, definition.getMethod());
				assertNotNull(definition.getProduces());
				assertEquals(1, definition.getProduces().length);
				assertEquals("application/json", MediaTypeHelper.toString(definition.getProduces()[0]));

				assertEquals("jax", method.getName());
				count++;
			}

			/*@GET
			@Path("/match/{this}/{that}")
			public Response match(@PathParam("this") String thisParam, @PathParam("that") String thatParam) {*/
			if (definition.getPath().equals("/test/match/{this}/{that}")) {

				//Method method = definitions.get(definition);
				assertEquals(HttpMethod.GET, definition.getMethod());
				assertNotNull(definition.getProduces());
				assertEquals(1, definition.getProduces().length);
				assertEquals("application/json", MediaTypeHelper.toString(definition.getProduces()[0]));

				assertEquals(2, definition.getParameters().size());
				MethodParameter param = definition.getParameters().get(0);
				assertEquals("this", param.getName());
				assertEquals(ParameterType.path, param.getType());
				assertEquals(0, param.getIndex());
				assertEquals(String.class, param.getDataType());

				param = definition.getParameters().get(1);
				assertEquals("that", param.getName());
				assertEquals(ParameterType.path, param.getType());
				assertEquals(1, param.getIndex());
				assertEquals(String.class, param.getDataType());
				count++;
			}
		}

		assertEquals(3, count);
	}

	@Test
	public void getReaderDefinitions() {

		Map<RouteDefinition, Method> definitions = AnnotationProcessor.collect(TestReaderRest.class);
		assertEquals(4, definitions.size());

		int count = 0;
		// check
		for (RouteDefinition definition : definitions.keySet()) {

			if (definition.getPath().equals("/read/custom")) {

				assertEquals(HttpMethod.POST, definition.getMethod());
				assertEquals(CustomWordListReader.class, definition.getReader());

				assertEquals(1, definition.getParameters().size());
				MethodParameter param = definition.getParameters().get(0);
				assertEquals("arg0", param.getName());
				assertEquals(ParameterType.body, param.getType());
				assertEquals(0, param.getIndex());
				assertEquals(-1, param.getPathIndex());
				assertEquals(List.class, param.getDataType());

				count++;
			}
		}

		assertEquals(1, count);
	}

	@Test
	public void getInheritedDefinition() {

		Map<RouteDefinition, Method> definitions = AnnotationProcessor.collect(ImplementationRest.class);

		assertEquals(2, definitions.size());


		int count = 0;
		for (RouteDefinition definition : definitions.keySet()) {

			if (definition.getPath().equals("/implementation/echo")) {

				//Method method = definitions.get(definition);

				assertEquals(HttpMethod.GET, definition.getMethod());
				assertNotNull(definition.getProduces());
				assertEquals(2, definition.getProduces().length);
				assertEquals("html/text", MediaTypeHelper.toString(definition.getProduces()[0]));
				assertEquals("application/json", MediaTypeHelper.toString(definition.getProduces()[1]));

				assertEquals(1, definition.getConsumes().length);
				assertEquals("application/json", MediaTypeHelper.toString(definition.getConsumes()[0]));

				assertEquals(1, definition.getParameters().size());
				MethodParameter param = definition.getParameters().get(0);
				assertEquals("name", param.getName());
				assertEquals(0, param.getIndex());
				assertEquals(-1, param.getPathIndex());
				assertEquals(-1, param.getRegExIndex());
				assertEquals(ParameterType.query, param.getType());

				count++;
			}

			if (definition.getPath().equals("/implementation/get/{id}")) {

				//Method method = definitions.get(definition);

				assertEquals(HttpMethod.GET, definition.getMethod());
				assertNotNull(definition.getProduces());
				assertEquals(2, definition.getConsumes().length);
				assertEquals("html/text", MediaTypeHelper.toString(definition.getConsumes()[0]));
				assertEquals("application/json", MediaTypeHelper.toString(definition.getConsumes()[1]));

				assertEquals(1, definition.getProduces().length);
				assertEquals("application/json", MediaTypeHelper.toString(definition.getProduces()[0]));

				assertEquals(1, definition.getParameters().size());
				MethodParameter param = definition.getParameters().get(0);
				assertEquals("id", param.getName());

				assertEquals(ParameterType.path, param.getType());
				assertEquals(3, param.getPathIndex());
				assertEquals(0, param.getIndex());
				assertEquals(-1, param.getRegExIndex());

				count++;
			}
		}

		assertEquals(2, count);
	}
}