package com.zandero.rest.injection.test;

import com.zandero.rest.RestBuilder;
import com.zandero.rest.VertxTest;
import com.zandero.rest.injection.FeatherInjectionProvider;
import com.zandero.rest.injection.GuiceInjectionProvider;
import com.zandero.rest.injection.InjectedServicesRest;
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
public class InjectionServiceProviderTest extends VertxTest {

	public void startWith(InjectionProvider provider, TestContext context) {

		super.before(context);

		Router router = new RestBuilder(vertx)
			                .injectWith(provider)
			                .register(InjectedServicesRest.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	public void startWithClass(Class<? extends InjectionProvider> provider, TestContext context) {

		super.before(context);

		Router router = new RestBuilder(vertx)
			                .injectWith(provider)
			                .register(InjectedServicesRest.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void guiceCallInjectedServiceRestTest(TestContext context) {

		startWith(new GuiceInjectionProvider(), context);

		final Async async = context.async();

		client.getNow("/getInstance/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void guiceCallInjectedServiceRestTest2(TestContext context) {

		startWithClass(GuiceInjectionProvider.class, context);

		final Async async = context.async();

		client.getNow("/getInstance/other", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Oh yes I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void featherCallInjectedServiceRestTest(TestContext context) {

		startWith(new FeatherInjectionProvider(), context);

		final Async async = context.async();

		client.getNow("/getInstance/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void featherCallInjectedServiceRestTest2(TestContext context) {

		startWithClass(FeatherInjectionProvider.class, context);

		final Async async = context.async();

		client.getNow("/getInstance/other", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Oh yes I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}
}
