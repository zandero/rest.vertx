package com.zandero.rest;
/*

import com.zandero.rest.test.ErrorThrowingRest2;
import com.zandero.rest.test.handler.BaseExceptionHandler;
import com.zandero.rest.test.handler.InheritedBaseExceptionHandler;
import com.zandero.rest.test.handler.InheritedFromInheritedExceptionHandler;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

*/
/**
 *
 *//*

@RunWith(VertxUnitRunner.class)
public class RouteExceptionHandlerTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before();

		Router router = new RestBuilder(vertx)
			                .register(ErrorThrowingRest2.class)
			                .errorHandler(new InheritedFromInheritedExceptionHandler())
			                .errorHandler(InheritedBaseExceptionHandler.class,
			                              BaseExceptionHandler.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void throwOne(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/exception/one", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("BaseExceptionHandler: BASE: first", body.toString());
				context.assertEquals(500, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwTwo(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/exception/two", response -> {


			response.bodyHandler(body -> {
				context.assertEquals("InheritedBaseExceptionHandler: BASE: INHERITED: second", body.toString());
				context.assertEquals(500, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwThree(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/exception/three", response -> {


			response.bodyHandler(body -> {
				context.assertEquals("InheritedFromInheritedExceptionHandler: BASE: INHERITED: INHERITED FROM: third", body.toString());
				context.assertEquals(500, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwFour(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/exception/four", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("com.zandero.rest.test.handler.MyExceptionClass", body.toString());
				context.assertEquals(500, response.statusCode());
				async.complete();
			});
		});
	}
}
*/
