package com.zandero.rest;
/*

import com.zandero.rest.test.TestHeaderRest;
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
public class RouteWithHeaderTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		Router router = RestRouter.register(vertx, TestHeaderRest.class);
		vertx.createHttpServer()
			.requestHandler(router)
			.listen(PORT);
	}

	@Test
	public void echoDummyJsonTest(VertxTestContext context) {

		final Async async = context.async();
		Dummy dummy = new Dummy("one", "dude");
		String json = JsonUtils.toJson(dummy);

		client.get("/header/dummy").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("one=dude", body.toString());
				async.complete();
			});
		}).putHeader("dummy", json).end();
	}

	@Test
	public void echoPostHeaderTest(VertxTestContext context) {

		final Async async = context.async();
		Dummy dummy = new Dummy("one", "dude");

		String json = JsonUtils.toJson(dummy);

		client.post("/header/dummy").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("one=dude, doing things", body.toString());
				async.complete();
			});
		}).putHeader("token", "doing").putHeader("other", "things").end(json);
	}

	@Test
	public void npeReaderTest(VertxTestContext context) {

		final Async async = context.async();

		client.get("/header/npe").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("OH SHIT!", body.toString());
				context.assertEquals(500, response.statusCode());
				async.complete();
			});
		}).putHeader("dummy", "").end();
	}
}
*/
