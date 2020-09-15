package com.zandero.rest;

import com.zandero.rest.test.TestFormRest;
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
class FormRestTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();
        TestFormRest testRest = new TestFormRest();

        Router router = RestRouter.register(vertx, testRest);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void loginFormTest(VertxTestContext context) {

        String content = "username=value&password=another";

        client.post(PORT, HOST, "/form/login")
            .as(BodyCodec.string())
            .putHeader("content-type", "application/x-www-form-urlencoded")
            .sendBuffer(Buffer.buffer(content), context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("value:another", response.body());
                context.completeNow();
            })));
    }

    @Test
    void multipartFormTest(VertxTestContext context) {

        String content = "username=value&password=another";

        client.post(PORT, HOST, "/form/login")
            .as(BodyCodec.string())
            .putHeader("content-type", "multipart/form-data")
            .sendBuffer(Buffer.buffer(content), context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("value:another", response.body());
                context.completeNow();
            })));
    }

    @Test
    void sendCookieTest(VertxTestContext context) {

        client.post(PORT, HOST, "/form/cookie")
            .as(BodyCodec.string())
            .putHeader("Cookie", "username=blabla")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("blabla", response.body());
                context.completeNow();
            })));
    }
}
