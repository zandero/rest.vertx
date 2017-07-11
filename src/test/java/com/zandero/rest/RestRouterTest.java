package com.zandero.rest;

import com.zandero.rest.reader.IntegerBodyReader;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Basic GET, POST tests
 */
@RunWith(VertxUnitRunner.class)
public class RestRouterTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		TestRest testRest = new TestRest();

		Router router = RestRouter.register(vertx, testRest);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void registerTestRest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/test/echo", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Hello world!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void jaxResponseTest(TestContext context) {

		final Async async = context.async();

		client.getNow("/test/jax", response -> {

			context.assertEquals(202, response.statusCode());
			context.assertEquals("Test", response.getHeader("X-Test"));

			response.handler(body -> {
				context.assertEquals("Hello", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void pathMatchTest(TestContext context) {

		final Async async = context.async();

		client.getNow("/test/match/hello/world", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("hello/world", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void pathMatch2Test(TestContext context) {

		final Async async = context.async();

		client.getNow("/test/match2/hello/world", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("hello/world", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testNonStringParameters(TestContext context) {

		final Async async = context.async();

		client.getNow("/test/mix/2/true", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("2/true", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testNonStringParameters2(TestContext context) {

		final Async async = context.async();

		client.getNow("/test/mix2/2/a", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("2/a", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testJsonPost(TestContext context) throws IOException {

		final Async async = context.async();

		Dummy dummy = new Dummy("hello", "world");
		String json = JsonUtils.toJson(dummy);


		/*StringEntity input = new StringEntity(json);
		input.setContentType("application/json");

		HttpPost request = (HttpPost) HttpUtils.post("http://localhost:4444/test/json", null, null, input, null);
		HttpUtils.execute(request);
*/
		client.post("/test/json/post", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("{\"name\":\"Received-hello\",\"value\":\"Received-world\"}", body.toString());
				async.complete();
			});
		}).putHeader("Content-Type", "application/json").end(json);

		//	async.complete();
	}

	@Test
	public void contextTest(TestContext context) {

		final Async async = context.async();
		client.getNow("/test/context/path", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("http://localhost:4444/test/context/path", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void invalidReaderRegistrationTest() {

		try {

			RestRouter.getReaders().register(List.class, IntegerBodyReader.class);
			fail();
		} catch (Exception e) {
			assertEquals("Incompatible types: 'interface java.util.List' and: 'class java.lang.Integer' using: 'class com.zandero.rest.reader.IntegerBodyReader'", e.getMessage());
		}
	}
}