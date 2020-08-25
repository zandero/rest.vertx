package com.zandero.rest.data;

import com.zandero.rest.AnnotationProcessor;
import com.zandero.rest.annotation.RouteOrder;
import com.zandero.rest.test.*;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpMethod;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

/**
 *
 */
class RouteDefinitionTest {

	@Test
	void getDefinitionTest() throws NoSuchMethodException {

		RouteDefinition base = new RouteDefinition(TestRest.class);

		assertEquals("/test", base.getPath());

		assertNotNull(base.getProduces());
		assertEquals(1, base.getProduces().length);

		assertNull(base.getMethod());
		assertNull(base.getConsumes());

		// 2.
		Method method = TestRest.class.getMethod("echo");
		RouteDefinition def = new RouteDefinition(base, method);

		assertEquals("/test/echo", def.getPath());

		assertNotNull(def.getProduces());
		assertEquals(1, def.getProduces().length);

		assertEquals(HttpMethod.GET, def.getMethod());

		assertNull(def.getConsumes());
	}

	@Test
	void emptyRootPath() throws NoSuchMethodException {

		RouteDefinition base = new RouteDefinition(TestEchoRest2.class);
		assertEquals("/test", base.getPath());

		Method method = TestEchoRest2.class.getMethod("bla");
		RouteDefinition def = new RouteDefinition(base, method);

		assertEquals("/test/bla", def.getPath());
	}

	@Test
	void getBodyParamTest() {

		RouteDefinition base = new RouteDefinition(TestPostRest.class);

		Method[] methods = TestPostRest.class.getMethods();

		Arrays.sort(methods, Comparator.comparingInt(method -> {
			RouteOrder order = method.getAnnotation(RouteOrder.class);
			return order == null ? 100 : order.value();
		}));

		// 1.
		Method method = methods[0];
		RouteDefinition def = new RouteDefinition(base, method);

		//def.setArguments(method);

		assertEquals("/post/json", def.getPath());
		assertEquals(HttpMethod.POST, def.getMethod());

		assertEquals(2, def.getParameters().size());

		MethodParameter param = def.getParameters().get(0);
		assertEquals("arg0", param.getName());
		assertEquals(ParameterType.unknown, param.getType()); // to be proclaimed as body by annotation processor
		assertEquals(Dummy.class, param.getDataType());
		assertNull(param.getDefaultValue());

		param = def.getParameters().get(1);
		assertEquals("X-Test", param.getName());
		assertEquals(ParameterType.header, param.getType());
		assertEquals(String.class, param.getDataType());
		assertNull(param.getDefaultValue());
	}

	// Test sometimes flaky as sorting of methods doesn't seem to work
	// ...
	@Test
	void regExDefinitionTest() {

		RouteDefinition base = new RouteDefinition(TestRegExRest.class);

		List<Method> methods = new ArrayList<>(Arrays.asList(TestRegExRest.class.getMethods()));
		// sort methods by name to prevent flakiness of test
		methods.sort(Comparator.comparing(Method::getName));


		Method method = methods.get(0);
		RouteDefinition def = new RouteDefinition(base, method);
		assertEquals("/regEx/{one:\\w+}/{two:\\d+}/{three:\\w+}", def.getPath());
		assertEquals("/regEx/\\w+/\\d+/\\w+", def.getRoutePath());
		assertTrue(def.pathIsRegEx());

		method = methods.get(1);
		def = new RouteDefinition(base, method);
		assertEquals("/regEx/:one:\\d+", def.getPath());
		assertEquals("/regEx/\\d+", def.getRoutePath());
		assertTrue(def.pathIsRegEx());

		method = methods.get(2);
		def = new RouteDefinition(base, method);
		assertTrue(def.pathIsRegEx());
		assertEquals("/regEx/:one:\\d+/minus/:two:\\d+", def.getPath());
		assertEquals("/regEx/\\d+/minus/\\d+", def.getRoutePath());

		method = methods.get(3);
		def = new RouteDefinition(base, method);
		assertTrue(def.pathIsRegEx());

		assertEquals("/regEx/{path:(?!api\\/).*}", def.getPath());
		assertEquals( "/regEx/(?!api\\/).*", def.getRoutePath());
	}

	@Test
	void missingArgumentAnnotationTest() {

		try {
			AnnotationProcessor.get(TestMissingAnnotationsRest.class);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("com.zandero.rest.test.TestMissingAnnotationsRest.returnOuch(String arg0) - " +
			             "Missing argument annotation (@PathParam, @QueryParam, @FormParam, @HeaderParam, @CookieParam or @Context) for: arg0!",
			             e.getMessage());
		}
	}

	@Test
	void isAsyncTest() {

		Promise<String> out = Promise.promise();
		Future<String> out2 = out.future();
		CompositeFuture out3 = CompositeFuture.all(out2, out2);

		CompletableFuture<String> complete = new CompletableFuture<>();

		assertTrue(RouteDefinition.isAsync(out2.getClass()));
		assertTrue(RouteDefinition.isAsync(out3.getClass()));

		assertFalse(RouteDefinition.isAsync(complete.getClass()));
		assertFalse(RouteDefinition.isAsync(String.class));
	}
}