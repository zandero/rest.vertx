package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.injection.*;
import com.zandero.rest.test.*;
import com.zandero.rest.test.handler.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RouteWithContextInjectionTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = new RestBuilder(vertx)
                            .injectWith(new GuiceInjectionProvider())
                            .addProvider(SimulatedUserProvider.class)
                            .errorHandler(MyExceptionHandler.class)
                            .register(vertx, TestContextInjectedRest.class)
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testGetRouteContext(VertxTestContext context) {

        client.get(PORT, HOST, "/context/user").as(BodyCodec.string())
            .putHeader("X-Token", "Test")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Test", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testFailToGetRouteContext(VertxTestContext context) {

        client.get(PORT, HOST, "/context/user").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("Exception: No user present!", response.body());
                assertEquals(404, response.statusCode());
                context.completeNow();
            })));
    }
}