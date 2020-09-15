package com.zandero.rest;

import com.zandero.rest.test.TestOrderRest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteOrderTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        TestOrderRest testRest = new TestOrderRest();

        Router router = RestRouter.register(vertx, testRest);
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void rootWithRootPathTest(VertxTestContext context) {

        client.get(PORT, HOST, "/order/test").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("first", response.body());
                context.completeNow();
            })));
    }
}
