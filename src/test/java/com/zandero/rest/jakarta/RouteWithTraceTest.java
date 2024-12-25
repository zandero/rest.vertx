package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.test.*;
import io.vertx.core.http.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

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

