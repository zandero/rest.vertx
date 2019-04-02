package com.zandero.rest;
/*

import com.zandero.rest.test.TestQueryRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
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
public class RouteWithQueryTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before();

		TestQueryRest testRest = new TestQueryRest();

		Router router = RestRouter.register(vertx, testRest);
		router.mountSubRouter("/sub", router);
		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void testAdd(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/query/add?one=1&two=2", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("3", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testSubAdd(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/sub/query/add?one=1&two=2", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("3", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void additionalParametersTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/query/add?one=3&two=5&three=3", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("8", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void missingParametersTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/query/add?two=5&three=3", response -> {

			context.assertEquals(400, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Missing @QueryParam(\"one\") for: /query/add", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void queryTypeMismatchTest(TestContext context) {

		final Async async = context.async();

		client.getNow("/query/add?one=A&two=2", response -> {

			context.assertEquals(400, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Invalid parameter type for: @QueryParam(\"one\") for: /query/add, expected: int, but got: String -> java.lang.NumberFormatException: For input string: \"A\"",
				                     body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void queryNegativeTest(TestContext context) {

		final Async async = context.async();

		client.getNow("/query/invert?negative=true&value=2.4", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("-2.4", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void queryNegativeTest2(TestContext context) {

		final Async async = context.async();

		client.getNow("/query/invert?negative=false&value=2.4", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("2.4", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void echoDummyJsonTest(TestContext context) {

		final Async async = context.async();
		Dummy dummy = new Dummy("one", "dude");
		String json = JsonUtils.toJson(dummy);

		client.getNow("/query/json?dummy=" + json, response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals(json, body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void emptyQueryParamTest(TestContext context) {

		final Async async = context.async();
		client.getNow("/query/empty?empty", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("true", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void emptyQueryParamMissingTest(TestContext context) {

		final Async async = context.async();
		client.getNow("/query/empty", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("true", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void decodeQueryParamMissingTest(TestContext context) {

		final Async async = context.async();
		client.getNow("/query/decode?query=test+%7Bhello%7D", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("test {hello}", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void decodeQueryWithPlus(TestContext context) {

		final Async async = context.async();
		client.getNow("/query/decode?query=hello+world", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("hello world", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void rawQueryParamMissingTest(TestContext context) {

		final Async async = context.async();
		client.getNow("/query/decode?original=test+%7Bhello%7D", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("test+%7Bhello%7D", body.toString());
				async.complete();
			});
		});
	}
}
*/
