package com.zandero.rest;
/*
import com.zandero.rest.test.TestSessionRest;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.VertxTestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


*/
/**
 *
 *//*

@ExtendWith(VertxExtension.class)
public class RouteWithSessionTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		Router router = Router.router(vertx);
		SessionHandler handler = SessionHandler.create(LocalSessionStore.create(vertx));
		router.route().handler(handler);
		RestRouter.register(router, TestSessionRest.class);

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void testResponseSession(VertxTestContext context) {



		client.get(PORT, HOST, "/session/echo").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertNotNull(body.toString());
				async.complete();
			});
		});
	}
}
*/
