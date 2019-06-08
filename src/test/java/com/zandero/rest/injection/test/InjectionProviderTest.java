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

		super.before();

		Router router = new RestBuilder(vertx)
			                .injectWith(provider)
			                .register(InjectedRest.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);
	}

	public void startWithClass(Class<? extends InjectionProvider> provider) {

		super.before();

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

		super.before();

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
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void guiceCallInjectedRestTest2(VertxTestContext context) {

		startWith(new GuiceInjectionProvider());

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/other").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Oh yes I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void featherCallInjectedRestTest(VertxTestContext context) {

		startWith(new FeatherInjectionProvider());

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/dummy").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void featherCallInjectedRestTest2(VertxTestContext context) {

		startWith(new FeatherInjectionProvider());

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/other").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Oh yes I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	// class type tests
	@Test
	public void guiceCallInjectedClassRestTest(VertxTestContext context) {

		startWithClass(GuiceInjectionProvider.class);

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/dummy").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void guiceCallInjectedClassRestTest2(VertxTestContext context) {

		startWithClass(GuiceInjectionProvider.class);

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/other").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Oh yes I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void featherCallInjectedClassRestTest(VertxTestContext context) {

		startWithClass(FeatherInjectionProvider.class);

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/dummy").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void featherCallInjectedClassRestTest2(VertxTestContext context) {

		startWithClass(FeatherInjectionProvider.class);

		final Async async = context.async();

		client.get(PORT, HOST, "/injected/other").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("Oh yes I'm so dummy!", body.toString());
				async.complete();
			});
		});
	}
}
*/
