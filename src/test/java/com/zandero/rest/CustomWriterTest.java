package com.zandero.rest;

import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.VertxTest;
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
public class CustomWriterTest extends VertxTest {

	@Test
	public void testCustomOutput(TestContext context) {

		TestRest testRest = new TestRest();

		Router router = RestRouter.register(vertx, testRest);
		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(PORT);

		// call and check response
		final Async async = context.async();

		client.getNow("/test/custom", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("<custom>CUSTOM</custom>", body.toString());
				async.complete();
			});
		});
	}

}
