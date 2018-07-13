package com.zandero.rest.data;

import com.zandero.rest.AnnotationProcessor;
import com.zandero.rest.annotation.RouteOrder;
import com.zandero.rest.test.TestMissingAnnotationsRest;
import com.zandero.rest.test.TestPostRest;
import com.zandero.rest.test.TestRegExRest;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;

/**
 *
 */
public class RouteDefinitionTest {

	@Test
	public void getDefinitionTest() throws NoSuchMethodException {

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
	public void getBodyParamTest() {

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

	@Test
	public void regExDefinitionTest() {

		RouteDefinition base = new RouteDefinition(TestRegExRest.class);

		// 1.
		Method method = TestRegExRest.class.getMethods()[0];
		RouteDefinition def = new RouteDefinition(base, method);
		assertTrue(def.pathIsRegEx());
		assertEquals("/regEx/\\d+/minus/\\d+", def.getPath());
		assertEquals("\\/regEx\\/\\d+\\/minus\\/\\d+", def.getRoutePath());

		// 2.
		method = TestRegExRest.class.getMethods()[1];
		def = new RouteDefinition(base, method);
		assertEquals("/regEx/\\d+", def.getPath());
		assertEquals("\\/regEx\\/\\d+", def.getRoutePath());
		assertTrue(def.pathIsRegEx());

		// 3.
		method = TestRegExRest.class.getMethods()[2];
		def = new RouteDefinition(base, method);
		assertEquals("/regEx/{one:\\w+}/{two:\\d+}/{three:\\w+}", def.getPath());
		assertEquals("\\/regEx\\/\\w+\\/\\d+\\/\\w+", def.getRoutePath());
		assertTrue(def.pathIsRegEx());

		// 4.
		method = TestRegExRest.class.getMethods()[3];
		def = new RouteDefinition(base, method);
		assertTrue(def.pathIsRegEx());


		//assertEquals("/regEx/{one:\\w+}/{two:\\d+}/{three:\\w+}", def.getPath());

		// TODO: issue #25
		// assertEquals("\\/regEx\\/\\w+\\/\\d+\\/\\w+", def.getRoutePath());
	}

	@Test
	public void missingArgumentAnnotationTest() {

		try {
			AnnotationProcessor.get(TestMissingAnnotationsRest.class);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("com.zandero.rest.test.TestMissingAnnotationsRest.returnOuch(String arg0) - " +
			             "Missing argument annotation (@PathParam, @QueryParam, @FormParam, @HeaderParam, @CookieParam or @Context) for: arg0!",
			             e.getMessage());
		}
	}

	@Test public void isAsyncTest() {

		Future<String> out = Future.future();
		CompositeFuture out2 = CompositeFuture.all(out, out);

		CompletableFuture<String> complete = new CompletableFuture<>();

		assertTrue(RouteDefinition.isAsync(out.getClass()));
		assertTrue(RouteDefinition.isAsync(out2.getClass()));

		assertFalse(RouteDefinition.isAsync(complete.getClass()));
		assertFalse(RouteDefinition.isAsync(String.class));
	}
}