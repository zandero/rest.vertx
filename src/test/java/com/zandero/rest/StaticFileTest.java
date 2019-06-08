package com.zandero.rest;

/*
import com.zandero.rest.test.TestStaticFileRest;
import com.zandero.rest.test.handler.RestNotFoundHandler;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.VertxTestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

*/
/**
 *
 *//*

@ExtendWith(VertxExtension.class)
public class StaticFileTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		Router router = new RestBuilder(vertx)
				.register(TestStaticFileRest.class)
				.build();

		RestRouter.notFound(router, "rest", RestNotFoundHandler.class);

		HttpServerOptions serverOptions = new HttpServerOptions();
		serverOptions.setCompressionSupported(true);



		vertx.createHttpServer(serverOptions)
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void testGetIndex(VertxTestContext context) {



		client.get(PORT, HOST, "/docs/index.html").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("<html>\n" +
				                     "    <body>\n" +
				                     "        <p>Hello</p>\n" +
				                     "    </body>\n" +
				                     "</html>", body.toString());
				async.complete();
			});
		});
	}

	@Ignore // todo: issue #26
	@Test
	public void fileNotFoundTest(VertxTestContext context) {



		client.get(PORT, HOST, "/docs/notExistent/file.html").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(404, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("", body.toString());
				async.complete();
			});
		});
	}
}
*/
