package com.zandero.rest;

import com.zandero.rest.test.ImplementationRest;
import com.zandero.rest.test.data.SimulatedUser;
import io.vertx.core.Handler;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class InheritedRouteTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();

        Router router = Router.router(vertx);
        router.route().handler(getUserHandler());
        RestRouter.register(router, ImplementationRest.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    private static Handler<RoutingContext> getUserHandler() {

        return context -> {

            // read header ... if present ... create user with given value
            String token = context.request().getHeader("X-Token");

            // set user ...
            if (token != null) {
                context.setUser(new SimulatedUser(token));
            }

            context.next();
        };
    }

    @Test
    void echoTest(VertxTestContext context) {

        client.get(PORT, HOST, "/implementation/echo?name=test")
            .as(BodyCodec.string())
            .putHeader("X-Token", "admin")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("\"test\"", response.body()); // JsonExceptionWriter
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void getTest(VertxTestContext context) {

        client.get(PORT, HOST, "/implementation/get/test?additional=it")
            .as(BodyCodec.string())
            .putHeader("X-Token", "test")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("\"testit\"", response.body()); // JsonExceptionWriter
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }
}
