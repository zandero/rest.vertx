package com.zandero.rest.injection.test;

import com.zandero.rest.RestBuilder;
import com.zandero.rest.VertxTest;
import com.zandero.rest.injection.FeatherInjectionProvider;
import com.zandero.rest.injection.GuiceInjectionProvider;
import com.zandero.rest.injection.InjectedRest;
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
public class InjectionProviderTest extends VertxTest {

	public void startWith(InjectionProvider provider, TestContext context) {

		super.before(context);

		Router router = new RestBuilder(vertx)
			                .injectWith(provider)
			                .register(InjectedRest.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	public void startWithClass(Class<? extends InjectionProvider> provider, TestContext context) {

		super.before(context);

		Router router = new RestBuilder(vertx)
			                .injectWith(provider)
			                .register(InjectedRest.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void guiceCallInjectedRestTest(TestContext context) {

		startWith(new GuiceInjectionProvider(), context);

		final Async async = context.async();

		client.getNow("/injected/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void guiceCallInjectedRestTest2(TestContext context) {

		startWith(new GuiceInjectionProvider(), context);

		final Async async = context.async();

		client.getNow("/injected/other", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Oh yes I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void featherCallInjectedRestTest(TestContext context) {

		startWith(new FeatherInjectionProvider(), context);

		final Async async = context.async();

		client.getNow("/injected/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void featherCallInjectedRestTest2(TestContext context) {

		startWith(new FeatherInjectionProvider(), context);

		final Async async = context.async();

		client.getNow("/injected/other", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Oh yes I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	// class type tests
	@Test
	public void guiceCallInjectedClassRestTest(TestContext context) {

		startWithClass(GuiceInjectionProvider.class, context);

		final Async async = context.async();

		client.getNow("/injected/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void guiceCallInjectedClassRestTest2(TestContext context) {

		startWithClass(GuiceInjectionProvider.class, context);

		final Async async = context.async();

		client.getNow("/injected/other", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Oh yes I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void featherCallInjectedClassRestTest(TestContext context) {

		startWithClass(FeatherInjectionProvider.class, context);

		final Async async = context.async();

		client.getNow("/injected/dummy", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void featherCallInjectedClassRestTest2(TestContext context) {

		startWithClass(FeatherInjectionProvider.class, context);

		final Async async = context.async();

		client.getNow("/injected/other", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Oh yes I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}
}
