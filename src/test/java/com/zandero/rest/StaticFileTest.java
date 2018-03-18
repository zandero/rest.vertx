package com.zandero.rest;

import com.zandero.rest.test.StaticFileRest;
import com.zandero.rest.test.handler.RestNotFoundHandler;
import io.vertx.core.http.HttpServerOptions;
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
public class StaticFileTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		Router router = new RestBuilder(vertx)
			                .register(StaticFileRest.class)
			                .build();

		RestRouter.notFound(router, "rest", RestNotFoundHandler.class);

		HttpServerOptions serverOptions = new HttpServerOptions();
		serverOptions.setCompressionSupported(true);



		vertx.createHttpServer(serverOptions)
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void testGetIndex(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/docs/index.html", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("<html>\n" +
				                     "    <body>\n" +
				                     "        <p>Hello</p>\n" +
				                     "    </body>\n" +
				                     "</html>", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void fileNotFoundTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/docs/notExistent/file.html", response -> {

			context.assertEquals(404, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("", body.toString());
				async.complete();
			});
		});
	}
}
