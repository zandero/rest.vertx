package com.zandero.rest;
/*

import com.zandero.rest.test.TestHeaderRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

*/
/**
 *
 *//*

@RunWith(VertxUnitRunner.class)
public class RouteWithHeaderTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before();

		Router router = RestRouter.register(vertx, TestHeaderRest.class);
		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(PORT);
	}

	@Test
	public void echoDummyJsonTest(TestContext context) {

		final Async async = context.async();
		Dummy dummy = new Dummy("one", "dude");
		String json = JsonUtils.toJson(dummy);

		client.get("/header/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("one=dude", body.toString());
				async.complete();
			});
		}).putHeader("dummy", json).end();
	}

	@Test
	public void echoPostHeaderTest(TestContext context) {

		final Async async = context.async();
		Dummy dummy = new Dummy("one", "dude");

		String json = JsonUtils.toJson(dummy);

		client.post("/header/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("one=dude, doing things", body.toString());
				async.complete();
			});
		}).putHeader("token", "doing").putHeader("other", "things").end(json);
	}

	@Test
	public void npeReaderTest(TestContext context) {

		final Async async = context.async();

		client.get("/header/npe", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("OH SHIT!", body.toString());
				context.assertEquals(500, response.statusCode());
				async.complete();
			});
		}).putHeader("dummy", "").end();
	}
}
*/
