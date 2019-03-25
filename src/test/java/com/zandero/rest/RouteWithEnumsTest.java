package com.zandero.rest;

import com.zandero.rest.test.TestEnumRest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RouteWithEnumsTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before();

		Router router = RestRouter.register(vertx, TestEnumRest.class);
		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void valueOfTest(TestContext context) {

		final Async async = context.async();

		client.getNow("/enum/simple/one", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("one", body.toString());
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void fromStringTest(TestContext context) {

		final Async async = context.async();

		client.getNow("/enum/fromString/3", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("three", body.toString());
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		});
	}

}