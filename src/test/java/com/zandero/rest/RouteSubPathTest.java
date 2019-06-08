package com.zandero.rest;
/*

import com.zandero.rest.test.TestPathRest;
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
public class RouteSubPathTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		TestPathRest testRest = new TestPathRest();

		Router router = RestRouter.register(vertx, testRest);
		router.mountSubRouter("/sub", router);

		vertx.createHttpServer()
			.requestHandler(router)
			.listen(PORT);
	}

	@Test
	public void rootWithRootPathTest(VertxTestContext context) throws IOException {

		final Async async = context.async();

		client.get(PORT, HOST, "/query/echo/this").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("querythis", body.toString());
				async.complete();
			});
		});

		final Async async2 = context.async();
		client.get(PORT, HOST, "/sub/query/echo/this").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("querythis", body.toString());
				async2.complete();
			});
		});
	}

	@Test
	public void rootWithRootPathTest2(VertxTestContext context) throws IOException {

		final Async async = context.async();

		client.get(PORT, HOST, "/this/echo/query").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("thisquery", body.toString());
				async.complete();
			});
		});

		final Async async2 = context.async();
		client.get(PORT, HOST, "/sub/this/echo/query").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("thisquery", body.toString());
				async2.complete();
			});
		});
	}

	@Test
	public void rootWithoutPathTest(VertxTestContext context) throws IOException {

		final Async async = context.async();

		client.get(PORT, HOST, "/this").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("this", body.toString());
				async.complete();
			});
		});

		final Async async2 = context.async();
		client.get(PORT, HOST, "/sub/this").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("this", body.toString());
				async2.complete();
			});
		});
	}
}
*/
