package com.zandero.rest;
/*

import com.zandero.rest.test.TestEchoRest;
import io.vertx.core.http.HttpMethod;
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
public class RouteWithTraceTest extends VertxTest {

	@BeforeAll
	static void start() {
		super.before();
	}

	@Test
	public void testTrace(VertxTestContext context) {

		Router router = RestRouter.register(vertx, TestEchoRest.class);

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);



		client.request(HttpMethod.TRACE, "/rest/echo").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("trace", body.toString()); // returns sorted list of unique words
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		}).end();
	}
}
*/
