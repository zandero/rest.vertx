package com.zandero.rest;

import com.zandero.rest.test.TestContextRest;
import com.zandero.rest.test.data.TokenProvider;
import com.zandero.rest.test.json.Dummy;
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
public class RouteWithContextProviderTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		TestContextRest testRest = new TestContextRest();

		Router router = RestRouter.register(vertx, testRest);
		//RestRouter.provide(router, TokenProvider.class);

		RestRouter.addProvider(Dummy.class, request -> new Dummy("test", "name"));
		RestRouter.addProvider(TokenProvider.class);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void pushContextTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/context/custom", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("{\"name\":\"test\",\"value\":\"name\"}", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void pushContextTokenTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/context/token", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("mySession", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "mySession").end();
	}

	@Test
	public void noContextTokenTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/context/token", response -> {

			context.assertEquals(400, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Can't provide @Context of type: class com.zandero.rest.test.data.Token", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void readMethodContextTokenDummy(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/context/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("mySession,Name:Dummy", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "mySession")
		      .putHeader("X-dummy-value", "Dummy")
		      .putHeader("X-dummy-name", "Name")
		      .end();
	}

	@Test
	public void readParamContextTokenDummy(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/context/dummy-token", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("***mySession***,Name:Dummy", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "mySession")
		      .putHeader("X-dummy-value", "Dummy")
		      .putHeader("X-dummy-name", "Name")
		      .end();
	}
}
