package com.zandero.rest;

import com.zandero.rest.reader.CustomBodyReader;
import com.zandero.rest.test.TestReaderRest;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.test.json.ExtendedDummy;
import com.zandero.rest.test.reader.DummyBodyReader;
import com.zandero.rest.test.reader.ExtendedDummyBodyReader;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class CustomReaderTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		// init before tests
		RestRouter.getReaders().clear();
		TestReaderRest testRest = new TestReaderRest();

		Router router = RestRouter.register(vertx, testRest);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void testCustomInput(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.post("/read/custom", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("brown,dog,fox,jumps,over,quick,red,the", body.toString()); // returns sorted list of unique words
				async.complete();
			});
		}).end("The quick brown fox jumps over the red dog!");
	}

	@Test
	public void testCustomInput_2(TestContext context) {

		TestRest testRest = new TestRest();

		RestRouter.getReaders()
		          .register(List.class, CustomBodyReader.class); // all arguments that are List<> go through this reader ... (reader returns List<String> as output)

		Router router = RestRouter.register(vertx, testRest);
		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);

		// call and check response
		final Async async = context.async();

		client.post("/read/registered", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("brown,dog,fox,jumps,over,quick,red,the", body.toString()); // returns sorted list of unique words
				async.complete();
			});
		}).end("The quick brown fox jumps over the red dog!");
	}


	@Test
	public void testExtendedReader(TestContext context) {

		RestRouter.getReaders().register(Dummy.class, DummyBodyReader.class);
		RestRouter.getReaders().register(ExtendedDummy.class, ExtendedDummyBodyReader.class);

		// check if correct reader is used
		final Async async = context.async();
		client.post("/read/normal/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("one=dummy", body.toString()); // returns sorted list of unique words
				async.complete();
			});
		}).putHeader("Content-Type", "application/json")
		      .end(JsonUtils.toJson(new Dummy("one", "dummy")));

		// 2nd send extended dummy to same REST
		client.post("/read/normal/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("one=dummy", body.toString()); // returns sorted list of unique words
				async.complete();
			});
		}).putHeader("Content-Type", "application/json")
		      .end(JsonUtils.toJson(new ExtendedDummy("one", "dummy", "extra")));

		// 3rd send extended dummy to extended REST
		client.post("/read/extended/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("one=dummy (extra)", body.toString()); // returns sorted list of unique words
				async.complete();
			});
		}).putHeader("Content-Type", "application/json")
		      .end(JsonUtils.toJson(new ExtendedDummy("one", "dummy", "extra")));

		// 4th send normal dummy to extended REST
		client.post("/read/extended/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("one=dummy (null)", body.toString()); // returns sorted list of unique words
				async.complete();
			});
		}).putHeader("Content-Type", "application/json")
		      .end(JsonUtils.toJson(new Dummy("one", "dummy")));
	}

	@Test
	public void extendedContentTypeTest(TestContext context) {

		RestRouter.getReaders().register(Dummy.class, DummyBodyReader.class);

		final Async async = context.async();
		client.post("/read/normal/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("one=dummy", body.toString()); // returns sorted list of unique words
				async.complete();
			});
		}).putHeader("Content-Type", "application/json;charset=UTF-8")
		      .end(JsonUtils.toJson(new Dummy("one", "dummy")));
	}

	@Test
	public void extendedContentTypeByMediaType(TestContext context) {

		RestRouter.getReaders().register("application/json", DummyBodyReader.class);

		final Async async = context.async();
		client.post("/read/normal/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("one=dummy", body.toString()); // returns sorted list of unique words
				async.complete();
			});
		}).putHeader("Content-Type", "application/json;charset=UTF-8")
		      .end(JsonUtils.toJson(new Dummy("one", "dummy")));
	}
}
