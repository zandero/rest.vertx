package com.zandero.rest;
/*

import com.zandero.rest.test.TestOrderRest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.VertxTestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

*/
/**
 *
 *//*

@ExtendWith(VertxExtension.class)
public class RouteOrderTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		TestOrderRest testRest = new TestOrderRest();

		Router router = RestRouter.register(vertx, testRest);
		vertx.createHttpServer()
			.requestHandler(router)
			.listen(PORT);
	}

	@Test
	public void rootWithRootPathTest(VertxTestContext context) throws IOException {

		final Async async = context.async();

		client.get(PORT, HOST, "/order/test").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("first", body.toString());
				async.complete();
			});
		});
	}
}
*/
