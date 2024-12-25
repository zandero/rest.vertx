package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.test.*;
import com.zandero.rest.test.json.*;
import com.zandero.utils.extra.*;
import io.vertx.core.buffer.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RouteWithPatchTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestPatchRest.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testCustomInput(VertxTestContext context) {

        Dummy json = new Dummy("test", "me");
        client.patch(PORT, HOST, "/patch/it").as(BodyCodec.string())
            .sendBuffer(Buffer.buffer(JsonUtils.toJson(json)), context.succeeding(response -> context.verify(() -> {

                assertEquals(200, response.statusCode());


                assertEquals("<custom>Received-test=Received-me</custom>", response.body()); // returns sorted list of unique words
                context.completeNow();
            })));
    }
}
