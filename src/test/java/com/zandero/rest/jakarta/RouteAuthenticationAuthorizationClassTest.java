package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.jakarta.test.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RouteAuthenticationAuthorizationClassTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestAuthorizationProviderClassRest.class);
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void missingCredentials(VertxTestContext context) {

        client.get(PORT, HOST, "/private/access")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("HTTP 400 just for you", response.body());
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void wrongCredentials(VertxTestContext context) {

        client.get(PORT, HOST, "/private/access")
            .as(BodyCodec.string())
            .putHeader("x-token", "bla")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("HTTP 406 no entry for you", response.body());
                assertEquals(406, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void allowAccess(VertxTestContext context) {

        client.get(PORT, HOST, "/private/access")
            .as(BodyCodec.string())
            .putHeader("x-token", "LetMeIn")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("access granted", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void second_missingCredentials(VertxTestContext context) {

        client.get(PORT, HOST, "/private/secondAccess")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("HTTP 400 missing credentials", response.body());
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void second_wrongCredentials(VertxTestContext context) {

        client.get(PORT, HOST, "/private/secondAccess")
            .as(BodyCodec.string())
            .putHeader("X-Token-The-Seconds", "bla")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("HTTP 406 who are you", response.body());
                assertEquals(406, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void second_allowAccess(VertxTestContext context) {

        client.get(PORT, HOST, "/private/secondAccess")
            .as(BodyCodec.string())
            .putHeader("X-Token-The-Seconds", "IKnowThePassword")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("access granted", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void third_missingCredentials(VertxTestContext context) {

        client.get(PORT, HOST, "/private/thirdAccess")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("HTTP 400 just for you", response.body());
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void third_wrongCredentials(VertxTestContext context) {

        client.get(PORT, HOST, "/private/thirdAccess")
            .as(BodyCodec.string())
            .putHeader("X-Token", "bla")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("HTTP 406 no entry for you", response.body());
                assertEquals(406, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void third_allowAccess(VertxTestContext context) {

        client.get(PORT, HOST, "/private/thirdAccess")
            .as(BodyCodec.string())
            .putHeader("X-Token", "SuperSecretPassword")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("access granted", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void fourth_missingCredentials(VertxTestContext context) {

        client.get(PORT, HOST, "/private/fourthAccess")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("HTTP 400 missing credentials", response.body());
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void fourth_wrongCredentials(VertxTestContext context) {

        client.get(PORT, HOST, "/private/fourthAccess")
            .as(BodyCodec.string())
            .putHeader("X-Token-The-Seconds", "bla")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("HTTP 406 who are you", response.body());
                assertEquals(406, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void fourth_allowAccess(VertxTestContext context) {

        client.get(PORT, HOST, "/private/fourthAccess")
            .as(BodyCodec.string())
            .putHeader("X-Token-The-Seconds", "SuperSecretPassword")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("access granted", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

}
