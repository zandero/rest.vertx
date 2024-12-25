package com.zandero.rest.injection.test;


import com.zandero.rest.*;
import com.zandero.rest.injection.*;
import com.zandero.rest.test.json.*;
import com.zandero.utils.extra.*;
import io.vertx.core.*;
import io.vertx.core.buffer.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class GuicedInjectionTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = new RestBuilder(vertx)
                            .injectWith(new GuiceInjectionProvider())
                            .register(GuicedRest.class)
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @BeforeEach
    void cleanUp() {
        // clear all registered writers or reader and handlers
        RestRouter.validateWith((javax.validation.Validator) null);
        RestRouter.validateWith((jakarta.validation.Validator) null);
        RestRouter.injectWith((InjectionProvider) null);
    }

    @AfterEach
    void lastChecks(Vertx vertx) {
        vertx.close(vertxTestContext.succeeding());
        vertx.close();
    }

    @Test
    void guiceCallInjectedRestTest(VertxTestContext context) {

        client.get(PORT, HOST, "/guice/A")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("1=I'm so dummy!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void guiceCallGuicedRestTest(VertxTestContext context) {

        client.get(PORT, HOST, "/guiceit")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Oh yes I'm so dummy!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void guiceCallInjectedPost(VertxTestContext context) {

        Dummy json = new Dummy("test", "me");
        client.post(PORT, HOST, "/guice/json")
            .as(BodyCodec.string())
            .putHeader("Content-Type", "application/json")
            .sendBuffer(Buffer.buffer(JsonUtils.toJson(json)),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("{\"name\":\"Received-Oh yes I'm so dummy!\",\"value\":\"Received-{\\\"name\\\":\\\"test\\\",\\\"value\\\":\\\"me\\\"}\"}", response.body());
                            context.completeNow();
                        })));
    }
}

