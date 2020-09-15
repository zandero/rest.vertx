package com.zandero.rest;

import com.zandero.rest.test.TestMatrixParamRest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteWithMatrixTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestMatrixParamRest.class);
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void matrixExtractTest(VertxTestContext context) {

        client.get(PORT, HOST, "/matrix/extract/result;one=1;two=2").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("result=3", response.body());
                context.completeNow();
            })));
    }

    @Test
    void matrixRegExTest(VertxTestContext context) {

        client.get(PORT, HOST, "/matrix/direct/param;one=1;two=2").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("3", response.body());
                context.completeNow();
            })));
    }
}
