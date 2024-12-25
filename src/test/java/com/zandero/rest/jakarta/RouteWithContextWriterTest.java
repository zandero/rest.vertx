package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.test.*;
import com.zandero.rest.test.json.*;
import com.zandero.rest.writer.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RouteWithContextWriterTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = RestRouter.register(vertx, TestWithXmlRest.class);
        RestRouter.getWriters().register(User.class, MyXmlWriter.class);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void textXml(VertxTestContext context) {


        client.get(PORT, HOST, "/xml/test").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {

                assertEquals(200, response.statusCode());

                String header = response.getHeader("Content-Type");
                assertEquals("text/xml", header); // writer always overrides definition

                header = response.getHeader("Cache-Control");
                assertEquals("private,no-cache,no-store", header);

                assertEquals("<u name=\"test\" />", response.body());
                context.completeNow();
            })));
    }

    @Test
    void textXmlWriterAddsHeader(VertxTestContext context) {

        client.get(PORT, HOST, "/xml/test2").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());

                String header = response.getHeader("Content-Type");
                assertEquals("text/xml", header);

                assertEquals("<u name=\"test\" />", response.body());
                context.completeNow();
            })));
    }
}

