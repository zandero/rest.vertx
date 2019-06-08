package com.zandero.rest;
/*

import com.zandero.rest.test.TestEnumRest;
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
public class RouteWithEnumsTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		Router router = RestRouter.register(vertx, TestEnumRest.class);
		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void valueOfTest(VertxTestContext context) {

		final Async async = context.async();

		client.get(PORT, HOST, "/enum/simple/one").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("one", body.toString());
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void fromStringTest(VertxTestContext context) {

		final Async async = context.async();

		client.get(PORT, HOST, "/enum/fromString/3").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("three", body.toString());
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		});
	}

}*/
