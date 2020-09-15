package com.zandero.rest;

import com.zandero.rest.test.TestContextRest;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.Handler;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteWithContextTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = Router.router(vertx);
        router.route().handler(pushContextHandler());

        RestRouter.register(router, TestContextRest.class);
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    static private Handler<RoutingContext> pushContextHandler() {

        return context -> {
            RestRouter.pushContext(context, new Dummy("test", "user"));
            context.next();
        };
    }

    @Test
    void testGetRouteContext(VertxTestContext context) {

        client.get(PORT, HOST, "/context/route").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("GET /context/route", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetRequestResponseContext(VertxTestContext context) {

        client.get(PORT, HOST, "/context/context").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(201, response.statusCode());
                assertEquals("/context/context", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetNonExistentContext(VertxTestContext context) {


        client.get(PORT, HOST, "/context/unknown").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                assertEquals("Can't provide @Context of type: interface javax.ws.rs.core.Request", response.body());
                context.completeNow();
            })));
    }

    @Test
    void pushContextTest(VertxTestContext context) {

        client.get(PORT, HOST, "/context/custom").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("{\"name\":\"test\",\"value\":\"user\"}", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testResponseContext(VertxTestContext context) {

        client.get(PORT, HOST, "/context/login").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(201, response.statusCode());
                assertEquals("session", response.getHeader("X-SessionId"));
                assertEquals("Hello world!", response.body());
                context.completeNow();
            })));
    }
}
