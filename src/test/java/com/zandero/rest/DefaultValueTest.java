package com.zandero.rest;

import com.zandero.rest.test.TestDefaultValueRest;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.Handler;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class DefaultValueTest extends VertxTest {

	@Test
	public void callMissingQueryParam(TestContext context) {

		Router router = RestRouter.register(vertx, TestDefaultValueRest.class);
		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);

		// call and check response
		final Async async = context.async();

		client.getNow("/default/echo", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Hello unknown", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void callPresentQueryParam(TestContext context) {

		Router router = RestRouter.register(vertx, TestDefaultValueRest.class);
		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);

		// call and check response
		final Async async = context.async();

		client.getNow("/default/echo?name=baby", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Hello baby", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void callMissingContext(TestContext context) {

		Router router = RestRouter.register(vertx, TestDefaultValueRest.class);
		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);

		// call and check response
		final Async async = context.async();

		client.getNow("/default/context", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Context is unknown user", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void callPresentContext(TestContext context) {

		Router router = Router.router(vertx);
		router.route().handler(pushContextHandler());
		RestRouter.register(router, TestDefaultValueRest.class);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);

		// call and check response
		final Async async = context.async();


		client.getNow("/default/context", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Context is test user", body.toString());
				async.complete();
			});
		});
	}

	private Handler<RoutingContext> pushContextHandler() {

		return context -> {
			RestRouter.pushContext(context, new Dummy("test", "user"));
			context.next();
		};
	}
}
