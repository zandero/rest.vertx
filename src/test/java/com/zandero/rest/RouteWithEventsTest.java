package com.zandero.rest;
/*

import com.zandero.rest.test.TestEventsRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.VertxTestContext;
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

@ExtendWith(VertxExtension.class)
public class RouteWithEventsTest extends VertxTest {

	@BeforeAll
	static void start() {
		super.before();
	}

	@Test
	public void testSimpleEvent(VertxTestContext context) throws InterruptedException {

		Router router = RestRouter.register(vertx, TestEventsRest.class);

		AtomicBoolean messageHasBeenSend = new AtomicBoolean(false);
		vertx.eventBus().consumer("rest.vertx.testing", (Handler<Message<String>>) event -> {

			Dummy dummy = JsonUtils.fromJson(event.body(), Dummy.class);
			System.out.println("Received dummy: " + dummy.name + ": " + dummy.value);
			messageHasBeenSend.set(true);
		});

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);



		client.get(PORT, HOST, "/events/ok").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		});

		Thread.sleep(1000);
		assertTrue(messageHasBeenSend.get());
	}

	@Test
	public void testStatusEvent(VertxTestContext context) throws InterruptedException {

		Router router = RestRouter.register(vertx, TestEventsRest.class);

		AtomicBoolean messageHasBeenSend = new AtomicBoolean(false);
		vertx.eventBus().consumer("rest.vertx.testing", (Handler<Message<String>>) event -> {

			Dummy dummy = JsonUtils.fromJson(event.body(), Dummy.class);
			System.out.println("Received dummy: " + dummy.name + ": " + dummy.value);
			messageHasBeenSend.set(true);
		});

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);



		client.get(PORT, HOST, "/events/error/301").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals(301, response.statusCode());
				async.complete();
			});
		});

		Thread.sleep(1000);
		assertTrue(messageHasBeenSend.get());
	}

	@Test
	public void testExceptionEvent(VertxTestContext context) throws InterruptedException {

		Router router = RestRouter.register(vertx, TestEventsRest.class);

		AtomicBoolean messageHasBeenSend = new AtomicBoolean(false);
		vertx.eventBus().consumer("rest.vertx.testing", (Handler<Message<Exception>>) event -> {

			//Dummy dummy = JsonUtils.fromJson(event.body(), Dummy.class);
			System.out.println("Received exception: " + event);
			messageHasBeenSend.set(true);
		});

		vertx.createHttpServer()
		     .requestHandler(router)
		     .listen(PORT);



		client.get(PORT, HOST, "/events/error/400").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

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
