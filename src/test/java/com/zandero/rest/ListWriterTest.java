package com.zandero.rest;

import com.zandero.rest.test.TestWriterRest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class ListWriterTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();

        Router router = new RestBuilder(vertx)
                            .register(TestWriterRest.class)
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void getOne(VertxTestContext context) {

        client.get(PORT, HOST, "/write/one")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("{\"value\":\"Hello world!\"}", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void getMany(VertxTestContext context) {

        client.get(PORT, HOST, "/write/many")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("[{\"value\":\"One\"},{\"value\":\"Two\"}]", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }
}