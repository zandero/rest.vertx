package com.zandero.rest;

import com.zandero.rest.test.TestPostRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.buffer.Buffer;
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
class DeleteWithBodyTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();

        Router router = RestRouter.register(vertx, TestPostRest.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testDelete(VertxTestContext context) {

        // call and check response
        Dummy dummy = new Dummy("hello", "world");
        String json = JsonUtils.toJson(dummy);

        client.delete(PORT, HOST, "/post/json")
            .as(BodyCodec.string())
            .putHeader("Content-Type", "application/json")
            .sendBuffer(Buffer.buffer(json), context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("application/json", response.getHeader("Content-Type"));
                assertEquals("<custom>Received-hello=Received-world</custom>", response.body());
                context.completeNow();
            })));
    }
}
