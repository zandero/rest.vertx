package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.test.*;
import io.vertx.core.buffer.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RouteWithBeanParamTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();
        Router router = RestRouter.register(vertx, TestBeanReaderRest.class);
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void postBean(VertxTestContext context) {

        client.post(PORT, HOST, "/bean/read/result;one=1;enum=two?query=1").as(BodyCodec.string())
            .putHeader("MyHeader", "true")
            .putHeader("Cookie", "chocolate=tasty")
            .send(context.succeeding(response -> context.verify(() -> {

                assertEquals(200, response.statusCode());
                assertEquals("Header: true, " +
                                 "Path: result;one=1;enum=two, " +
                                 "Query: 1, " +
                                 "Cookie: tasty, " +
                                 "Matrix: two, " +
                                 "one: 1, " +
                                 "Body: empty", response.body());

                context.completeNow();
            })));
    }

    @Test
    void postWithBody(VertxTestContext context) {

        client.post(PORT, HOST, "/bean/read/result;one=1;enum=one?query=10").as(BodyCodec.string())
            .putHeader("MyHeader", "true")
            .putHeader("Cookie", "chocolate=tasty")
            .as(BodyCodec.string())
            .sendBuffer(Buffer.buffer("BLA"),
                        context.succeeding(response -> context.verify(() -> {

                            assertEquals(200, response.statusCode());
                            assertEquals("Header: true, " +
                                             "Path: result;one=1;enum=one, " +
                                             "Query: 10, " +
                                             "Cookie: tasty, " +
                                             "Matrix: one, " +
                                             "one: 1, " +
                                             "Body: BLA", response.body());

                            context.completeNow();
                        })));
    }

    @Test
    void getBean(VertxTestContext context) {

        client.get(PORT, HOST, "/bean/write/result;one=1;enum=two?query=one+two").as(BodyCodec.string())
            .putHeader("MyHeader", "true")
            .putHeader("Cookie", "chocolate=tasty")
            .send(context.succeeding(response -> context.verify(() -> {

                assertEquals(200, response.statusCode());
                assertEquals("Header: true, " +
                                 "Path: result;one=1;enum=two, " +
                                 "Query: one+two, " +
                                 "Cookie: tasty, " +
                                 "Matrix: two, " +
                                 "one: 1, " +
                                 "Body: empty", response.body());

                context.completeNow();
            })));
    }

    @Test
    void postBeanComplex(VertxTestContext context) {

        client.post(PORT, HOST, "/bean/complex/read/result;one=1;enum=two?query=1").as(BodyCodec.string())
            .putHeader("MyHeader", "true")
            .putHeader("Cookie", "chocolate=tasty")
            .send(context.succeeding(response -> context.verify(() -> {

                assertEquals(200, response.statusCode());
                assertEquals("Header: true, " +
                                 "Path: result;one=1;enum=two, " +
                                 "Query: 1, " +
                                 "Cookie: tasty, " +
                                 "Matrix: two",
                             response.body());

                context.completeNow();
            })));
    }
}
