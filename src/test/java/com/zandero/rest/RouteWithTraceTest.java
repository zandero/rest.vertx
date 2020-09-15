package com.zandero.rest;

import com.zandero.rest.test.TestEchoRest;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteWithTraceTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();

        Router router = RestRouter.register(vertx, TestEchoRest.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testTrace(VertxTestContext context) {

        client.request(HttpMethod.TRACE, PORT, HOST, "/rest/echo").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("trace", response.body()); // returns sorted list of unique words
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }
}

