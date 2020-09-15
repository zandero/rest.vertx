package com.zandero.rest;

import com.zandero.rest.test.TestMultiProducesRest;
import com.zandero.rest.writer.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteWithMultiProducesTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = new RestBuilder(vertx)
                            .register(TestMultiProducesRest.class)
                            .writer(MediaType.APPLICATION_XML, TestXmlResponseWriter.class)
                            .writer(TestJsonResponseWriter.class) // resolve from @Produces
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void echoXmlTest(VertxTestContext context) {

        client.get(PORT, HOST, "/multi/consume").as(BodyCodec.string())
            .putHeader("Accept", "application/xml")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("<xml>HELLO!</xml>", response.body());
                context.completeNow();
            })));
    }

    @Test
    void echoJsonTest(VertxTestContext context) {

        client.get(PORT, HOST, "/multi/consume").as(BodyCodec.string())
            .putHeader("Accept", "application/json")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("{\"text\": \"HELLO!\"}", response.body());
                context.completeNow();
            })));
    }
}
