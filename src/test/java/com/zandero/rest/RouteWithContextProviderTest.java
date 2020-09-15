package com.zandero.rest;

import com.zandero.rest.test.TestContextRest;
import com.zandero.rest.test.data.TokenProvider;
import com.zandero.rest.test.json.Dummy;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteWithContextProviderTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        TestContextRest testRest = new TestContextRest();

        Router router = RestRouter.register(vertx, testRest);

        RestRouter.addProvider(Dummy.class, request -> new Dummy("test", "name"));
        RestRouter.addProvider(TokenProvider.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void pushContextTest(VertxTestContext context) {


        client.get(PORT, HOST, "/context/custom").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {

                assertEquals(200, response.statusCode());


                assertEquals("{\"name\":\"test\",\"value\":\"name\"}", response.body());
                context.completeNow();
            })));
    }

    @Test
    void pushContextTokenTest(VertxTestContext context) {

        client.get(PORT, HOST, "/context/token").as(BodyCodec.string())
            .putHeader("X-Token", "mySession")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("mySession", response.body());
                context.completeNow();
            })));
    }

    @Test
    void noContextTokenTest(VertxTestContext context) {

        client.get(PORT, HOST, "/context/token").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                assertEquals("Can't provide @Context of type: class com.zandero.rest.test.data.Token", response.body());
                context.completeNow();
            })));
    }

    @Test
    void readMethodContextTokenDummy(VertxTestContext context) {

        client.get(PORT, HOST, "/context/dummy").as(BodyCodec.string())
            .putHeader("X-Token", "mySession")
            .putHeader("X-dummy-value", "Dummy")
            .putHeader("X-dummy-name", "Name")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("mySession,Name:Dummy", response.body());
                context.completeNow();
            })));
    }

    @Test
    void readParamContextTokenDummy(VertxTestContext context) {

        client.get(PORT, HOST, "/context/dummy-token").as(BodyCodec.string())
            .putHeader("X-Token", "mySession")
            .putHeader("X-dummy-value", "Dummy")
            .putHeader("X-dummy-name", "Name")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("***mySession***,Name:Dummy", response.body());
                context.completeNow();
            })));
    }
}
