package com.zandero.rest;
/*

import com.zandero.rest.reader.IntegerBodyReader;
import com.zandero.rest.test.TestIncompatibleReaderRest;
import com.zandero.rest.test.TestIncompatibleWriterRest;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.TestRestWithNonRestMethod;
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

*/

import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.TestRestWithNonRestMethod;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Basic GET, POST tests
 */

@ExtendWith(VertxExtension.class)
public class RestRouterTest extends VertxTest {

    @BeforeEach
    void start() {

        super.before();

        TestRest testRest = new TestRest();

        Router router = RestRouter.register(vertx,
                testRest,
                TestRestWithNonRestMethod.class);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(PORT, testContext.succeeding());
    }

    @Test
    void registerTestRest(Vertx vertx, VertxTestContext testContext) {

        client.get(PORT, HOST, "/test/echo")
                .as(BodyCodec.string())
                .send(testContext.succeeding(response -> testContext.verify(() -> {
                    assertEquals("Hello world!", response.body());
                    testContext.completeNow();
                })));
    }

    @Test
    void jaxResponseTest(Vertx vertx, VertxTestContext testContext) {

        client.get(PORT, HOST, "/test/jax")
                .as(BodyCodec.string())
                .send(testContext.succeeding(response -> testContext.verify(() -> {
                    assertEquals(202, response.statusCode());
                    assertEquals("Test", response.getHeader("X-Test"));
                    assertEquals("\"Hello\"", response.body()); // produces JSON ... so quotes are correct
                    testContext.completeNow();
                })));
    }
}
/*
	@Test
	public void pathMatchTest(TestContext context) {

		final Async async = context.async();

		client.getNow("/test/match/hello/world", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("\"hello/world\"", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void pathMatch2Test(TestContext context) {

		final Async async = context.async();

		client.getNow("/test/match2/hello/world", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("\"hello/world\"", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testNonStringParameters(TestContext context) {

		final Async async = context.async();

		client.getNow("/test/mix/2/true", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("\"2/true\"", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testNonStringParameters2(TestContext context) {

		final Async async = context.async();

		client.getNow("/test/mix2/2/a", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("\"2/a\"", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testJsonPost(TestContext context) throws IOException {

		final Async async = context.async();

		Dummy dummy = new Dummy("hello", "world");
		String json = JsonUtils.toJson(dummy);


		*/
/*StringEntity input = new StringEntity(json);
		input.setContentType("application/json");

		HttpPost request = (HttpPost) HttpUtils.post("http://localhost:4444/test/json", null, null, input, null);
		HttpUtils.execute(request);
*//*

		client.post("/test/json/post", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
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

			response.bodyHandler(body -> {
				context.assertEquals("http://localhost:4444/test/context/path", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void nullTest(TestContext context) {

		final Async async = context.async();
		client.getNow("/test/context/null", response -> {

			context.assertEquals(200, response.statusCode());
			response.bodyHandler(bodyHandler -> {
				                     String body = bodyHandler.getString(0, bodyHandler.length());
				                     context.assertEquals("", body);
			                     });
			async.complete();
		});
	}

	@Test
	public void invalidReaderRegistrationTest() {

		try {
			RestRouter.getReaders().register(List.class, IntegerBodyReader.class);
			fail();
		} catch (Exception e) {
			assertEquals("Incompatible types: 'interface java.util.List' and: 'class java.lang.Integer' using: 'class com.zandero.rest.reader.IntegerBodyReader'!", e.getMessage());
		}
	}

	@Test
	public void incompatibleReaderTypeTest() {

		try {
			RestRouter.register(vertx, TestIncompatibleReaderRest.class);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("POST /incompatible/ouch - Parameter type: 'class java.lang.String' " +
					             "not matching reader type: 'class java.lang.Integer' in: 'class com.zandero.rest.reader.IntegerBodyReader'!", e.getMessage());
		}
	}

	@Test
	public void incompatibleWriterTypeTest() {

		try {
			RestRouter.register(vertx, TestIncompatibleWriterRest.class);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("GET /incompatible/ouch - Response type: 'class java.lang.String' " +
					             "not matching writer type: 'class com.zandero.rest.test.json.Dummy' in: 'class com.zandero.rest.writer.TestDummyWriter'!", e.getMessage());
		}
	}

	@Test
	public void testRestWithNonAnnotatedMethod(TestContext context) {

		final Async async = context.async();
		client.getNow("/mixed/echo", response -> {

			response.bodyHandler(bodyHandler -> {
				String body = bodyHandler.getString(0, bodyHandler.length());
				context.assertEquals("hello", body);
				context.assertEquals(200, response.statusCode());
			});
			async.complete();
		});
	}
}*/
