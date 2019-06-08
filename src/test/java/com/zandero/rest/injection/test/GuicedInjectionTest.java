package com.zandero.rest.injection.test;
/*

import com.zandero.rest.RestBuilder;
import com.zandero.rest.VertxTest;
import com.zandero.rest.injection.GuiceInjectionProvider;
import com.zandero.rest.injection.GuicedRest;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.VertxTestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Test;
import org.junit.runner.RunWith;

*/
/**
 *
 *//*

@ExtendWith(VertxExtension.class)
public class GuicedInjectionTest extends VertxTest {

	public void startWith(InjectionProvider provider, VertxTestContext context) {

before();

		Router router = new RestBuilder(vertx)
			                .injectWith(provider)
			                .register(GuicedRest.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void guiceCallInjectedRestTest(VertxTestContext context) {

		startWith(new GuiceInjectionProvider(), context);

		final Async async = context.async();

		client.get(PORT, HOST, "/guice/A").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

			assertEquals(200, response.statusCode());

			response.bodyHandler(bodyHandler -> {
				String body = bodyHandler.getString(0, bodyHandler.length());
				assertEquals("1=I'm so dummy!", body);
				context.completeNow();
			})));
	}

	@Test
	public void guiceCallGuicedRestTest(VertxTestContext context) {

		startWith(new GuiceInjectionProvider(), context);

		final Async async = context.async();

		client.get(PORT, HOST, "/guiceit").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

			assertEquals(200, response.statusCode());

			response.bodyHandler(bodyHandler -> {
				String body = bodyHandler.toString();
				assertEquals("Oh yes I'm so dummy!", body);
				context.completeNow();
			})));
	}

	@Test
	public void guiceCallInjectedPost(VertxTestContext context) {

		startWith(new GuiceInjectionProvider(), context);

		final Async async = context.async();

		Dummy json = new Dummy("test", "me");
		client.post("/guice/json").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

			assertEquals(200, response.statusCode());

			response.bodyHandler(bodyHandler -> {
				String body = bodyHandler.getString(0, bodyHandler.length());
				assertEquals("{\"name\":\"Received-Oh yes I'm so dummy!\",\"value\":\"Received-{\\\"name\\\":\\\"test\\\",\\\"value\\\":\\\"me\\\"}\"}", body);
				context.completeNow();
			});
		}).putHeader("Content-Type", "application/json").end(JsonUtils.toJson(json));
	}
}
*/
