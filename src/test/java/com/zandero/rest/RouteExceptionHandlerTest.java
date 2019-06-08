package com.zandero.rest;
/*

import com.zandero.rest.test.ErrorThrowingRest2;
import com.zandero.rest.test.handler.BaseExceptionHandler;
import com.zandero.rest.test.handler.InheritedBaseExceptionHandler;
import com.zandero.rest.test.handler.InheritedFromInheritedExceptionHandler;
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
public class RouteExceptionHandlerTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		Router router = new RestBuilder(vertx)
			                .register(ErrorThrowingRest2.class)
			                .errorHandler(new InheritedFromInheritedExceptionHandler())
			                .errorHandler(InheritedBaseExceptionHandler.class,
			                              BaseExceptionHandler.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void throwOne(VertxTestContext context) {



		client.get(PORT, HOST, "/throw/exception/one").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("BaseExceptionHandler: BASE: first", body.toString());
				context.assertEquals(500, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwTwo(VertxTestContext context) {



		client.get(PORT, HOST, "/throw/exception/two").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->


			response.bodyHandler(body -> {
				context.assertEquals("InheritedBaseExceptionHandler: BASE: INHERITED: second", body.toString());
				context.assertEquals(500, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwThree(VertxTestContext context) {



		client.get(PORT, HOST, "/throw/exception/three").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->


			response.bodyHandler(body -> {
				context.assertEquals("InheritedFromInheritedExceptionHandler: BASE: INHERITED: INHERITED FROM: third", body.toString());
				context.assertEquals(500, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void throwFour(VertxTestContext context) {



		client.get(PORT, HOST, "/throw/exception/four").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("com.zandero.rest.test.handler.MyExceptionClass", body.toString());
				context.assertEquals(500, response.statusCode());
				async.complete();
			});
		});
	}
}
*/
