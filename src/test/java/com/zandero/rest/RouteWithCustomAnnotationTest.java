package com.zandero.rest;

import com.google.inject.Module;
import com.zandero.rest.injection.*;
import com.zandero.rest.test.TestIssue55Rest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class RouteWithCustomAnnotationTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        RestBuilder builder = new RestBuilder(vertx)
                                  .injectWith(new GuiceInjectionProvider(getModules()))
                                  .register(TestIssue55Rest.class);

        vertx.createHttpServer()
            .requestHandler(builder.build())
            .listen(PORT);
    }

    private static Module[] getModules() {
        return new Module[]{
            new GuiceAdminModule()
        };
    }

    @Test
    void testDeleteWithBody(VertxTestContext context) {

        Dummy json = new Dummy("test", "me");
        client.delete(PORT, HOST, "/system/user").as(BodyCodec.string())
            .sendBuffer(Buffer.buffer(JsonUtils.toJson(json)), context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("[delete]", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testGetEcho(VertxTestContext context) {

        client.get(PORT, HOST, "/system/user/echo2").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Hello echo 2", response.body());
                context.completeNow();
            })));
    }
}
