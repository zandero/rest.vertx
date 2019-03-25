package com.zandero.rest;

import com.zandero.rest.test.TestSessionRest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RouteWithSessionTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before();

		Router router = Router.router(vertx);
		SessionHandler handler = SessionHandler.create(LocalSessionStore.create(vertx));
		router.route().handler(handler);
		RestRouter.register(router, TestSessionRest.class);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void testResponseSession(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/session/echo", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertNotNull(body.toString());
				async.complete();
			});
		});
	}
}
