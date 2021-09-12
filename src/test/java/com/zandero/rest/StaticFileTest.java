package com.zandero.rest;

import com.zandero.rest.test.TestStaticFileRest;
import com.zandero.rest.test.handler.FileNotFoundExceptionHandler;
import com.zandero.rest.test.handler.InheritedFromInheritedExceptionHandler;
import com.zandero.rest.test.handler.RestNotFoundHandler;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class StaticFileTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = new RestBuilder(vertx)
                .register(TestStaticFileRest.class)
                .build();

        RestRouter.getExceptionHandlers().register(FileNotFoundExceptionHandler.class);

        HttpServerOptions serverOptions = new HttpServerOptions();
        serverOptions.setCompressionSupported(true);

        vertx.createHttpServer(serverOptions)
                .requestHandler(router)
                .listen(PORT);
    }

    @Test
    void testGetIndex(VertxTestContext context) {

        client.get(PORT, HOST, "/docs/index.html")
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals(200, response.statusCode());
                    assertEquals("<html>\n" +
                            "    <body>\n" +
                            "        <p>Hello</p>\n" +
                            "    </body>\n" +
                            "</html>", response.bodyAsString());
                    context.completeNow();
                })));
    }

    @Test
    void fileNotFoundTest(VertxTestContext context) {

        client.get(PORT, HOST, "/docs/notExistent/file.html")
                .send(context.succeeding(response -> context.verify(() -> {
                    assertEquals(404, response.statusCode());
                    assertEquals("html/notExistent", response.bodyAsString());
                    context.completeNow();
                })));
    }
}
