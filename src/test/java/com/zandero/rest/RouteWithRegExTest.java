package com.zandero.rest;
/*

import com.zandero.rest.test.TestRegExRest;
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
public class RouteWithRegExTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		TestRegExRest testRest = new TestRegExRest();
		Router router = RestRouter.register(vertx, testRest);
		router = router.mountSubRouter("/sub", router);

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void testSimpleRegEx(VertxTestContext context) {



		client.get(PORT, HOST, "/regEx/123").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("123", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testSubSimpleRegEx(VertxTestContext context) {




		client.get(PORT, HOST, "/sub/regEx/231").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("231", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testRegEx(VertxTestContext context) {



		client.get(PORT, HOST, "/regEx/1/minus/2").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("-1", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testSubRegEx(VertxTestContext context) {



		client.get(PORT, HOST, "/sub/regEx/2/minus/1").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("1", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testSimpleRegExWithMultipleVariables(VertxTestContext context) {



		client.get(PORT, HOST, "/regEx/ena/2/tri").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("{one=ena, two=2, three=tri}", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testSubSimpleRegExWithMultipleVariables(VertxTestContext context) {



		client.get(PORT, HOST, "/sub/regEx/ena/2/tri").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("{one=ena, two=2, three=tri}", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testAllButApi(VertxTestContext context) {



		client.get(PORT, HOST, "/regEx/api/a").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals(200, response.statusCode());
				context.assertEquals("api - last", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testAllButApi2(VertxTestContext context) {



		client.get(PORT, HOST, "/regEx/test").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals(200, response.statusCode());
				context.assertEquals("test - not /api", body.toString());
				async.complete();
			});
		});
	}

}
*/
