package com.zandero.rest;

import com.zandero.rest.test.ErrorThrowingRest;
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
public class GlobalErrorHandlerTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		ErrorThrowingRest testRest = new ErrorThrowingRest();

		Router router = RestRouter.register(vertx, testRest);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void throwUnhandledExceptionTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/unhandled", response -> {

			context.assertEquals(400, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Ouch!", body.toString());
				async.complete();
			});
		});
	}
}
