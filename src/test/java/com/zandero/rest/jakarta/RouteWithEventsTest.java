package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.test.*;
import com.zandero.rest.test.json.*;
import com.zandero.utils.extra.*;
import io.vertx.core.*;
import io.vertx.core.eventbus.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RouteWithEventsTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();
    }

    @BeforeEach
    void startUp() {
        Router router = RestRouter.register(vertx, TestEventsRest.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testSimpleEvent(VertxTestContext context) throws InterruptedException {

        AtomicBoolean messageHasBeenSend = new AtomicBoolean(false);
        vertx.eventBus().consumer("rest.vertx.testing", (Handler<Message<String>>) event -> {

            Dummy dummy = JsonUtils.fromJson(event.body(), Dummy.class);
            System.out.println("Received dummy: " + dummy.name + ": " + dummy.value);
            messageHasBeenSend.set(true);
        });

        client.get(PORT, HOST, "/events/ok").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));

        Thread.sleep(1000);
        assertTrue(messageHasBeenSend.get());

        // 2
        messageHasBeenSend.set(false);
        vertx.eventBus().consumer("rest.vertx.testing", (Handler<Message<String>>) event -> {

            Dummy dummy = JsonUtils.fromJson(event.body(), Dummy.class);
            System.out.println("Received dummy: " + dummy.name + ": " + dummy.value);
            messageHasBeenSend.set(true);
        });

        client.get(PORT, HOST, "/events/error/301").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(301, response.statusCode());
                context.completeNow();
            })));

        Thread.sleep(1000);
        assertTrue(messageHasBeenSend.get());

        // 3
        messageHasBeenSend.set(false);
        vertx.eventBus().consumer("rest.vertx.testing", (Handler<Message<Exception>>) event -> {

            //Dummy dummy = JsonUtils.fromJson(event.body(), Dummy.class);
            System.out.println("Received exception: " + event);
            messageHasBeenSend.set(true);
        });

        client.get(PORT, HOST, "/events/error/400").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));

        Thread.sleep(1000);
        assertTrue(messageHasBeenSend.get());
    }
}
