package com.zandero.resttest;

import com.zandero.rest.RestRouter;
import com.zandero.resttest.test.TestEchoRest;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
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

        client.request(HttpMethod.OPTIONS,PORT, HOST, "/rest/echo/body")
            .as(BodyCodec.string())
            .sendBuffer(Buffer.buffer("The quick brown fox jumps over the red dog!"),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("The quick brown fox jumps over the red dog!", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));
    }

    /**
     * json defines get's as bodyless, although some do implement directly on a fetch stream, if we adhere to the spec we shouldn't allow t
     * @param context
     */
    @Test
    void testSimpleEchoBody(VertxTestContext context) {

        client.get(PORT, HOST, "/rest/echo/simple/body")
            .as(BodyCodec.string())
            .sendBuffer(Buffer.buffer("The quick brown fox jumps over the red dog!"),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(405, response.statusCode());
                            //assertEquals("Simple: The quick brown fox jumps over the red dog!", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));

        //instead we use options
        client.request(HttpMethod.OPTIONS,PORT, HOST, "/rest/echo/simple/body")
                .as(BodyCodec.string())
                .sendBuffer(Buffer.buffer("The quick brown fox jumps over the red dog!"),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("Simple: The quick brown fox jumps over the red dog!", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));
    }
}
