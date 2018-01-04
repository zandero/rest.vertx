package com.zandero.rest.injection.test;

import com.zandero.rest.RestBuilder;
import com.zandero.rest.VertxTest;
import com.zandero.rest.injection.GuiceInjectionProvider;
import com.zandero.rest.injection.GuicedRest;
import com.zandero.rest.injection.InjectionProvider;
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
public class GuicedInjectionTest extends VertxTest {

	public void startWith(InjectionProvider provider, TestContext context) {

		super.before(context);

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
				context.assertEquals("A", body);
				async.complete();
			});
		});
	}
}
