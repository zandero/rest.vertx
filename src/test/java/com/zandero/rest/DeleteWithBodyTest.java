package com.zandero.rest;
/*

import com.zandero.rest.test.TestPostRest;
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
public class DeleteWithBodyTest extends VertxTest {

	@Before
	public void startUp(TestContext context) {

		super.before();

		Router router = RestRouter.register(vertx, TestPostRest.class);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void testDelete(TestContext context) {

		// call and check response
		final Async async = context.async();

		Dummy dummy = new Dummy("hello", "world");
		String json = JsonUtils.toJson(dummy);

		client.delete("/post/json", response -> {

			context.assertEquals(200, response.statusCode());
			context.assertEquals("application/json", response.getHeader("Content-Type"));

			response.bodyHandler(body -> {
				context.assertEquals("<custom>Received-hello=Received-world</custom>", body.toString());
				async.complete();
			});
		}).putHeader("Content-Type", "application/json").end(json);
	}
}
*/
