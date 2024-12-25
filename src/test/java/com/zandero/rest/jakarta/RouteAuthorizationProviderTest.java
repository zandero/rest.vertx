package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.test.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testGetUserNonAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/user")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                assertEquals("Missing authentication token", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetOtherUserNonAuthorized(VertxTestContext context) {

        client.get(PORT, HOST, "/private/other_user")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                assertEquals("HTTP 400 just for you", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetOtherUserWrongToken(VertxTestContext context) {

        client.get(PORT, HOST, "/private/other_user")
            .as(BodyCodec.string())
            .putHeader("X-Token", "Bla")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(406, response.statusCode());
                assertEquals("HTTP 406 no entry for you", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetOtherUserOK(VertxTestContext context) {

        client.get(PORT, HOST, "/private/other_user")
            .as(BodyCodec.string())
            .putHeader("X-Token", "LetMeIn")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("other_user", response.body());
                context.completeNow();
            })));
    }
}
