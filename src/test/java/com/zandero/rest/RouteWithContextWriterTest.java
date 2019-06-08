package com.zandero.rest;
/*

import com.zandero.rest.test.TestWithXmlRest;
import com.zandero.rest.test.json.User;
import com.zandero.rest.writer.MyXmlWriter;
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
public class RouteWithContextWriterTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		Router router = RestRouter.register(vertx, TestWithXmlRest.class);
		RestRouter.getWriters().register(User.class, MyXmlWriter.class);

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void textXml(VertxTestContext context) {



		client.get(PORT, HOST, "/xml/test").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			String header = response.getHeader("Content-Type");
			context.assertEquals("text/xml", header); // writer always overrides definition

			header = response.getHeader("Cache-Control");
			context.assertEquals("private,no-cache,no-store", header);

			response.bodyHandler(body -> {
				context.assertEquals("<u name=\"test\" />", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void textXmlWriterAddsHeader(VertxTestContext context) {



		client.get(PORT, HOST, "/xml/test2").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

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
*/
