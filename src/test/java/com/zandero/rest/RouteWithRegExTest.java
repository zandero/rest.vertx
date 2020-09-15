package com.zandero.rest;

import com.zandero.rest.test.TestRegExRest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteWithRegExTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        TestRegExRest testRest = new TestRegExRest();
        Router router = RestRouter.register(vertx, testRest);
        router = router.mountSubRouter("/sub", router);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testSimpleRegEx(VertxTestContext context) {

        client.get(PORT, HOST, "/regEx/123").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("123", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testSubSimpleRegEx(VertxTestContext context) {

        client.get(PORT, HOST, "/sub/regEx/231").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("231", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testRegEx(VertxTestContext context) {

        client.get(PORT, HOST, "/regEx/1/minus/2").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("-1", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testSubRegEx(VertxTestContext context) {

        client.get(PORT, HOST, "/sub/regEx/2/minus/1").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("1", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testSimpleRegExWithMultipleVariables(VertxTestContext context) {

        client.get(PORT, HOST, "/regEx/ena/2/tri").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("{one=ena, two=2, three=tri}", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testSubSimpleRegExWithMultipleVariables(VertxTestContext context) {

        client.get(PORT, HOST, "/sub/regEx/ena/2/tri").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("{one=ena, two=2, three=tri}", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testAllButApi(VertxTestContext context) {

        client.get(PORT, HOST, "/regEx/api/a").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("api - last", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testAllButApi2(VertxTestContext context) {

        client.get(PORT, HOST, "/regEx/test").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("test - not /api", response.body());
                context.completeNow();
            })));
    }
}
