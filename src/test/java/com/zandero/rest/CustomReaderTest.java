package com.zandero.rest;

import com.zandero.rest.RestRouter;
import com.zandero.rest.VertxTest;
import com.zandero.rest.reader.CustomWordListReader;
import com.zandero.rest.reader.DummyBodyReader;
import com.zandero.rest.reader.ExtendedDummyBodyReader;
import com.zandero.rest.reader.IntegerBodyReader;
import com.zandero.rest.test.TestPostRest;
import com.zandero.rest.test.TestReaderRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.test.json.ExtendedDummy;
import com.zandero.rest.writer.TestCustomWriter;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 */

@ExtendWith(VertxExtension.class)
class CustomReaderTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestReaderRest.class);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(PORT);
    }

    @Test
    void testCustomInput(VertxTestContext context) {

        client.post(PORT, HOST, "/read/custom")
                .as(BodyCodec.string())
                .sendBuffer(Buffer.buffer("The quick brown fox jumps over the red dog!"),
                        context.succeeding(response -> context.verify(() -> {

                            assertEquals(200, response.statusCode());
                            assertEquals("brown,dog,fox,jumps,over,quick,red,the", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));
    }

    @Test
    void testCustomInput_WithBuilder(VertxTestContext context) {

        client.post(PORT, HOST, "/read/custom")
                .as(BodyCodec.string())
                .sendBuffer(Buffer.buffer("The quick brown fox jumps over the red dog!"),
                        context.succeeding(response -> context.verify(() -> {

                            assertEquals(200, response.statusCode());
                            assertEquals("brown,dog,fox,jumps,over,quick,red,the", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));
    }

    @Test
    void testCustomInput_2(VertxTestContext context) {

        RestRouter.getReaders()
                .register(List.class, CustomWordListReader.class); // all arguments that are List<> go through this reader ... (reader returns List<String> as output)

        client.post(PORT, HOST, "/read/registered")
                .as(BodyCodec.string())
                .sendBuffer(Buffer.buffer("The quick brown fox jumps over the red dog!"),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("brown,dog,fox,jumps,over,quick,red,the", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));
    }

    @Test
    void testExtendedReader(VertxTestContext context) {

        RestRouter.getReaders().register(Dummy.class, DummyBodyReader.class);
        RestRouter.getReaders().register(ExtendedDummy.class, ExtendedDummyBodyReader.class);

        // check if correct reader is used

        client.post(PORT, HOST, "/read/normal/dummy")
                .as(BodyCodec.string())
                .putHeader("Content-Type", "application/json")
                .sendBuffer(Buffer.buffer(JsonUtils.toJson(new Dummy("one", "dummy"))),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("one=dummy", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));

        // 2nd send extended dummy to same REST
        client.post(PORT, HOST, "/read/normal/dummy")
                .as(BodyCodec.string())
                .putHeader("Content-Type", "application/json")
                .sendBuffer(Buffer.buffer(JsonUtils.toJson(new ExtendedDummy("one", "dummy", "extra"))),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("one=dummy", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));

        // 3rd send extended dummy to extended REST
        client.post(PORT, HOST, "/read/extended/dummy")
                .as(BodyCodec.string())
                .putHeader("Content-Type", "application/json")
                .sendBuffer(Buffer.buffer(JsonUtils.toJson(new ExtendedDummy("one", "dummy", "extra"))),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("one=dummy (extra)", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));


        // 4th send normal dummy to extended REST
        client.post(PORT, HOST, "/read/extended/dummy")
                .as(BodyCodec.string())
                .putHeader("Content-Type", "application/json")
                .sendBuffer(Buffer.buffer(JsonUtils.toJson(new Dummy("one", "dummy"))),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("one=dummy (null)", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));
    }

    @Test
    void extendedContentTypeTest(VertxTestContext context) {

        RestRouter.getReaders().register(Dummy.class, DummyBodyReader.class);

        client.post(PORT, HOST, "/read/normal/dummy")
                .as(BodyCodec.string())
                .putHeader("Content-Type", "application/json;charset=UTF-8")
                .sendBuffer(Buffer.buffer(JsonUtils.toJson(new Dummy("one", "dummy"))),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("one=dummy", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));
    }

    @Test
    void extendedContentTypeByMediaType(VertxTestContext context) {

        // register reader afterwards - should still work
        RestRouter.getReaders().register("application/json", DummyBodyReader.class);

        client.post(PORT, HOST, "/read/normal/dummy")
                .as(BodyCodec.string())
                .putHeader("Content-Type", "application/json")
                .sendBuffer(Buffer.buffer(JsonUtils.toJson(new Dummy("one", "dummy"))),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("one=dummy", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));
    }

    @Test
    void extendedContentTypeByAnnotatedMediaType(VertxTestContext context) {

        // register reader afterwards - should still work
        RestRouter.getReaders().register(DummyBodyReader.class);

        client.post(PORT, HOST, "/read/normal/dummy")
                .as(BodyCodec.string())
                .putHeader("Content-Type", "application/json")
                .sendBuffer(Buffer.buffer(JsonUtils.toJson(new Dummy("one", "dummy"))),
                        context.succeeding(response -> context.verify(() -> {
                            assertEquals(200, response.statusCode());
                            assertEquals("one=dummy", response.body()); // returns sorted list of unique words
                            context.completeNow();
                        })));
    }

    @Test
    void incompatibleMimeTypeReaderTest() {

        // register application/json to IntegerReader
        RestRouter.getReaders().register("application/json", IntegerBodyReader.class);

        // bind rest that consumes application/json
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> RestRouter.register(vertx, TestPostRest.class));
        assertEquals(
                "POST /post/json - Parameter type: 'class com.zandero.rest.test.json.Dummy' not matching reader type: " +
                        "'class java.lang.Integer' in: 'class com.zandero.rest.reader.IntegerBodyReader'!",
                e.getMessage());
    }

    @Test
    void incompatibleMimeTypeWriterTest() {

        // register application/json to String writer
        RestRouter.getWriters().register("application/json", TestCustomWriter.class);

        // bind rest that consumes application/json
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> RestRouter.register(vertx, TestPostRest.class));
        assertEquals(
                "POST /post/json - Response type: 'class com.zandero.rest.test.json.Dummy' not matching writer type: " +
                        "'class java.lang.String' in: 'class com.zandero.rest.writer.TestCustomWriter'!",
                e.getMessage());
    }
}
