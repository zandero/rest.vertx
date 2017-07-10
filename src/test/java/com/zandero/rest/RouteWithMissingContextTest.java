package com.zandero.rest;

import com.zandero.rest.test.TestContextRest;
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
public class RouteWithMissingContextTest extends VertxTest {

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
	public void missingContextTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/context/custom", response -> {

			context.assertEquals(400, response.statusCode());

			response.handler(body -> {
				context.assertEquals("\"Can't provide @Context of type: class com.zandero.rest.test.json.Dummy\"", body.toString());
				async.complete();
			});
		});
	}
}
