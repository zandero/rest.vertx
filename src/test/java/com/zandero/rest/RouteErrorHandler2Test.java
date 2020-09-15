package com.zandero.rest;

import com.zandero.rest.test.ErrorThrowingRest2;
import com.zandero.rest.test.handler.MyGlobalExceptionHandler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteErrorHandler2Test extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        MyGlobalExceptionHandler handler = new MyGlobalExceptionHandler("Big");

        Router router = new RestBuilder(vertx)
                            .register(new ErrorThrowingRest2())
                            .errorHandler(handler)
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void throwUnhandledExceptionTest(VertxTestContext context) {

        client.get(PORT, HOST, "/throw/myHandler")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("Big auch!", response.body());
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }
}

