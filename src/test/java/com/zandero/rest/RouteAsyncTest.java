package com.zandero.rest;

import com.zandero.rest.test.TestAsyncRest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteAsyncTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestAsyncRest.class);
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(PORT);
    }

    @Test
    void testAsyncCallInsideRest(VertxTestContext context) {

        client.get(PORT, HOST, "/async/call")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals(200, response.statusCode());
                    assertEquals("{\"name\": \"async\", \"value\": \"called\"}", response.body());
                    context.completeNow();
                })));
    }

    @Test
    void testAsyncCompletableRest(VertxTestContext context) {


        client.get(PORT, HOST, "/async/completable")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals(200, response.statusCode());
                    assertEquals("{\"name\": \"hello\", \"value\": \"world\"}", response.body());
                    context.completeNow();
                })));
    }

    @Test
    void testAsyncCallWithExecutorRest(VertxTestContext context) {

        client.get(PORT, HOST, "/async/executor")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals(200, response.statusCode());
                    assertEquals("{\"name\": \"async\", \"value\": \"called\"}", response.body());
                    context.completeNow();
                })));

    }

    @Test
    void testAsyncCallInsideRestNullFutureResult(VertxTestContext context) {

        client.get(PORT, HOST, "/async/empty")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals(204, response.statusCode());
                    context.completeNow();
                })));
    }

    @Test
    void testAsyncCallInsideRestNullNoWriterFutureResult(VertxTestContext context) {

        client.get(PORT, HOST, "/async/null")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

                    assertEquals(200, response.statusCode());
                    context.completeNow();
                })));
    }
}

