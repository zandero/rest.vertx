package com.zandero.rest.data;

import com.zandero.rest.test.TestRest;
import io.vertx.core.http.HttpMethod;
import org.junit.Test;

import java.lang.reflect.Method;

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
		RouteDefinition def = new RouteDefinition(base, method.getAnnotations());

		assertEquals("/test/echo", def.getPath());

		assertNotNull(def.getProduces());
		assertEquals(1, def.getProduces().length);

		assertEquals(HttpMethod.GET, def.getMethod());

		assertNull(def.getConsumes());
	}
}