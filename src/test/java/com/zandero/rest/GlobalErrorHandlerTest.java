package com.zandero.rest;

import com.zandero.rest.test.ErrorThrowingRest;
import com.zandero.rest.test.ErrorThrowingRest2;
import com.zandero.rest.test.handler.IllegalArgumentExceptionHandler;
import com.zandero.rest.test.handler.JsonExceptionHandler;
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

		super.before();

		ErrorThrowingRest handled = new ErrorThrowingRest();
		ErrorThrowingRest2 unhandled = new ErrorThrowingRest2();

		Router router = RestRouter.register(vertx, unhandled, handled);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void throwOuchExceptionTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/ouch", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("{\"message\":\"Ouch!\",\"code\":406}", body.toString()); // JsonExceptionWriter
				context.assertEquals(406, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwBangExceptionTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/bang", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("Bang!", body.toString()); // Generic exception writer
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionTest(TestContext context) {

		// call and check response
		final Async async = context.async();
		RestRouter.getExceptionHandlers().register(IllegalArgumentExceptionHandler.class);

		client.getNow("/throw/unhandled", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'KABUM!'", body.toString());
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwDifferentExceptionTestOne(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/multi/one", response -> { // throws NotAllowedException

			response.bodyHandler(body -> {
				context.assertEquals("Exception: HTTP 405 Method Not Allowed", body.toString()); // ExceptionWriter kicked in
				context.assertEquals(405, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwDifferentExceptionTestTwo(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/multi/two", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'Bang!'", body.toString()); // IllegalArgumentExceptionWriter kicked in
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void multipleGlobalErrorHandlersTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		RestRouter.getExceptionHandlers().register(new JsonExceptionHandler());

		client.getNow("/throw/multi/four", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("Exception: ADIOS!", body.toString()); // ExceptionWriter kicked in
				context.assertEquals(500, response.statusCode()); //
				async.complete();
			});
		});
	}

	@Test
	public void multipleGlobalErrorHandlersTest2(TestContext context) {
		final Async async = context.async();
		client.getNow("/throw/multi/one", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("Exception: HTTP 405 Method Not Allowed", body.toString()); // ExceptionWriter kicked in
				context.assertEquals(405, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void multipleGlobalErrorHandlersTest3(TestContext context) {
		final Async async = context.async();
		client.getNow("/throw/multi/three", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'WHAT!'", body.toString()); // ExceptionWriter kicked in
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}
}
