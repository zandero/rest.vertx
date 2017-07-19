package com.zandero.rest;

import com.zandero.rest.test.TestContextRest;
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
		RestRouter.addContextProvider(Dummy.class, request -> new Dummy("test", "name"));

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

			response.handler(body -> {
				context.assertEquals("{\"name\":\"test\",\"value\":\"name\"}", body.toString());
				async.complete();
			});
		});
	}
}
