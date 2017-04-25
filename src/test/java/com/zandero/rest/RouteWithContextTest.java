package com.zandero.rest;

import com.zandero.rest.test.TestContextRest;
import com.zandero.rest.test.VertxTest;
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
public class RouteWithContextTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		TestContextRest testRest = new TestContextRest();

		Router router = RestRouter.register(vertx, testRest);
		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(PORT);
	}

	@Test
	public void testGetRouteContext(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/context/route", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("GET /context/route", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testGetRequestResponseContext(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/context/context", response -> {

			context.assertEquals(201, response.statusCode());

			response.handler(body -> {
				context.assertEquals("/context/context", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testGetNonExistentContext(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/context/unknown", response -> {

			context.assertEquals(400, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Can't provide @Context of type: interface javax.ws.rs.core.Request", body.toString());
				async.complete();
			});
		});
	}

}
