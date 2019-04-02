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
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Test;
import org.junit.runner.RunWith;

*/
/**
 *
 *//*

@RunWith(VertxUnitRunner.class)
public class GuicedInjectionTest extends VertxTest {

	public void startWith(InjectionProvider provider, TestContext context) {

		super.before();

		Router router = new RestBuilder(vertx)
			                .injectWith(provider)
			                .register(GuicedRest.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void guiceCallInjectedRestTest(TestContext context) {

		startWith(new GuiceInjectionProvider(), context);

		final Async async = context.async();

		client.getNow("/guice/A", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(bodyHandler -> {
				String body = bodyHandler.getString(0, bodyHandler.length());
				context.assertEquals("1=I'm so dummy!", body);
				async.complete();
			});
		});
	}

	@Test
	public void guiceCallGuicedRestTest(TestContext context) {

		startWith(new GuiceInjectionProvider(), context);

		final Async async = context.async();

		client.getNow("/guiceit", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(bodyHandler -> {
				String body = bodyHandler.toString();
				context.assertEquals("Oh yes I'm so dummy!", body);
				async.complete();
			});
		});
	}

	@Test
	public void guiceCallInjectedPost(TestContext context) {

		startWith(new GuiceInjectionProvider(), context);

		final Async async = context.async();

		Dummy json = new Dummy("test", "me");
		client.post("/guice/json", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(bodyHandler -> {
				String body = bodyHandler.getString(0, bodyHandler.length());
				context.assertEquals("{\"name\":\"Received-Oh yes I'm so dummy!\",\"value\":\"Received-{\\\"name\\\":\\\"test\\\",\\\"value\\\":\\\"me\\\"}\"}", body);
				async.complete();
			});
		}).putHeader("Content-Type", "application/json").end(JsonUtils.toJson(json));
	}
}
*/
