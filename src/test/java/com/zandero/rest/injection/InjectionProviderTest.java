package com.zandero.rest.injection;

import com.zandero.rest.RestBuilder;
import com.zandero.rest.VertxTest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class InjectionProviderTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		Router router = new RestBuilder(vertx)
			                .injectWith(new GuiceInjectionProvider())
			                .register(InjectedRest.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void callInjectedRestTest(TestContext context) {

		final Async async = context.async();

		client.getNow("/injected/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void callInjectedRestTest2(TestContext context) {

		final Async async = context.async();

		client.getNow("/injected/other", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("Oh yes I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}
}
