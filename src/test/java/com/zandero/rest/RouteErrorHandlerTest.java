package com.zandero.rest;

import com.zandero.rest.test.*;
import com.zandero.rest.test.handler.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteErrorHandlerTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = new RestBuilder(vertx)
                            .register(ErrorThrowingRest.class, new ErrorThrowingRest2())
                            .errorHandler(new MyExceptionHandler(),
                                          new IllegalArgumentExceptionHandler(),
                                          new MyOtherExceptionHandler())
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void throwUnhandledExceptionTest(VertxTestContext context) {

        client.get(PORT, HOST, "/throw/unhandled").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("Huh this produced an error: 'KABUM!'", response.body());
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwUnhandledExceptionOne(VertxTestContext context) {

        client.get(PORT, HOST, "/throw/big/one").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("{\"message\":\"HTTP 405 Method Not Allowed\",\"code\":406}", response.body());
                assertEquals(406, response.statusCode()); // JsonExceptionHandler takes over
                context.completeNow();
            })));
    }

    @Test
    void throwUnhandledExceptionTwo(VertxTestContext context) {

        // JsonExceptionHandler will take over
        client.get(PORT, HOST, "/throw/big/two").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("{\"message\":\"Bang!\",\"code\":406}", response.body());
                assertEquals(406, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwUnhandledExceptionThree(VertxTestContext context) {

        // JsonExceptionHandler
        client.get(PORT, HOST, "/throw/big/three").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("{\"message\":\"WHAT!\",\"code\":406}", response.body());
                assertEquals(406, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwUnhandledExceptionFour(VertxTestContext context) {

        // FAIL MyExceptionHandler should handle this one
        client.get(PORT, HOST, "/throw/big/four").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("{\"message\":null,\"code\":406}", response.body());
                assertEquals(406, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwHandledExceptionOne(VertxTestContext context) {

        client.get(PORT, HOST, "/throw/multi/one").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                // MyOtherExceptionHandler should handle this
                assertEquals("Exception: HTTP 405 Method Not Allowed", response.body());
                assertEquals(405, response.statusCode()); // JsonExceptionHandler takes over
                context.completeNow();
            })));
    }

    @Test
    void throwHandledExceptionTwo(VertxTestContext context) {

        // IllegalArgumentExceptionHandler should handle this one
        client.get(PORT, HOST, "/throw/multi/two").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("Huh this produced an error: 'Bang!'", response.body());
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwHandledExceptionThree(VertxTestContext context) {

        // IllegalArgumentExceptionHandler should handle this one
        client.get(PORT, HOST, "/throw/multi/three").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("Huh this produced an error: 'WHAT!'", response.body());
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwHandledExceptionFour(VertxTestContext context) {

        // MyExceptionHandler should handle this one
        client.get(PORT, HOST, "/throw/multi/four").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("Exception: ADIOS!", response.body());
                assertEquals(500, response.statusCode());
                context.completeNow();
            })));
    }
}
