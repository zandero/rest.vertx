package com.zandero.rest;

import com.zandero.rest.handler.MyUserHandler;
import com.zandero.rest.test.TestAuthorizationRest;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteAuthorizationWithClassHandlerTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        // REST with @RolesAllowed annotations
        TestAuthorizationRest testRest = new TestAuthorizationRest();
        Router router = new RestBuilder(vertx)
                            .routeHandler(MyUserHandler.class)
                            .register(testRest)
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testGetAll(VertxTestContext context) {

        client.get(PORT, HOST, "/private/all")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("all", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testGetNobody(VertxTestContext context) {

        client.get(PORT, HOST, "/private/nobody")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(401, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testGetUserNonAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/user")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(401, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testGetUserAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/user")
            .as(BodyCodec.string())
            .putHeader("X-Token", "user")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("user", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testPostUserAuthorized(VertxTestContext context) {

        client.post(PORT, HOST, "/private/user")
            .as(BodyCodec.string())
            .putHeader("X-Token", "user")
            .sendBuffer(Buffer.buffer("HELLO"), context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("HELLO", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testPostUserUnauthorized(VertxTestContext context) {

        client.post(PORT, HOST, "/private/user")
            .as(BodyCodec.string())
            .sendBuffer(Buffer.buffer("HELLO"), context.succeeding(response -> context.verify(() -> {
                assertEquals("HTTP 401 Unauthorized", response.body());
                assertEquals(401, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testGetAdminAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/admin")
            .as(BodyCodec.string())
            .putHeader("X-Token", "admin")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("admin", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testGetAdminUnAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/admin")
            .as(BodyCodec.string())
            .putHeader("X-Token", "user")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(401, response.statusCode());
                assertEquals("HTTP 401 Unauthorized", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetOtherUnAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/other")
            .as(BodyCodec.string())
            .putHeader("X-Token", "user")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(401, response.statusCode());
                assertEquals("HTTP 401 Unauthorized", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetOtherOneAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/other")
            .as(BodyCodec.string())
            .putHeader("X-Token", "one")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("{\"role\":\"one\"}", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetOtherTwoAuthorized(VertxTestContext context) {

        // call and check response
        client.get(PORT, HOST, "/private/other")
            .as(BodyCodec.string())
            .putHeader("X-Token", "two")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("{\"role\":\"two\"}", response.body());
                context.completeNow();
            })));
    }
}
