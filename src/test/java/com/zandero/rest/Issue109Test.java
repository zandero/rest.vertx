package com.zandero.rest;

import com.zandero.rest.test.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class Issue109Test extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestIssue109Rest.class);
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);

        vertx.eventBus() // (1)
            .consumer("Hello", handler -> {

                JsonObject message = (JsonObject) handler.body();

                if (message.isEmpty() || message.getString("name") == null) {
                    handler.fail(123, "Missing user");
                }
                else {

                    Map<String, Object> map = new HashMap<>();
                    map.put("response", "World");
                    map.put("user", message.getString("name"));

                    JsonObject response = new JsonObject(map);
                    handler.reply(response);
                }
            });
    }

    @Test
    void testAsyncCallPromiseInsideRest_Fail(VertxTestContext context) {

        JsonObject json = new JsonObject();
        client.post(PORT, HOST, "/issue/109")
            .as(BodyCodec.string())
            .sendBuffer(Buffer.buffer(json.toString()), context.succeeding(response -> context.verify(() -> {
                assertEquals(500, response.statusCode());
                assertEquals("Missing user", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testAsyncCallPromiseInsideRest_Success(VertxTestContext context) {

        JsonObject json = new JsonObject(Collections.singletonMap("name", "Dummy"));
        client.post(PORT, HOST, "/issue/109")
            .as(BodyCodec.string())
            .sendBuffer(Buffer.buffer(json.toString()), context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Dummy", response.body());
                context.completeNow();
            })));
    }
}
