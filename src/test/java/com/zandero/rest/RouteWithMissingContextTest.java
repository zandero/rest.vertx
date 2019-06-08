package com.zandero.rest;
/*

import com.zandero.rest.test.TestContextRest;
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
public class RouteWithMissingContextTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		TestContextRest testRest = new TestContextRest();
		Router router = RestRouter.register(vertx, testRest);

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void missingContextTest(VertxTestContext context) {



		client.get(PORT, HOST, "/context/custom").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(400, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Can't provide @Context of type: class com.zandero.rest.test.json.Dummy", body.toString());
				async.complete();
			});
		});
	}
}
*/
