package com.zandero.rest;
/*

import com.zandero.rest.test.ErrorThrowingRest2;
import com.zandero.rest.test.handler.MyGlobalExceptionHandler;
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
public class RouteErrorHandler2Test extends VertxTest {

	MyGlobalExceptionHandler handler = new MyGlobalExceptionHandler("Big");

	@Before
	public void start(TestContext context) {

		super.before();

		Router router = new RestBuilder(vertx)
			                .register(new ErrorThrowingRest2())
			                .errorHandler(handler)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void throwUnhandledExceptionTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/throw/myHandler", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("Big auch!", body.toString());
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}
}
*/
