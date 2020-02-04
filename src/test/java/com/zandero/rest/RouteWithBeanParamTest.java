package com.zandero.rest;

import com.zandero.rest.test.TestBeanReaderRest;
import com.zandero.rest.test.TestContextRest;
import com.zandero.rest.test.data.TokenProvider;
import com.zandero.rest.test.json.Dummy;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteWithBeanParamTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestBeanReaderRest.class);

       /* RestRouter.addProvider(Dummy.class, request -> new Dummy("test", "name"));
        RestRouter.addProvider(TokenProvider.class);*/

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(PORT);
    }

    @Test
    void postBean(VertxTestContext context) {

        client.post(PORT, HOST, "/read/bean?query=1").as(BodyCodec.string())
                .putHeader("MyHeader", "true")
                .putHeader("Cookie", "chocolate=tasty")
                .send(context.succeeding(response -> context.verify(() -> {

                    assertEquals("Header: true, Path: /read/bean, Query: 1, Cookie: tasty", response.body());
                    assertEquals(200, response.statusCode());


                    context.completeNow();
                })));
    }

   /* @Test
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
    }*/
}
