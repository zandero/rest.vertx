package com.zandero.rest;
/*

import com.zandero.rest.test.TestEchoRest;
import com.zandero.rest.test.handler.NotFoundHandler;
import com.zandero.rest.test.handler.OtherNotFoundHandler;
import com.zandero.rest.test.handler.RestNotFoundHandler;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

*/
/**
 *
 *//*

@RunWith(VertxUnitRunner.class)
public class NoMatchTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before();

		Router router = new RestBuilder(vertx)
			                .register(TestEchoRest.class)
			                .notFound(".*\\/other/?.*", OtherNotFoundHandler.class)
			                .notFound("/rest/.*", RestNotFoundHandler.class)
			                .notFound(NotFoundHandler.class)
			                .build();

		RestRouter.notFound(router, "rest", RestNotFoundHandler.class);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void testEcho(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/rest/echo", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("\"echo\"", body.toString());
				context.assertEquals(200, response.statusCode());

				String header = response.getHeader("Content-Type");

				async.complete();
			});
		});
	}

	@Test
	public void testNoMatchDefault(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/bla", response -> {

			context.assertEquals(404, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("404 HTTP Resource: '/bla' not found!", body.toString());
				async.complete();
			});
		}).putHeader("Accept", "application/json").end();
	}

	@Test
	public void testNoMatchRest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/rest/bla", response -> {

			context.assertEquals(404, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("REST endpoint: '/rest/bla' not found!", body.toString());
				async.complete();
			});
		}).putHeader("Accept", "application/json").end();
	}

	@Test
	public void testOtherRest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/rest/other", response -> {

			context.assertEquals(404, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("'/rest/other' not found!", body.toString());
				async.complete();
			});
		}).putHeader("Accept", "application/json").end();
	}

	@Test
	public void testOther2Rest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/rest/other/", response -> {

			context.assertEquals(404, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("'/rest/other/' not found!", body.toString());
				async.complete();
			});
		}).putHeader("Accept", "application/json").end();
	}
}
*/
