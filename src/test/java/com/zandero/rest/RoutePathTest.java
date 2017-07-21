package com.zandero.rest;

import com.zandero.rest.test.TestInvalidMethodRest;
import com.zandero.rest.test.TestPathRest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RoutePathTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		TestPathRest testRest = new TestPathRest();

		Router router = RestRouter.register(vertx, testRest);
		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(PORT);
	}

	@Test
	public void rootWithRootPathTest(TestContext context) throws IOException {

		final Async async = context.async();

		client.getNow("/query/echo/this", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("querythis", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void rootWithRootPathTest2(TestContext context) throws IOException {

		final Async async = context.async();

		client.getNow("/this/echo/query", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("thisquery", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void rootWithoutPathTest(TestContext context) throws IOException {

		final Async async = context.async();

		client.getNow("/this", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("this", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void invalidDuplicateMethodRestTest() {

		try {
			RestRouter.register(vertx, TestInvalidMethodRest.class);
			fail();
		}
		catch (Exception e) {
			assertEquals("class com.zandero.rest.test.TestInvalidMethodRest.echo() - Method already set to: POST!", e.getMessage());
		}
	}
}
