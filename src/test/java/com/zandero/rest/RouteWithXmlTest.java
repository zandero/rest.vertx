package com.zandero.rest;

import com.zandero.rest.test.TestWithXmlRest;
import com.zandero.rest.test.json.User;
import com.zandero.rest.writer.MyXmlWriter;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RouteWithXmlTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		Router router = RestRouter.register(vertx, TestWithXmlRest.class);
		RestRouter.getWriters().register(User.class, MyXmlWriter.class);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void textXml(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/xml/test", response -> {

			context.assertEquals(200, response.statusCode());

			String header = response.getHeader("Content-Type");
			context.assertEquals("application/xml", header);

			header = response.getHeader("Cache-Control");
			context.assertEquals("private,no-cache,no-store", header);

			response.bodyHandler(body -> {
				context.assertEquals("<u name=\"test\" />", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void textXmlWriterAddsHeader(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/xml/test2", response -> {

			context.assertEquals(200, response.statusCode());

			String header = response.getHeader("Content-Type");
			context.assertEquals("text/xml", header);

			response.bodyHandler(body -> {
				context.assertEquals("<u name=\"test\" />", body.toString());
				async.complete();
			});
		});
	}
}
