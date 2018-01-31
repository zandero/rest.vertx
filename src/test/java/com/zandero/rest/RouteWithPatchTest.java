package com.zandero.rest;

import com.zandero.rest.test.TestPatchRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RouteWithPatchTest extends VertxTest {

	@Test
	public void testCustomInput(TestContext context) {

		Router router = RestRouter.register(vertx, TestPatchRest.class);

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);

		// call and check response
		final Async async = context.async();

		Dummy json = new Dummy("test", "me");

		client.request(HttpMethod.PATCH,"/patch/it", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("<custom>Received-test=Received-me</custom>", body.toString()); // returns sorted list of unique words
				async.complete();
			});
		}).end(JsonUtils.toJson(json));
	}
}
