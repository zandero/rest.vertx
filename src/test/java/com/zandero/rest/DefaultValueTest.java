package com.zandero.rest;

import com.zandero.rest.test.TestDefaultValueRest;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.Handler;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
@ExtendWith(VertxExtension.class)
class DefaultValueTest extends VertxTest {

    private static Router router;

    @BeforeAll
    static void start() {
        before();

        router = RestRouter.register(vertx, TestDefaultValueRest.class);
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void callMissingQueryParam(VertxTestContext context) {

        client.get(PORT, HOST, "/default/echo")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Hello unknown", response.body());
                context.completeNow();
            })));
    }

    @Test
    void callPresentQueryParam(VertxTestContext context) {

        client.get(PORT, HOST, "/default/echo?name=baby")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("Hello baby", response.body());
                context.completeNow();
            })));
    }

    @Test
    void callMissingContext(VertxTestContext context) {

        client.get(PORT, HOST, "/default/context")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Context is unknown user", response.body());
                context.completeNow();
            })));
    }

    @Test
    void callPresentContext(VertxTestContext context) {

        router.route().order(RestRouter.ORDER_PROVIDER_HANDLER).handler(pushContextHandler());

        client.get(PORT, HOST, "/default/context").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Context is test user", response.body());
                context.completeNow();
            })));
    }

    private Handler<RoutingContext> pushContextHandler() {

        return context -> {
            RestRouter.pushContext(context, new Dummy("test", "user"));
            context.next();
        };
    }
}
