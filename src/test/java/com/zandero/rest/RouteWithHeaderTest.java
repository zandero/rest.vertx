package com.zandero.rest;

import com.zandero.rest.test.TestHeaderRest;
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
class RouteWithHeaderTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestHeaderRest.class);
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void echoDummyJsonTest(VertxTestContext context) {

        Dummy dummy = new Dummy("one", "dude");
        String json = JsonUtils.toJson(dummy);

        client.get(PORT, HOST, "/header/dummy").as(BodyCodec.string())
            .putHeader("dummy", json)
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("one=dude", response.body());
                context.completeNow();
            })));
    }

    @Test
    void echoPostHeaderTest(VertxTestContext context) {

        Dummy dummy = new Dummy("one", "dude");

        String json = JsonUtils.toJson(dummy);

        client.post(PORT, HOST, "/header/dummy").as(BodyCodec.string())
            .putHeader("token", "doing")
            .putHeader("other", "things")
            .sendBuffer(Buffer.buffer(JsonUtils.toJson(json)), context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("one=dude, doing things", response.body());
                context.completeNow();
            })));
    }

    @Test
    void npeReaderTest(VertxTestContext context) {

        client.get(PORT, HOST, "/header/npe").as(BodyCodec.string())
            .putHeader("dummy", "")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("OH SHIT!", response.body());
                assertEquals(500, response.statusCode());
                context.completeNow();
            })));
    }
}