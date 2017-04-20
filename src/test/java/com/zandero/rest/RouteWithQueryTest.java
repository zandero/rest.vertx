package com.zandero.rest;

import com.zandero.rest.test.TestQueryRest;
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
public class RouteWithQueryTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		TestQueryRest testRest = new TestQueryRest();

		Router router = RestRouter.register(vertx, testRest);
		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(PORT);
	}

	@Test
	public void testAdd(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/query/add?one=1&two=2", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("3", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void additionalParametersTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/query/add?one=3&two=5&three=3", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("8", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void missingParametersTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/query/add?two=5&three=3", response -> {

			context.assertEquals(400, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Missing @QueryParam(\"one\") for: /query/add", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void queryTypeMismatchTest(TestContext context) {

		final Async async = context.async();

		client.getNow("/query/add?one=A&two=2", response -> {

			context.assertEquals(400, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Invalid parameter type for: @QueryParam(\"one\") for: /query/add, expected: int, but got: String", body.toString());
				async.complete();
			});
		});
	}
}
