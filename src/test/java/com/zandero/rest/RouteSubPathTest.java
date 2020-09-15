package com.zandero.rest;

import com.zandero.rest.test.TestPathRest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteSubPathTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        TestPathRest testRest = new TestPathRest();

        Router router = RestRouter.register(vertx, testRest);
        router.mountSubRouter("/sub", router);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void rootWithRootPathTest(VertxTestContext context) {

        client.get(PORT, HOST, "/query/echo/this").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("querythis", response.body());
                context.completeNow();
            })));

        client.get(PORT, HOST, "/sub/query/echo/this").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("querythis", response.body());
                context.completeNow();
            })));
    }

    @Test
    void rootWithRootPathTest2(VertxTestContext context) {

        client.get(PORT, HOST, "/this/echo/query").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("thisquery", response.body());
                context.completeNow();
            })));

        client.get(PORT, HOST, "/sub/this/echo/query").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("thisquery", response.body());
                context.completeNow();
            })));
    }

    @Test
    void rootWithoutPathTest(VertxTestContext context) {

        client.get(PORT, HOST, "/this").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("this", response.body());
                context.completeNow();
            })));

        client.get(PORT, HOST, "/sub/this").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("this", response.body());
                context.completeNow();
            })));
    }
}

