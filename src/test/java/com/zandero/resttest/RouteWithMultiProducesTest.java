package com.zandero.resttest;

import com.zandero.rest.RestBuilder;
import com.zandero.resttest.test.TestMultiProducesRest;
import com.zandero.resttest.writer.TestJsonResponseWriter;
import com.zandero.resttest.writer.TestXmlResponseWriter;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.ws.rs.core.MediaType;

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

    @Test
    void echoProducesJsonTest(VertxTestContext context) {

        client.get(PORT, HOST, "/multi/produce").as(BodyCodec.string())
            .putHeader("Accept", "application/json")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("{\"text\": \"Bam!\"}", response.body());
                context.completeNow();
            })));
    }

    @Test
    void echoProducesXmlTest(VertxTestContext context) {

        client.get(PORT, HOST, "/multi/produce").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("<xml>Bam!</xml>", response.body());
                context.completeNow();
            })));
    }
}
