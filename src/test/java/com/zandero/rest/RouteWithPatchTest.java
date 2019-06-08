package com.zandero.rest;
/*

import com.zandero.rest.test.TestPatchRest;
import com.zandero.rest.test.TestPathRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.http.HttpMethod;
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
public class RouteWithPatchTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		Router router = RestRouter.register(vertx, TestPatchRest.class);

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void testCustomInput(VertxTestContext context) {



		Dummy json = new Dummy("test", "me");

		client.request(HttpMethod.PATCH,"/patch/it").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("<custom>Received-test=Received-me</custom>", body.toString()); // returns sorted list of unique words
				async.complete();
			});
		}).end(JsonUtils.toJson(json));
	}
}
*/
