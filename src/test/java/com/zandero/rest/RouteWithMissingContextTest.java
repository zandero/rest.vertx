package com.zandero.rest;

import com.zandero.rest.test.TestContextRest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteWithMissingContextTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        TestContextRest testRest = new TestContextRest();
        Router router = RestRouter.register(vertx, testRest);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void missingContextTest(VertxTestContext context) {

        client.get(PORT, HOST, "/context/custom").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                assertEquals("Can't provide @Context of type: class com.zandero.rest.test.json.Dummy", response.body());
                context.completeNow();
            })));
    }
}
