package com.zandero.rest;

import com.zandero.rest.test.ErrorThrowingRest2;
import com.zandero.rest.test.handler.IllegalArgumentExceptionHandler;
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
public class RouteErrorHandlerTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		ErrorThrowingRest2 unhandled = new ErrorThrowingRest2();

		Router router = new RestBuilder(vertx)
			                    .register(unhandled)
			                    .errorHandler(new IllegalArgumentExceptionHandler())
			                    .build();

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void throwUnhandledExceptionTest(TestContext context) {

		// call and check response
		final Async async = context.async();
		RestRouter.getExceptionHandlers().register(IllegalArgumentExceptionHandler.class);

		client.getNow("/throw/unhandled", response -> {

			context.assertEquals(400, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Huh this produced an error: 'KABUM!'", body.toString());
				async.complete();
			});
		});
	}
}
