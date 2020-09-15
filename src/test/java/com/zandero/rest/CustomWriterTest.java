package com.zandero.rest;

import com.zandero.rest.test.*;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.*;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 */
@ExtendWith(VertxExtension.class)
class CustomWriterTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx,
                                            TestRest.class,
                                            TestHtmlRest.class,
                                            TestPostRest.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testCustomOutput(VertxTestContext context) {

        client.get(PORT, HOST, "/test/custom")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("<custom>CUSTOM</custom>", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testRegisteredContentTypeWriterOutput(VertxTestContext context) {

        RestRouter.getWriters().register(MediaType.TEXT_HTML, TestCustomWriter.class); // bind media type to this writer

        client.get(PORT, HOST, "/html/body")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals(MediaType.TEXT_HTML, response.getHeader("Content-Type"));
                assertEquals("<custom>body</custom>", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testProducesOnExperimentalGet(VertxTestContext context) {

        RestRouter.getWriters().register(MediaType.TEXT_HTML, TestCustomWriter.class); // bind media type to this writer

        client.get(PORT, HOST, "/html/head")
            .as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {

                assertEquals(200, response.statusCode());
                assertEquals(MediaType.TEXT_HTML, response.getHeader("Content-Type"));

                assertEquals("<custom>head</custom>", response.body());
                context.completeNow();
            })));
    }

    @Test
    void testRegisteredClassTypeWriterOutput(VertxTestContext context) {

        RestRouter.getWriters().register(Dummy.class, TestDummyWriter.class); // bind media type to this writer


        Dummy dummy = new Dummy("hello", "world");
        String json = JsonUtils.toJson(dummy);

        client.post(PORT, HOST, "/post/json")
            .as(BodyCodec.string())
            .putHeader("Content-Type", "application/json")
            .sendBuffer(Buffer.buffer(json),
                        context.succeeding(response -> context.verify(() -> {

                            assertEquals(200, response.statusCode());
                            assertEquals("application/json;charset=utf-8", response.getHeader("Content-Type"));
                            assertEquals("<custom>Received-hello=Received-world</custom>", response.body());
                            context.completeNow();
                        })));
    }

    @Test
    void testRegisteredClassTypeReaderOutput(VertxTestContext context) {

        Dummy dummy = new Dummy("hello", "world");
        String json = JsonUtils.toJson(dummy);

        client.put(PORT, HOST, "/post/json")
            .as(BodyCodec.string())
            .putHeader("Content-Type", "application/json")
            .sendBuffer(Buffer.buffer(json), context.succeeding(response -> context.verify(() -> {

                assertEquals(200, response.statusCode());
                assertEquals(MediaType.APPLICATION_JSON, response.getHeader("Content-Type"));
                assertEquals("<custom>Received-hello=Received-world</custom>", response.body());
                context.completeNow();
            })));
    }
}
