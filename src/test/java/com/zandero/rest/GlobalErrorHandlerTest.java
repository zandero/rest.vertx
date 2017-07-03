package com.zandero.rest;

import com.zandero.rest.test.ErrorThrowingRest;
import com.zandero.rest.test.ErrorThrowingRest2;
import com.zandero.rest.test.VertxTest;
import com.zandero.rest.test.handler.HandleRestException;
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

		ErrorThrowingRest handled = new ErrorThrowingRest();
		ErrorThrowingRest2 unhandled = new ErrorThrowingRest2();

		Router router = RestRouter.register(vertx, handled, unhandled);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void throwOuchExceptionTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/ouch", response -> {

			context.assertEquals(406, response.statusCode());

			response.handler(body -> {
				context.assertEquals("{\"message\":\"Ouch!\",\"code\":406}", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void throwBangExceptionTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/bang", response -> {

			context.assertEquals(400, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Huh this produced an error: 'Bang!'", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionTest(TestContext context) {

		// call and check response
		final Async async = context.async();
		RestRouter.errorHandler(HandleRestException.class);

		client.getNow("/throw/unhandled", response -> {

			context.assertEquals(400, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Huh this produced an error: 'KABUM!'", body.toString());
				async.complete();
			});
		});
	}
}
