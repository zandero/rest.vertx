package com.zandero.rest;
/*

import com.zandero.rest.test.TestQueryRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
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
public class RouteWithQueryTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		TestQueryRest testRest = new TestQueryRest();

		Router router = RestRouter.register(vertx, testRest);
		router.mountSubRouter("/sub", router);
		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void testAdd(VertxTestContext context) {



		client.get(PORT, HOST, "/query/add?one=1&two=2").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("3", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testSubAdd(VertxTestContext context) {



		client.get(PORT, HOST, "/sub/query/add?one=1&two=2").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("3", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void additionalParametersTest(VertxTestContext context) {



		client.get(PORT, HOST, "/query/add?one=3&two=5&three=3").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("8", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void missingParametersTest(VertxTestContext context) {



		client.get(PORT, HOST, "/query/add?two=5&three=3").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(400, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Missing @QueryParam(\"one\") for: /query/add", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void queryTypeMismatchTest(VertxTestContext context) {

		final Async async = context.async();

		client.get(PORT, HOST, "/query/add?one=A&two=2").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(400, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Invalid parameter type for: @QueryParam(\"one\") for: /query/add, expected: int, but got: String -> java.lang.NumberFormatException: For input string: \"A\"",
				                     body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void queryNegativeTest(VertxTestContext context) {

		final Async async = context.async();

		client.get(PORT, HOST, "/query/invert?negative=true&value=2.4").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("-2.4", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void queryNegativeTest2(VertxTestContext context) {

		final Async async = context.async();

		client.get(PORT, HOST, "/query/invert?negative=false&value=2.4").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("2.4", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void echoDummyJsonTest(VertxTestContext context) {

		final Async async = context.async();
		Dummy dummy = new Dummy("one", "dude");
		String json = JsonUtils.toJson(dummy);

		client.get(PORT, HOST, "/query/json?dummy=" + json.as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals(json, body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void emptyQueryParamTest(VertxTestContext context) {

		final Async async = context.async();
		client.get(PORT, HOST, "/query/empty?empty").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("true", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void emptyQueryParamMissingTest(VertxTestContext context) {

		final Async async = context.async();
		client.get(PORT, HOST, "/query/empty").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("true", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void decodeQueryParamMissingTest(VertxTestContext context) {

		final Async async = context.async();
		client.get(PORT, HOST, "/query/decode?query=test+%7Bhello%7D").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("test {hello}", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void decodeQueryWithPlus(VertxTestContext context) {

		final Async async = context.async();
		client.get(PORT, HOST, "/query/decode?query=hello+world").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("hello world", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void rawQueryParamMissingTest(VertxTestContext context) {

		final Async async = context.async();
		client.get(PORT, HOST, "/query/decode?original=test+%7Bhello%7D").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("test+%7Bhello%7D", body.toString());
				async.complete();
			});
		});
	}
}
*/
