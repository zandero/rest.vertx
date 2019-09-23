package com.zandero.rest;

import com.zandero.rest.reader.IntegerBodyReader;
import com.zandero.rest.test.TestIncompatibleReaderRest;
import com.zandero.rest.test.TestIncompatibleWriterRest;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.TestRestWithNonRestMethod;
import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic GET, POST tests
 */

@ExtendWith(VertxExtension.class)
class RestRouterTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        TestRest testRest = new TestRest();

        Router router = RestRouter.register(vertx,
                testRest,
                TestRestWithNonRestMethod.class);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(PORT, vertxTestContext.succeeding());
    }

    @Test
    void registerTestRest(VertxTestContext context) {

        client.get(PORT, HOST, "/test/echo")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals("Hello world!", response.body());
                    context.completeNow();
                })));
    }

    @Test
    void jaxResponseTest(VertxTestContext context) {

        client.get(PORT, HOST, "/test/jax")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals(202, response.statusCode());
                    assertEquals("Test", response.getHeader("X-Test"));
                    assertEquals("\"Hello\"", response.body()); // produces JSON ... so quotes are correct
                    context.completeNow();
                })));
    }

    @Test
    void pathMatchTest(VertxTestContext context) {

        client.get(PORT, HOST, "/test/match/hello/world")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals(200, response.statusCode());
                    assertEquals("\"hello/world\"", response.body());
                    context.completeNow();
                })));
    }

    @Test
    void pathMatch2Test(VertxTestContext context) {

        client.get(PORT, HOST, "/test/match2/hello/world")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals(200, response.statusCode());
                    assertEquals("\"hello/world\"", response.body());
                    context.completeNow();
                })));
    }

    @Test
    void testNonStringParameters(VertxTestContext context) {

        client.get(PORT, HOST, "/test/mix/2/true")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

                    assertEquals(200, response.statusCode());
                    assertEquals("\"2/true\"", response.body());
                    context.completeNow();
                })));
    }

    @Test
    void testNonStringParameters2(VertxTestContext context) {

        client.get(PORT, HOST, "/test/mix2/2/a")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

                    assertEquals(200, response.statusCode());
                    assertEquals("\"2/a\"", response.body());
                    context.completeNow();
                })));
    }

    @Test
    void testJsonPost(VertxTestContext context) {

        Dummy dummy = new Dummy("hello", "world");
        String json = JsonUtils.toJson(dummy);

        client.post(PORT, HOST, "/test/json/post")
                .as(BodyCodec.string())
                .putHeader("Content-Type", "application/json")
                .sendBuffer(Buffer.buffer(json), context.succeeding(response -> context.verify(() -> {

                    assertEquals(200, response.statusCode());
                    assertEquals("{\"name\":\"Received-hello\",\"value\":\"Received-world\"}", response.body());
                    context.completeNow();
                })));
    }

    @Test
    void contextTest(VertxTestContext context) {

        client.get(PORT, HOST, "/test/context/path")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

                    assertEquals(200, response.statusCode());
                    assertEquals("http://localhost:4444/test/context/path", response.body());
                    context.completeNow();
                })));
    }

    @Test
    void nullTest(VertxTestContext context) {

        client.get(PORT, HOST, "/test/context/null")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {

                    assertEquals(200, response.statusCode());
                    assertNull(response.body());
                    context.completeNow();
                })));
    }

    @Test
    void invalidReaderRegistrationTest() {

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> RestRouter.getReaders().register(List.class, IntegerBodyReader.class));
        assertEquals("Incompatible types: 'interface java.util.List' and: 'class java.lang.Integer' using: 'class com.zandero.rest.reader.IntegerBodyReader'!", e.getMessage());
    }

    @Test
    void incompatibleReaderTypeTest() {

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> RestRouter.register(vertx, TestIncompatibleReaderRest.class));
        assertEquals("POST /incompatible/ouch - Parameter type: 'class java.lang.String' " +
                "not matching reader type: 'class java.lang.Integer' in: 'class com.zandero.rest.reader.IntegerBodyReader'!", e.getMessage());

    }

    @Test
    void incompatibleWriterTypeTest() {

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> RestRouter.register(vertx, TestIncompatibleWriterRest.class));
        assertEquals("GET /incompatible/ouch - Response type: 'class java.lang.String' " +
                "not matching writer type: 'class com.zandero.rest.test.json.Dummy' in: 'class com.zandero.rest.writer.TestDummyWriter'!", e.getMessage());
    }

    @Test
    void testRestWithNonAnnotatedMethod(VertxTestContext context) {

        client.get(PORT, HOST, "/mixed/echo")
                .as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals(200, response.statusCode());
                    assertEquals("hello", response.body());
                    context.completeNow();
                })));
    }
}
