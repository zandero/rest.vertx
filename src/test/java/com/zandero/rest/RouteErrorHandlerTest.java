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

		super.before();

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

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'KABUM!'", body.toString());
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionOne(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/big/one", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("{\"message\":\"HTTP 405 Method Not Allowed\",\"code\":406}", body.toString());
				context.assertEquals(406, response.statusCode()); // JsonExceptionHandler takes over
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionTwo(TestContext context) {

		// call and check response
		final Async async = context.async();

		// JsonExceptionHandler will take over
		client.getNow("/throw/big/two", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("{\"message\":\"Bang!\",\"code\":406}", body.toString());
				context.assertEquals(406, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionThree(TestContext context) {

		// call and check response
		final Async async = context.async();

		// JsonExceptionHandler
		client.getNow("/throw/big/three", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("{\"message\":\"WHAT!\",\"code\":406}", body.toString());
				context.assertEquals(406, response.statusCode());
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

			response.bodyHandler(body -> {
				context.assertEquals("{\"message\":null,\"code\":406}", body.toString());
				context.assertEquals(406, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwHandledExceptionOne(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/multi/one", response -> {

			// MyOtherExceptionHandler should handle this
			response.bodyHandler(body -> {
				context.assertEquals("Exception: HTTP 405 Method Not Allowed", body.toString());
				context.assertEquals(405, response.statusCode()); // JsonExceptionHandler takes over
				async.complete();
			});
		});
	}

	@Test
	public void throwHandledExceptionTwo(TestContext context) {

		// call and check response
		final Async async = context.async();

		// IllegalArgumentExceptionHandler should handle this one
		client.getNow("/throw/multi/two", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'Bang!'", body.toString());
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwHandledExceptionThree(TestContext context) {

		// call and check response
		final Async async = context.async();

		// IllegalArgumentExceptionHandler should handle this one
		client.getNow("/throw/multi/three", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'WHAT!'", body.toString());
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwHandledExceptionFour(TestContext context) {

		// call and check response
		final Async async = context.async();

		// MyExceptionHandler should handle this one
		client.getNow("/throw/multi/four", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("Exception: ADIOS!", body.toString());
				context.assertEquals(500, response.statusCode());
				async.complete();
			});
		});
	}
}
