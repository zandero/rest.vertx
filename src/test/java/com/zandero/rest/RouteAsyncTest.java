package com.zandero.rest;

import com.zandero.rest.test.TestAsyncRest;
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
public class RouteAsyncTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		Router router = RestRouter.register(vertx, TestAsyncRest.class);
		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void testAsyncCallInsideRest(TestContext context) {

		final Async async = context.async();

		client.getNow("/async/call", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("{\"name\": \"async\", \"value\": \"called\"}", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testAsyncCallInsideRestNullFutureResult(TestContext context) {

		final Async async = context.async();

		client.getNow("/async/empty", response -> {

			context.assertEquals(204, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void testAsyncCallInsideRestNullNoWriterFutureResult(TestContext context) {

		final Async async = context.async();

		client.getNow("/async/null", response -> {

			context.assertEquals(200, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void testAsyncHandler(TestContext context) {

		final Async async = context.async();

		client.getNow("/async/handler", response -> {

			context.assertEquals(200, response.statusCode());
			response.handler(body -> {
				context.assertEquals("{\"name\": \"async\", \"value\": \"called\"}", body.toString());
				async.complete();
			});
		});
	}
}
