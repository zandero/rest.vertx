package com.zandero.rest;

import com.zandero.rest.test.TestQueryRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.ext.web.Router;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteWithQueryTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        TestQueryRest testRest = new TestQueryRest();

        Router router = RestRouter.register(vertx, testRest);
        router.mountSubRouter("/sub", router);
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testAdd(VertxTestContext context) {

        client.get(PORT, HOST, "/query/add?one=1&two=2")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("3", response.bodyAsString());
                context.completeNow();
            })));
    }

    @Test
    void testSubAdd(VertxTestContext context) {

        client.get(PORT, HOST, "/sub/query/add?one=1&two=2")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("3", response.bodyAsString());
                context.completeNow();
            })));
    }

    @Test
    void additionalParametersTest(VertxTestContext context) {

        client.get(PORT, HOST, "/query/add?one=3&two=5&three=3")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("8", response.bodyAsString());
                context.completeNow();
            })));
    }

    @Test
    void missingParametersTest(VertxTestContext context) {

        client.get(PORT, HOST, "/query/add?two=5&three=3")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                assertEquals("Missing @QueryParam(\"one\") for: /query/add", response.bodyAsString());
                context.completeNow();
            })));
    }

    @Test
    void queryTypeMismatchTest(VertxTestContext context) {

        client.get(PORT, HOST, "/query/add?one=A&two=2")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                assertEquals("Failed to convert value: 'A', to primitive type: int",
                             response.bodyAsString());
                context.completeNow();
            })));
    }

    @Test
    void queryNegativeTest(VertxTestContext context) {

        client.get(PORT, HOST, "/query/invert?negative=true&value=2.4")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("-2.4", response.bodyAsString());
                context.completeNow();
            })));
    }

    @Test
    void queryNegativeTest2(VertxTestContext context) {

        client.get(PORT, HOST, "/query/invert?negative=false&value=2.4")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("2.4", response.bodyAsString());
                context.completeNow();
            })));
    }

    @Test
    void echoDummyJsonTest(VertxTestContext context) {

        Dummy dummy = new Dummy("one", "dude");
        String json = JsonUtils.toJson(dummy);

        client.get(PORT, HOST, "/query/json?dummy=" + json)
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals(json, response.bodyAsString());
                context.completeNow();
            })));
    }

    @Test
    void emptyQueryParamTest(VertxTestContext context) {

        client.get(PORT, HOST, "/query/empty?empty")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("true", response.bodyAsString());
                context.completeNow();
            })));
    }

    @Test
    void emptyQueryParamMissingTest(VertxTestContext context) {

        client.get(PORT, HOST, "/query/empty")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("true", response.bodyAsString());
                context.completeNow();
            })));
    }

    @Test
    void decodeQueryParamMissingTest(VertxTestContext context) {

        client.get(PORT, HOST, "/query/decode?query=test+%7Bhello%7D")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("test {hello}", response.bodyAsString());
                context.completeNow();
            })));
    }

    @Test
    void decodeQueryWithPlus(VertxTestContext context) {

        client.get(PORT, HOST, "/query/decode?query=hello+world")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("hello world", response.bodyAsString());
                context.completeNow();
            })));
    }

    @Test
    void rawQueryParamMissingTest(VertxTestContext context) {

        client.get(PORT, HOST, "/query/decode?original=test+%7Bhello%7D")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("test+%7Bhello%7D", response.bodyAsString());
                context.completeNow();
            })));
    }
}
