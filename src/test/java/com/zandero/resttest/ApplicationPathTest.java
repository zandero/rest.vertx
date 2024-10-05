package com.zandero.resttest;

import com.zandero.rest.RestRouter;
import com.zandero.resttest.test.RestApplicationV1;
import com.zandero.resttest.test.TestApplicationV1PathRest;
import com.zandero.resttest.test.TestApplicationV2PathRest;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

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

        client.get(PORT, HOST, "/v2/application/echo/this?query=kveri").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("2thiskveri", response.body());
                context.completeNow();
            })));
    }

    @Test
    void v2TestEcho2(VertxTestContext context) {

        client.get(PORT, HOST, "/v2/application/echo2/this?query=kveri").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("/v2/application/echo2/this2kveri", response.body());
                context.completeNow();
            })));
    }

}
