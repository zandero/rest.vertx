package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.handler.*;
import com.zandero.rest.injection.*;
import com.zandero.rest.test.*;
import io.vertx.core.buffer.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RouteAuthorizationInjectionTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        // 2. REST with @RolesAllowed annotations
        Router router = new RestBuilder(vertx)
                            .injectWith(new GuiceInjectionProvider())
                            .provide(UserHandler.class)
                            .register(TestAuthorizationRest.class)
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
                assertEquals(200, response.statusCode());
                assertEquals("all", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetNobody(VertxTestContext context) {

        client.get(PORT, HOST, "/private/nobody")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(403, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testGetUserNonAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/user")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(403, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testGetUserAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/user")
            .as(BodyCodec.string())
            .putHeader("X-Token", "user")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("user", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetAdminAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/admin")
            .as(BodyCodec.string())
            .putHeader("X-Token", "admin")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("admin", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetAdminUnAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/admin")
            .as(BodyCodec.string())
            .putHeader("X-Token", "user")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(403, response.statusCode());
                assertEquals("HTTP 403 Forbidden", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetOtherUnAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/other")
            .as(BodyCodec.string())
            .putHeader("X-Token", "user")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(403, response.statusCode());
                assertEquals("HTTP 403 Forbidden", response.body());
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


        client.get(PORT, HOST, "/private/other")
            .as(BodyCodec.string())
            .putHeader("X-Token", "two")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("{\"role\":\"two\"}", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testPostUserAuthorized(VertxTestContext context) {


        client.post(PORT, HOST, "/private/user")
            .as(BodyCodec.string())
            .putHeader("X-Token", "user")
            .sendBuffer(Buffer.buffer("HELLO"), context.succeeding(response -> context.verify(() -> {
                assertEquals("HELLO", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testPostUserForbidden(VertxTestContext context) {

        client.post(PORT, HOST, "/private/user")
            .as(BodyCodec.string())
            .sendBuffer(Buffer.buffer("HELLO"), context.succeeding(response -> context.verify(() -> {
                assertEquals("HTTP 403 Forbidden", response.body());
                assertEquals(403, response.statusCode());
                context.completeNow();
            })));
    }
}

