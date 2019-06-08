package com.zandero.rest;
/*

import com.zandero.rest.test.TestContextRest;
import com.zandero.rest.test.data.TokenProvider;
import com.zandero.rest.test.json.Dummy;
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
public class RouteWithContextProviderTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		TestContextRest testRest = new TestContextRest();

		Router router = RestRouter.register(vertx, testRest);
		//RestRouter.provide(router, TokenProvider.class);

		RestRouter.addProvider(Dummy.class, request -> new Dummy("test", "name"));
		RestRouter.addProvider(TokenProvider.class);

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void pushContextTest(VertxTestContext context) {



		client.get(PORT, HOST, "/context/custom").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("{\"name\":\"test\",\"value\":\"name\"}", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void pushContextTokenTest(VertxTestContext context) {



		client.get("/context/token").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("mySession", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "mySession").end();
	}

	@Test
	public void noContextTokenTest(VertxTestContext context) {



		client.get(PORT, HOST, "/context/token").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(400, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Can't provide @Context of type: class com.zandero.rest.test.data.Token", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void readMethodContextTokenDummy(VertxTestContext context) {



		client.get("/context/dummy").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("mySession,Name:Dummy", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "mySession")
		      .putHeader("X-dummy-value", "Dummy")
		      .putHeader("X-dummy-name", "Name")
		      .end();
	}

	@Test
	public void readParamContextTokenDummy(VertxTestContext context) {



		client.get("/context/dummy-token").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("***mySession***,Name:Dummy", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "mySession")
		      .putHeader("X-dummy-value", "Dummy")
		      .putHeader("X-dummy-name", "Name")
		      .end();
	}
}
*/
