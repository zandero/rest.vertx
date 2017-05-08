package com.zandero.rest;

import com.zandero.rest.test.TestOrderRest;
import com.zandero.rest.test.VertxTest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RouteOrderTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		TestOrderRest testRest = new TestOrderRest();

		Router router = RestRouter.register(vertx, testRest);
		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(PORT);
	}

	@Test
	public void rootWithRootPathTest(TestContext context) throws IOException {

		final Async async = context.async();

		client.getNow("/order/test", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("first", body.toString());
				async.complete();
			});
		});
	}
}
