package com.zandero.rest;
/*

import com.zandero.rest.test.TestEventsRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

*/
/**
 *
 *//*

@RunWith(VertxUnitRunner.class)
public class RouteWithEventsTest extends VertxTest {

	@Before
	public void start(TestContext context) {
		super.before();
	}

	@Test
	public void testSimpleEvent(TestContext context) throws InterruptedException {

		Router router = RestRouter.register(vertx, TestEventsRest.class);

		AtomicBoolean messageHasBeenSend = new AtomicBoolean(false);
		vertx.eventBus().consumer("rest.vertx.testing", (Handler<Message<String>>) event -> {

			Dummy dummy = JsonUtils.fromJson(event.body(), Dummy.class);
			System.out.println("Received dummy: " + dummy.name + ": " + dummy.value);
			messageHasBeenSend.set(true);
		});

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);

		// call and check response
		final Async async = context.async();

		client.getNow("/events/ok", response -> {

			response.bodyHandler(body -> {
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		});

		Thread.sleep(1000);
		assertTrue(messageHasBeenSend.get());
	}

	@Test
	public void testStatusEvent(TestContext context) throws InterruptedException {

		Router router = RestRouter.register(vertx, TestEventsRest.class);

		AtomicBoolean messageHasBeenSend = new AtomicBoolean(false);
		vertx.eventBus().consumer("rest.vertx.testing", (Handler<Message<String>>) event -> {

			Dummy dummy = JsonUtils.fromJson(event.body(), Dummy.class);
			System.out.println("Received dummy: " + dummy.name + ": " + dummy.value);
			messageHasBeenSend.set(true);
		});

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);

		// call and check response
		final Async async = context.async();

		client.getNow("/events/error/301", response -> {

			response.bodyHandler(body -> {
				context.assertEquals(301, response.statusCode());
				async.complete();
			});
		});

		Thread.sleep(1000);
		assertTrue(messageHasBeenSend.get());
	}

	@Test
	public void testExceptionEvent(TestContext context) throws InterruptedException {

		Router router = RestRouter.register(vertx, TestEventsRest.class);

		AtomicBoolean messageHasBeenSend = new AtomicBoolean(false);
		vertx.eventBus().consumer("rest.vertx.testing", (Handler<Message<Exception>>) event -> {

			//Dummy dummy = JsonUtils.fromJson(event.body(), Dummy.class);
			System.out.println("Received exception: " + event);
			messageHasBeenSend.set(true);
		});

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);

		// call and check response
		final Async async = context.async();

		client.getNow("/events/error/400", response -> {

			response.bodyHandler(body -> {
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});

		Thread.sleep(1000);
		assertTrue(messageHasBeenSend.get());
	}
}
*/
