package com.zandero.rest;

import com.zandero.rest.test.ErrorThrowingRest;
import com.zandero.rest.test.ErrorThrowingRest2;
import com.zandero.rest.test.handler.IllegalArgumentExceptionHandler;
import com.zandero.rest.test.handler.MyExceptionHandler;
import com.zandero.rest.test.handler.MyOtherExceptionHandler;
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

		Router router = new RestBuilder(vertx)
			                    .register(ErrorThrowingRest.class, new ErrorThrowingRest2())
			                    .errorHandler(new MyExceptionHandler(),
			                    	          new IllegalArgumentExceptionHandler(),
			                                  new MyOtherExceptionHandler())
			                    .build();

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

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'KABUM!'", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionOne(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/big/one", response -> {

			context.assertEquals(406, response.statusCode()); // JsonExceptionHandler takes over

			response.bodyHandler(body -> {
				context.assertEquals("{\"message\":\"HTTP 405 Method Not Allowed\",\"code\":406}", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionTwo(TestContext context) {

		// call and check response
		final Async async = context.async();

		// FAIL IllegalArgumentExceptionHandler should handle this one
		client.getNow("/throw/big/two", response -> {

			context.assertEquals(400, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'KABUM!'", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionThree(TestContext context) {

		// call and check response
		final Async async = context.async();

		// FAIL IllegalArgumentExceptionHandler should handle this one
		client.getNow("/throw/big/three", response -> {

			context.assertEquals(400, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'KABUM!'", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionFour(TestContext context) {

		// call and check response
		final Async async = context.async();

		// FAIL MyExceptionHandler should handle this one
		client.getNow("/throw/big/four", response -> {

			context.assertEquals(400, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'KABUM!'", body.toString());
				async.complete();
			});
		});
	}
}
