package com.zandero.rest;

import com.zandero.rest.test.TestAsyncRest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
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
    void testAsyncCallFutureInsideRest(VertxTestContext context) {

        client.get(PORT, HOST, "/async/call_future")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("{\"name\": \"async\", \"value\": \"called\"}", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testAsyncCallPromiseInsideRest(VertxTestContext context) {

        client.get(PORT, HOST, "/async/call_promise")
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
    void testAsyncCallWithExecutorFutureRest(VertxTestContext context) {

        client.get(PORT, HOST, "/async/executor_future")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("{\"name\": \"async\", \"value\": \"called\"}", response.body());
                context.completeNow();
            })));

    }

    @Test
    void testAsyncCallWithExecutorPromiseRest(VertxTestContext context) {

        client.get(PORT, HOST, "/async/executor_promise")
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

    @Test
    void testAsyncHanlder(VertxTestContext context) throws InterruptedException {

        client.get(PORT, HOST, "/async/handler")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {

                assertEquals(200, response.statusCode());
                assertEquals("\"invoked\"", response.body());
                context.completeNow();
            })));

        Thread.sleep(2000); // wait until handler finished
    }
}

