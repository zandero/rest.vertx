package com.zandero.rest;

import com.zandero.rest.test.TestHtmlRest;
import com.zandero.rest.test.TestPostRest;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.TestCustomWriter;
import com.zandero.rest.writer.TestDummyWriter;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class CustomWriterTest extends VertxTest {

	@Before
	public void setUp(TestContext context) {

		super.before(context);

		Router router = RestRouter.register(vertx,
		                                    TestRest.class,
		                                    TestHtmlRest.class,
		                                    TestPostRest.class);
		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void testCustomOutput(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/test/custom", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("<custom>CUSTOM</custom>", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testRegisteredContentTypeWriterOutput(TestContext context) {

		RestRouter.getWriters().register(MediaType.TEXT_HTML, TestCustomWriter.class); // bind media type to this writer

		final Async async = context.async();

		client.getNow("/html/body", response -> {

			context.assertEquals(200, response.statusCode());
			context.assertEquals(MediaType.TEXT_HTML, response.getHeader("Content-Type"));

			response.bodyHandler(body -> {
				context.assertEquals("<custom>body</custom>", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testProducesOnExperimentalGet(TestContext context) {

		RestRouter.getWriters().register(MediaType.TEXT_HTML, TestCustomWriter.class); // bind media type to this writer

		final Async async = context.async();

		client.getNow("/html/head", response -> {

			context.assertEquals(200, response.statusCode());
			context.assertEquals(MediaType.TEXT_HTML, response.getHeader("Content-Type"));

			response.bodyHandler(body -> {
				context.assertEquals("<custom>head</custom>", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testRegisteredClassTypeWriterOutput(TestContext context) {

		RestRouter.getWriters().register(Dummy.class, TestDummyWriter.class); // bind media type to this writer

		final Async async = context.async();

		Dummy dummy = new Dummy("hello", "world");
		String json = JsonUtils.toJson(dummy);

		client.post("/post/json", response -> {

			context.assertEquals(200, response.statusCode());
			context.assertEquals("application/json;charset=utf-8", response.getHeader("Content-Type"));

			response.bodyHandler(body -> {
				context.assertEquals("<custom>Received-hello=Received-world</custom>", body.toString());
				async.complete();
			});
		}).putHeader("Content-Type", "application/json").end(json);
	}

	@Test
	public void testRegisteredClassTypeReaderOutput(TestContext context) {

		final Async async = context.async();

		Dummy dummy = new Dummy("hello", "world");
		String json = JsonUtils.toJson(dummy);

		client.put("/post/json", response -> {

			context.assertEquals(200, response.statusCode());
			context.assertEquals(MediaType.APPLICATION_JSON, response.getHeader("Content-Type"));

			response.bodyHandler(body -> {
				context.assertEquals("<custom>Received-hello=Received-world</custom>", body.toString());
				async.complete();
			});
		}).putHeader("Content-Type", "application/json").end(json);
	}
}
