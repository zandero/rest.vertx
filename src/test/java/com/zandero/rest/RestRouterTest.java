package com.zandero.rest;

import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.VertxTest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RestRouterTest extends VertxTest {

	@Test
	public void registerTestRest(TestContext context) {

		TestRest testRest = new TestRest();

		Router router = RestRouter.register(vertx, testRest);
		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(PORT);

		// call and check response
		final Async async = context.async();

		client.getNow("/test/echo", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Hello world!", body.toString());
				async.complete();
			});
		});
	}
}