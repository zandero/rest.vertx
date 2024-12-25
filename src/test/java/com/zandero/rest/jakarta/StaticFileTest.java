package com.zandero.rest.jakarta;

import com.zandero.rest.*;
import com.zandero.rest.jakarta.test.*;
import com.zandero.rest.test.handler.*;
import io.vertx.core.http.*;
import io.vertx.ext.web.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class StaticFileTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        Router router = new RestBuilder(vertx)
                .register(TestStaticFileRest.class)
                .build();

       // RestRouter.notFound(router, "rest", RestNotFoundHandler.class);
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
