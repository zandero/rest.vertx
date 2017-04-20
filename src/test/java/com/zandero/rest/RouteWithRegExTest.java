package com.zandero.rest;

import com.zandero.rest.test.TestRegExRest;
import com.zandero.rest.test.VertxTest;
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
public class RouteWithRegExTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		TestRegExRest testRest = new TestRegExRest();
		Router router = RestRouter.register(vertx, testRest);

		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(PORT);
	}

	@Test
	public void testSimpleRegEx(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/regEx/123", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("123", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testRegEx(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/regEx/ena/2/tri", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("{one=ena, two=2, three=tri}", body.toString());
				async.complete();
			});
		});
	}

}
