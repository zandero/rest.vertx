package com.zandero.rest;
/*

import com.zandero.rest.test.ErrorThrowingRest;
import com.zandero.rest.test.ErrorThrowingRest2;
import com.zandero.rest.test.handler.IllegalArgumentExceptionHandler;
import com.zandero.rest.test.handler.MyExceptionHandler;
import com.zandero.rest.test.handler.MyOtherExceptionHandler;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.VertxTestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

*/
/**
 *
 *//*

@ExtendWith(VertxExtension.class)
public class RouteErrorHandlerTest extends VertxTest {

	@BeforeAll
	static void start() {

		before();

		Router router = new RestBuilder(vertx)
			                .register(ErrorThrowingRest.class, new ErrorThrowingRest2())
			                .errorHandler(new MyExceptionHandler(),
			                              new IllegalArgumentExceptionHandler(),
			                              new MyOtherExceptionHandler())
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void throwUnhandledExceptionTest(VertxTestContext context) {



		client.get(PORT, HOST, "/throw/unhandled").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'KABUM!'", body.toString());
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionOne(VertxTestContext context) {



		client.get(PORT, HOST, "/throw/big/one").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("{\"message\":\"HTTP 405 Method Not Allowed\",\"code\":406}", body.toString());
				context.assertEquals(406, response.statusCode()); // JsonExceptionHandler takes over
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionTwo(VertxTestContext context) {



		// JsonExceptionHandler will take over
		client.get(PORT, HOST, "/throw/big/two").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("{\"message\":\"Bang!\",\"code\":406}", body.toString());
				context.assertEquals(406, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionThree(VertxTestContext context) {



		// JsonExceptionHandler
		client.get(PORT, HOST, "/throw/big/three").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("{\"message\":\"WHAT!\",\"code\":406}", body.toString());
				context.assertEquals(406, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwUnhandledExceptionFour(VertxTestContext context) {



		// FAIL MyExceptionHandler should handle this one
		client.get(PORT, HOST, "/throw/big/four").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("{\"message\":null,\"code\":406}", body.toString());
				context.assertEquals(406, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwHandledExceptionOne(VertxTestContext context) {



		client.get(PORT, HOST, "/throw/multi/one").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			// MyOtherExceptionHandler should handle this
			response.bodyHandler(body -> {
				context.assertEquals("Exception: HTTP 405 Method Not Allowed", body.toString());
				context.assertEquals(405, response.statusCode()); // JsonExceptionHandler takes over
				async.complete();
			});
		});
	}

	@Test
	public void throwHandledExceptionTwo(VertxTestContext context) {



		// IllegalArgumentExceptionHandler should handle this one
		client.get(PORT, HOST, "/throw/multi/two").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'Bang!'", body.toString());
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwHandledExceptionThree(VertxTestContext context) {



		// IllegalArgumentExceptionHandler should handle this one
		client.get(PORT, HOST, "/throw/multi/three").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("Huh this produced an error: 'WHAT!'", body.toString());
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwHandledExceptionFour(VertxTestContext context) {



		// MyExceptionHandler should handle this one
		client.get(PORT, HOST, "/throw/multi/four").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("Exception: ADIOS!", body.toString());
				context.assertEquals(500, response.statusCode());
				async.complete();
			});
		});
	}
}
*/
