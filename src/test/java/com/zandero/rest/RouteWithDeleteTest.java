package com.zandero.rest;

import com.zandero.rest.test.TestDeleteRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteWithDeleteTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestDeleteRest.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testDeleteWithBody(VertxTestContext context) {

        Dummy json = new Dummy("test", "me");
        client.delete(PORT, HOST, "/delete/it/123").as(BodyCodec.string())
            .sendBuffer(Buffer.buffer(JsonUtils.toJson(json)), context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("<custom>Received-test=Received-me</custom>", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testDeleteWithoutBody(VertxTestContext context) {

        client.delete(PORT, HOST, "/delete/empty/123").send(context.succeeding(response -> context.verify(() -> {
            assertEquals(200, response.statusCode());
            assertEquals("success", response.body().toString());
            context.completeNow();
        })));
    }
}
