package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.authorization.*;
import com.zandero.rest.jakarta.test.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RouteNewAuthorizationTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        RestBuilder builder = new RestBuilder(vertx)
                                  .authenticateWith(MyAuthenticator.class)
                                  .authorizeWith(new TestAuthorizationProvider())
                                  .register(TestEchoRest.class);

        vertx.createHttpServer()
            .requestHandler(builder.build())
            .listen(PORT);
    }

    @Test
    void echoBadRequest(VertxTestContext context) {

        // call and check response
        client.get(PORT, HOST, "/rest/echo")
            .as(BodyCodec.string())
            .putHeader("X-Token", "two")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                assertEquals("HTTP 400 Bad Request", response.body());
                context.completeNow();
            })));
    }

    @Test
    void echoOK(VertxTestContext context) {

        // call and check response
        client.get(PORT, HOST, "/rest/echo")
            .as(BodyCodec.string())
            .putHeader("X-Token", "LetMeIn")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("\"echo\"", response.body());
                context.completeNow();
            })));
    }
}
