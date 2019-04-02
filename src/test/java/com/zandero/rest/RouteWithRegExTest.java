package com.zandero.rest;
/*

import com.zandero.rest.test.TestRegExRest;
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
public class RouteWithRegExTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before();

		TestRegExRest testRest = new TestRegExRest();
		Router router = RestRouter.register(vertx, testRest);
		router = router.mountSubRouter("/sub", router);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void testSimpleRegEx(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/regEx/123", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("123", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testSubSimpleRegEx(TestContext context) {

		// call and check response
		final Async async = context.async();


		client.getNow("/sub/regEx/231", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("231", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testRegEx(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/regEx/1/minus/2", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("-1", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testSubRegEx(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/sub/regEx/2/minus/1", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("1", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testSimpleRegExWithMultipleVariables(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/regEx/ena/2/tri", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("{one=ena, two=2, three=tri}", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testSubSimpleRegExWithMultipleVariables(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/sub/regEx/ena/2/tri", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("{one=ena, two=2, three=tri}", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testAllButApi(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/regEx/api/a", response -> {

			response.bodyHandler(body -> {
				context.assertEquals(200, response.statusCode());
				context.assertEquals("api - last", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testAllButApi2(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/regEx/test", response -> {

			response.bodyHandler(body -> {
				context.assertEquals(200, response.statusCode());
				context.assertEquals("test - not /api", body.toString());
				async.complete();
			});
		});
	}

}
*/
