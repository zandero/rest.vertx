package com.zandero.rest.injection.test;
/*

import com.zandero.rest.RestBuilder;
import com.zandero.rest.VertxTest;
import com.zandero.rest.injection.*;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.VertxTestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

*/
/**
 *
 *//*

@ExtendWith(VertxExtension.class)
public class InjectionProviderTest extends VertxTest {

	public void startWith(InjectionProvider provider) {

before();

		Router router = new RestBuilder(vertx)
			                .injectWith(provider)
			                .register(InjectedRest.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	public void startWithClass(Class<? extends InjectionProvider> provider) {

before();

		Router router = new RestBuilder(vertx)
			                .injectWith(provider)
			                .register(InjectedRest.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	@Test
	public void testHasInjection() {

before();

		assertTrue(InjectionProvider.hasInjection(GuiceInjectService.class));
		assertTrue(InjectionProvider.hasInjection(OtherServiceImpl.class));
		assertTrue(InjectionProvider.hasInjection(GuicedRest.class));

		assertFalse(InjectionProvider.hasInjection(UserServiceImpl.class));
	}

	@Test
	public void guiceCallInjectedRestTest(VertxTestContext context) {

		startWith(new GuiceInjectionProvider());

		final Async async = context.async();
		
		client.get(PORT, HOST, "/injected/dummy").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

			assertEquals(200, response.statusCode());


				assertEquals("I'm so dummy!", response.body());
				context.completeNow();
			})));
	}

	@Test
	public void guiceCallInjectedRestTest2(VertxTestContext context) {

		startWith(new GuiceInjectionProvider());

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/other").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

			assertEquals(200, response.statusCode());


				assertEquals("Oh yes I'm so dummy!", response.body());
				context.completeNow();
			})));
	}

	@Test
	public void featherCallInjectedRestTest(VertxTestContext context) {

		startWith(new FeatherInjectionProvider());

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/dummy").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

			assertEquals(200, response.statusCode());


				assertEquals("I'm so dummy!", response.body());
				context.completeNow();
			})));
	}

	@Test
	public void featherCallInjectedRestTest2(VertxTestContext context) {

		startWith(new FeatherInjectionProvider());

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/other").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

			assertEquals(200, response.statusCode());


				assertEquals("Oh yes I'm so dummy!", response.body());
				context.completeNow();
			})));
	}

	// class type tests
	@Test
	public void guiceCallInjectedClassRestTest(VertxTestContext context) {

		startWithClass(GuiceInjectionProvider.class);

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/dummy").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

			assertEquals(200, response.statusCode());


				assertEquals("I'm so dummy!", response.body());
				context.completeNow();
			})));
	}

	@Test
	public void guiceCallInjectedClassRestTest2(VertxTestContext context) {

		startWithClass(GuiceInjectionProvider.class);

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/other").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

			assertEquals(200, response.statusCode());


				assertEquals("Oh yes I'm so dummy!", response.body());
				context.completeNow();
			})));
	}

	@Test
	public void featherCallInjectedClassRestTest(VertxTestContext context) {

		startWithClass(FeatherInjectionProvider.class);

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/dummy").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

			assertEquals(200, response.statusCode());


				assertEquals("I'm so dummy!", response.body());
				context.completeNow();
			})));
	}

	@Test
	public void featherCallInjectedClassRestTest2(VertxTestContext context) {

		startWithClass(FeatherInjectionProvider.class);

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/other").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

			assertEquals(200, response.statusCode());


				assertEquals("Oh yes I'm so dummy!", response.body());
				context.completeNow();
			})));
	}
}
*/
