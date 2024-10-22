package com.zandero.resttest;

import com.zandero.rest.RestBuilder;
import com.zandero.resttest.test.TestEchoRest;
import com.zandero.resttest.test.handler.NotFoundHandler;
import com.zandero.resttest.test.handler.OtherNotFoundHandler;
import com.zandero.resttest.test.handler.RestNotFoundHandler;
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
public class NoMatchTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();

        Router router = new RestBuilder(vertx)
                            .register(TestEchoRest.class)
                            .notFound(".*\\/other/?.*", OtherNotFoundHandler.class)
                            .notFound("/rest/.*", RestNotFoundHandler.class)
                            .notFound(NotFoundHandler.class)
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testEcho(VertxTestContext context) {

        client.get(PORT, HOST, "/rest/echo")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("\"echo\"", response.body());
                assertEquals(200, response.statusCode());

                String header = response.getHeader("Content-Type");
                assertEquals("application/json;charset=UTF-8", header);
                context.completeNow();
            })));
    }

    @Test
    void testNoMatchDefault(VertxTestContext context) {

        client.get(PORT, HOST, "/bla")
            .putHeader("Accept", "application/json")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(404, response.statusCode());
                assertEquals("404 HTTP Resource: '/bla' not found!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testNoMatchRest(VertxTestContext context) {

        client.get(PORT, HOST, "/rest/bla")
            .as(BodyCodec.string())
            .putHeader("Accept", "application/json")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(404, response.statusCode());
                assertEquals("REST endpoint: '/rest/bla' not found!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testOtherRest(VertxTestContext context) {

        client.get(PORT, HOST, "/rest/other")
            .as(BodyCodec.string())
            .putHeader("Accept", "application/json")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(404, response.statusCode());
                assertEquals("'/rest/other' not found!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testOther2Rest(VertxTestContext context) {

        client.get(PORT, HOST, "/rest/other/")
            .putHeader("Accept", "application/json")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(404, response.statusCode());
                assertEquals("'/rest/other/' not found!", response.body());
                context.completeNow();
            })));
    }
}