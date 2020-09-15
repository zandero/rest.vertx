package com.zandero.rest;

import com.zandero.rest.test.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class ApplicationPathTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        RestApplicationV1 testV1Rest = new TestApplicationV1PathRest();
        RestApplicationV1 testV2Rest = new TestApplicationV2PathRest();

        Router router = RestRouter.register(vertx, testV1Rest, testV2Rest);
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void v1Test(VertxTestContext context) {

        client.get(PORT, HOST, "/v1/application/echo/this").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("this", response.body());
                context.completeNow();
            })));
    }

    @Test
    void v2Test(VertxTestContext context) {

        client.get(PORT, HOST, "/v2/application/echo/this").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("2this", response.body());
                context.completeNow();
            })));
    }

}
