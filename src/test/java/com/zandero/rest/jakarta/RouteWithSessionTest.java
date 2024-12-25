package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.test.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RouteWithSessionTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = Router.router(vertx);
        SessionHandler handler = SessionHandler.create(LocalSessionStore.create(vertx));
        router.route().handler(handler);
        RestRouter.register(router, TestSessionRest.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testResponseSession(VertxTestContext context) {

        client.get(PORT, HOST, "/session/echo").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertNotNull(response.body());
                context.completeNow();
            })));
    }
}

