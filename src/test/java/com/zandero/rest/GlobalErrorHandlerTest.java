package com.zandero.rest;

import com.zandero.rest.test.*;
import com.zandero.rest.test.handler.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */

@ExtendWith(VertxExtension.class)
class GlobalErrorHandlerTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();

        ErrorThrowingRest handled = new ErrorThrowingRest();
        ErrorThrowingRest2 unhandled = new ErrorThrowingRest2();

        Router router = RestRouter.register(vertx, unhandled, handled);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void throwOuchExceptionTest(VertxTestContext context) {

        client.get(PORT, HOST, "/throw/ouch")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("{\"message\":\"Ouch!\",\"code\":406}", response.body()); // JsonExceptionWriter
                assertEquals(406, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwBangExceptionTest(VertxTestContext context) {

        client.get(PORT, HOST, "/throw/bang")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {

                assertEquals("Bang!", response.body());
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwUnhandledExceptionTest(VertxTestContext context) {

        // call and check response
        RestRouter.getExceptionHandlers().register(IllegalArgumentExceptionHandler.class);

        client.get(PORT, HOST, "/throw/unhandled")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {

                assertEquals("Huh this produced an error: 'KABUM!'", response.body());
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwDifferentExceptionTestOne(VertxTestContext context) {

        client.get(PORT, HOST, "/throw/multi/one")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> { // throws NotAllowedException
                assertEquals("Exception: HTTP 405 Method Not Allowed", response.body()); // ExceptionWriter kicked in
                assertEquals(405, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void throwDifferentExceptionTestTwo(VertxTestContext context) {

        client.get(PORT, HOST, "/throw/multi/two")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("Huh this produced an error: 'Bang!'", response.body()); // IllegalArgumentExceptionWriter kicked in
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void multipleGlobalErrorHandlersTest(VertxTestContext context) {

        RestRouter.getExceptionHandlers().register(new JsonExceptionHandler());

        client.get(PORT, HOST, "/throw/multi/four")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("Exception: ADIOS!", response.body()); // ExceptionWriter kicked in
                assertEquals(500, response.statusCode()); //
                context.completeNow();
            })));
    }

    @Test
    void multipleGlobalErrorHandlersTest2(VertxTestContext context) {
        client.get(PORT, HOST, "/throw/multi/one")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("Exception: HTTP 405 Method Not Allowed", response.body()); // ExceptionWriter kicked in
                assertEquals(405, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void multipleGlobalErrorHandlersTest3(VertxTestContext context) {
        client.get(PORT, HOST, "/throw/multi/three")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("Huh this produced an error: 'WHAT!'", response.body()); // ExceptionWriter kicked in
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }
}
