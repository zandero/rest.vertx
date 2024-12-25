package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.reader.*;
import com.zandero.rest.test.*;
import com.zandero.rest.test.data.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
public class RouteWithEnumsTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = new RestBuilder(vertx)
                            .register(TestEnumRest.class)
                            .reader(MyEnum.class, MyEnumReader.class)
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void valueOfTest(VertxTestContext context) {

        client.get(PORT, HOST, "/enum/simple/one").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("one", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void valueOfReader(VertxTestContext context) {

        client.get(PORT, HOST, "/enum/reader/1").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("one", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }


    @Test
    void fromStringTest(VertxTestContext context) {

        client.get(PORT, HOST, "/enum/fromString/3").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("three", response.body());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }
}
