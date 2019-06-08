package com.zandero.rest;
/*

import com.zandero.rest.injection.GuiceInjectionProvider;
import com.zandero.rest.injection.SimulatedUserProvider;
import com.zandero.rest.test.TestContextInjectedRest;
import com.zandero.rest.test.handler.MyExceptionHandler;
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
public class RouteWithContextInjectionTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();


		Router router = new RestBuilder(vertx)
			                .injectWith(new GuiceInjectionProvider())
			                .addProvider(SimulatedUserProvider.class)
			                .errorHandler(MyExceptionHandler.class)
			                .register(vertx, TestContextInjectedRest.class).build();
		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void testGetRouteContext(VertxTestContext context) {



		client.get("/context/user").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Test", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "Test").end();
	}

	@Test
	public void testFailToGetRouteContext(VertxTestContext context) {



		client.get("/context/user").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("Exception: No user present!", body.toString());
				context.assertEquals(404, response.statusCode());
				async.complete();
			});
		}).end();
	}
}
*/
