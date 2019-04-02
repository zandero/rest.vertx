package com.zandero.rest;
/*

import com.zandero.rest.injection.GuiceInjectionProvider;
import com.zandero.rest.test.TestEchoRest;
import com.zandero.rest.test.handler.NotFoundHandler;
import com.zandero.rest.test.handler.RestNotFoundHandler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

*/
/**
 *
 *//*

@RunWith(VertxUnitRunner.class)
public class RouteWithCorsTest extends VertxTest {

	@Test
	public void start(TestContext context) {

		super.before();

		Set<String> allowedHeaders = new HashSet<>();
		allowedHeaders.add("Access-Control-Request-Method");
		allowedHeaders.add("Access-Control-Allow-Credentials");
		allowedHeaders.add("Access-Control-Allow-Origin");
		allowedHeaders.add("Access-Control-Allow-Headers");
		allowedHeaders.add("Content-Type");
		allowedHeaders.add("Origin");

		Router router = new RestBuilder(vertx)
			                .injectWith(new GuiceInjectionProvider())
			                .enableCors("*", false, -1, allowedHeaders,
			                            HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS)
			                .register(TestEchoRest.class)
			                .notFound(".*", RestNotFoundHandler.class) // rest not found info
			                .notFound(NotFoundHandler.class) // last resort 404 page
			                .build();
	}
}*/
