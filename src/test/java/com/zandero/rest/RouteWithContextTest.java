package com.zandero.rest;

import com.zandero.rest.test.TestContextRest;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.Handler;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RouteWithContextTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		Router router = Router.router(vertx);
		router.route().handler(pushContextHandler());

		router = RestRouter.register(router, TestContextRest.class);
		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	private Handler<RoutingContext> pushContextHandler() {

		return context -> {
			RestRouter.pushContext(context, new Dummy("test", "user"));
			context.next();
		};
	}

	@Test
	public void testGetRouteContext(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/context/route", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("GET /context/route", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testGetRequestResponseContext(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/context/context", response -> {

			context.assertEquals(201, response.statusCode());

			response.handler(body -> {
				context.assertEquals("/context/context", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testGetNonExistentContext(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/context/unknown", response -> {

			context.assertEquals(400, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Can't provide @Context of type: interface javax.ws.rs.core.Request", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void pushContextTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/context/custom", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("{\"name\":\"test\",\"value\":\"user\"}", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testResponseContex(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/context/login", response -> {

			context.assertEquals(201, response.statusCode());
			context.assertEquals("session", response.getHeader("X-SessionId"));

			response.handler(body -> {
				context.assertEquals("Hello world!", body.toString());
				async.complete();
			});
		});
	}
}
