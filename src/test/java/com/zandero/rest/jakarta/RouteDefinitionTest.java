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
class RouteDefinitionTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestRouteDefinitionRest.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testSimpleRegEx(VertxTestContext context) {

        client.get(PORT, HOST, "/route/definition").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("class java.lang.String, arg0, class com.zandero.rest.data.RouteDefinition, context", response.body());
                context.completeNow();
            })));
    }
}
