package com.zandero.rest;

import com.zandero.rest.test.ImplementationRest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class InheritedRouteTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();

        Router router = RestRouter.register(vertx, ImplementationRest.class);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(PORT);
    }

    @Test
    void echoTest(VertxTestContext context) {

        client.get(PORT, HOST, "/implementation/echo?name=test")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals("\"test\"", response.body()); // JsonExceptionWriter
                    assertEquals(200, response.statusCode());
                    context.completeNow();
                })));
    }

    @Test
    void getTest(VertxTestContext context) {


        client.get(PORT, HOST, "/implementation/get/test?additional=it")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals("\"testit\"", response.body()); // JsonExceptionWriter
                    assertEquals(200, response.statusCode());
                    context.completeNow();
                })));
    }
}
