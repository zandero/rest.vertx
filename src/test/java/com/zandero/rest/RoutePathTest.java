package com.zandero.rest;

import com.zandero.rest.test.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.ext.web.handler.*;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class RoutePathTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        TestPathRest testRest = new TestPathRest();

        Router router = Router.router(vertx);

        // The handler
        router.route().handler(LoggerHandler.create(true, LoggerFormat.DEFAULT));
        RestRouter.register(router, testRest);

        //Router router = RestRouter.register(vertx, testRest);

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void rootWithRootPathTest(VertxTestContext context) {

        client.get(PORT, HOST, "/query/echo/this").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("querythis", response.body());
                context.completeNow();
            })));
    }

    @Test
    void rootWithRootPathTest2(VertxTestContext context) {

        client.get(PORT, HOST, "/this/echo/query").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("thisquery", response.body());
                context.completeNow();
            })));
    }

    @Test
    void rootWithoutPathTest(VertxTestContext context) {

        client.get(PORT, HOST, "/this").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("this", response.body());
                context.completeNow();
            })));
    }

    @Test
    void invalidDuplicateMethodRestTest() {

        Exception e = assertThrows(IllegalArgumentException.class, () -> RestRouter.register(vertx, TestInvalidMethodRest.class));
        assertEquals("com.zandero.rest.test.TestInvalidMethodRest.echo() - Method already set to: POST!", e.getMessage());
    }

    @Test
    void invalidDoubleBodyRestTest() {

        Exception e = assertThrows(IllegalArgumentException.class, () -> RestRouter.register(vertx, TestDoubleBodyParamRest.class));
        assertEquals("com.zandero.rest.test.TestDoubleBodyParamRest.echo(String arg0, String arg1) - to many body arguments given. " +
                         "Missing argument annotation (@PathParam, @QueryParam, @FormParam, @HeaderParam, @CookieParam or @Context) for: unknown arg1!",
                     e.getMessage());
    }

    @Test
    void noPathRestTest() {

        Exception e = assertThrows(IllegalArgumentException.class, () -> RestRouter.register(vertx, TestMissingPathRest.class));
        assertEquals("com.zandero.rest.test.TestMissingPathRest.echo() - Missing route @Path!", e.getMessage());
    }
}

