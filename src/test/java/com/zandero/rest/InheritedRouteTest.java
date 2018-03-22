package com.zandero.rest;

import com.zandero.rest.test.ImplementationRest;
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
public class InheritedRouteTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		Router router = RestRouter.register(vertx, ImplementationRest.class);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void echoTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/implementation/echo?name=test", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("\"test\"", body.toString()); // JsonExceptionWriter
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void getTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/implementation/get/test?additional=it", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("\"testit\"", body.toString()); // JsonExceptionWriter
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		});
	}
}
