package com.zandero.rest;

import com.zandero.rest.test.TestEchoRest;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteWithGetBodyTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestEchoRest.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testEcho(VertxTestContext context) {

        client.get(PORT, HOST, "/rest/echo")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("\"echo\"", response.body());

                String header = response.getHeader("Content-Type");
                assertEquals("application/json;charset=UTF-8", header);
                context.completeNow();
            })));
    }

    @Test
    void testEchoBody(VertxTestContext context) {

        client.get(PORT, HOST, "/rest/echo/body")
            .as(BodyCodec.string())
            .sendBuffer(Buffer.buffer("The quick brown fox jumps over the red dog!"),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("The quick brown fox jumps over the red dog!", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));
    }

    @Test
    void testSimpleEchoBody(VertxTestContext context) {

        client.get(PORT, HOST, "/rest/echo/simple/body")
            .as(BodyCodec.string())
            .sendBuffer(Buffer.buffer("The quick brown fox jumps over the red dog!"),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("Simple: The quick brown fox jumps over the red dog!", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));
    }
}
