package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.jakarta.test.*;
import com.zandero.rest.test.json.*;
import com.zandero.utils.extra.*;
import io.vertx.core.buffer.*;
import io.vertx.ext.web.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RouteRegistrationTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        TestRest testRest = new TestRest();
        TestPostRest testPostRest = new TestPostRest();

        Router router = RestRouter.register(vertx, testRest, testPostRest);
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void registerTwoRoutesTest(VertxTestContext context) {

        // check if both are active
        Dummy json = new Dummy("test", "me");

        // 2nd REST
        client.post(PORT, HOST, "/post/json")
            .putHeader("content-type", "application/json")
            .sendBuffer(Buffer.buffer(JsonUtils.toJson(json)),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("{\"name\":\"Received-test\",\"value\":\"Received-me\"}", response.bodyAsString());
                            context.completeNow();
                        })));
    }

    @Test
    void registerTwoRoutesSeparate(VertxTestContext context) {

        // check if both are active
        Dummy json = new Dummy("test", "me");

        client.post(PORT, HOST, "/test/json/post")
            .putHeader("content-type", "application/json")
            .sendBuffer(Buffer.buffer(JsonUtils.toJson(json)),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("{\"name\":\"Received-test\",\"value\":\"Received-me\"}", response.bodyAsString());
                            context.completeNow();
                        })));

        // 2nd REST
        client.post(PORT, HOST, "/post/json")
            .putHeader("content-type", "application/json")
            .sendBuffer(Buffer.buffer(JsonUtils.toJson(json)),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("{\"name\":\"Received-test\",\"value\":\"Received-me\"}", response.bodyAsString());
                            context.completeNow();
                        })));
    }
}

