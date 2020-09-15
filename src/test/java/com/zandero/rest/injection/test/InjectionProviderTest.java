package com.zandero.rest.injection.test;

import com.zandero.rest.*;
import com.zandero.rest.injection.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class InjectionProviderTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();
    }

    void startWith(InjectionProvider provider) {

        Router router = new RestBuilder(vertx)
                            .injectWith(provider)
                            .register(InjectedRest.class)
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    void startWithClass(Class<? extends InjectionProvider> provider) {

        Router router = new RestBuilder(vertx)
                            .injectWith(provider)
                            .register(InjectedRest.class)
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void testHasInjection() {

        assertTrue(InjectionProvider.hasInjection(GuiceInjectService.class));
        assertTrue(InjectionProvider.hasInjection(OtherServiceImpl.class));
        assertTrue(InjectionProvider.hasInjection(GuicedRest.class));

        assertTrue(InjectionProvider.hasInjection(OtherServiceImpl.class));
    }

    @Test
    void canBeInjected() {
        assertTrue(InjectionProvider.canBeInjected(UserServiceImpl.class));
    }

    @Test
    void guiceCallInjectedRestTest(VertxTestContext context) {

        startWith(new GuiceInjectionProvider());

        client.get(PORT, HOST, "/injected/dummy").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("I'm so dummy!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void guiceCallInjectedRestTest2(VertxTestContext context) {

        startWith(new GuiceInjectionProvider());

        client.get(PORT, HOST, "/injected/other").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Oh yes I'm so dummy!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void featherCallInjectedRestTest(VertxTestContext context) {

        startWith(new FeatherInjectionProvider());

        client.get(PORT, HOST, "/injected/dummy").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("I'm so dummy!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void featherCallInjectedRestTest2(VertxTestContext context) {

        startWith(new FeatherInjectionProvider());

        client.get(PORT, HOST, "/injected/other").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Oh yes I'm so dummy!", response.body());
                context.completeNow();
            })));
    }

    // class type tests
    @Test
    void guiceCallInjectedClassRestTest(VertxTestContext context) {

        startWithClass(GuiceInjectionProvider.class);

        client.get(PORT, HOST, "/injected/dummy").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("I'm so dummy!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void guiceCallInjectedClassRestTest2(VertxTestContext context) {

        startWithClass(GuiceInjectionProvider.class);

        client.get(PORT, HOST, "/injected/other").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Oh yes I'm so dummy!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void featherCallInjectedClassRestTest(VertxTestContext context) {

        startWithClass(FeatherInjectionProvider.class);

        client.get(PORT, HOST, "/injected/dummy").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("I'm so dummy!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void featherCallInjectedClassRestTest2(VertxTestContext context) {

        startWithClass(FeatherInjectionProvider.class);

        client.get(PORT, HOST, "/injected/other").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Oh yes I'm so dummy!", response.body());
                context.completeNow();
            })));
    }
}

