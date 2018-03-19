package com.zandero.rest;

import com.zandero.rest.test.TestEchoRest;
import io.vertx.core.http.HttpMethod;
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
public class RouteWithTraceTest extends VertxTest {

	@Test
	public void testTrace(TestContext context) {

		Router router = RestRouter.register(vertx, TestEchoRest.class);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);

		// call and check response
		final Async async = context.async();

		client.request(HttpMethod.TRACE, "/rest/echo", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("trace", body.toString()); // returns sorted list of unique words
				async.complete();
			});
		}).end();
	}
}
