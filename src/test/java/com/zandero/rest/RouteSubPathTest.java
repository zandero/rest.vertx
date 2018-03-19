package com.zandero.rest;

import com.zandero.rest.test.TestPathRest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RouteSubPathTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		TestPathRest testRest = new TestPathRest();

		Router router = RestRouter.register(vertx, testRest);
		router.mountSubRouter("/sub", router);

		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(PORT);
	}

	@Test
	public void rootWithRootPathTest(TestContext context) throws IOException {

		final Async async = context.async();

		client.getNow("/query/echo/this", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("querythis", body.toString());
				async.complete();
			});
		});

		final Async async2 = context.async();
		client.getNow("/sub/query/echo/this", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("querythis", body.toString());
				async2.complete();
			});
		});
	}

	@Test
	public void rootWithRootPathTest2(TestContext context) throws IOException {

		final Async async = context.async();

		client.getNow("/this/echo/query", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("thisquery", body.toString());
				async.complete();
			});
		});

		final Async async2 = context.async();
		client.getNow("/sub/this/echo/query", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("thisquery", body.toString());
				async2.complete();
			});
		});
	}

	@Test
	public void rootWithoutPathTest(TestContext context) throws IOException {

		final Async async = context.async();

		client.getNow("/this", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("this", body.toString());
				async.complete();
			});
		});

		final Async async2 = context.async();
		client.getNow("/sub/this", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("this", body.toString());
				async2.complete();
			});
		});
	}
}
