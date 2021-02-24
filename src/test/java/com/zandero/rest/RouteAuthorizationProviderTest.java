package com.zandero.rest;

import com.zandero.rest.test.TestAuthorizationProviderRest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class RouteAuthorizationProviderTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestAuthorizationProviderRest.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

   /* private static Handler<RoutingContext> getUserHandler() {

        return context -> {

            // read header ... if present ... create user with given value
            String token = context.request().getHeader("X-Token");

            // set user ...
            if (token != null) {
                context.setUser(new SimulatedUser(token));
            }

            context.next();
        };
    }*/

    @Test
    void testGetAllDefaultAllow(VertxTestContext context) {

        client.get(PORT, HOST, "/private/all_default")
            .as(BodyCodec.string())
            .putHeader("X-Token", "user")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("all", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetAllDefaultDeny(VertxTestContext context) {

        client.get(PORT, HOST, "/private/all_default")
            .as(BodyCodec.string())
            .putHeader("X-Token", "LetMeIn")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(403, response.statusCode());
                assertEquals("HTTP 403 Forbidden", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetAll(VertxTestContext context) {

        client.get(PORT, HOST, "/private/all")
            .as(BodyCodec.string())
            .putHeader("X-Token", "LetMeIn")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("all", response.body());
                context.completeNow();
            })));
    }

   /* @Test
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
    }*/
}