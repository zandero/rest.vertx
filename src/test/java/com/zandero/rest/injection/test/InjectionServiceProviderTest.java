package com.zandero.rest.injection.test;


import com.zandero.rest.*;
import com.zandero.rest.injection.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class InjectionServiceProviderTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();
    }

    @BeforeEach
    void cleanUp() {

        // clear registered injection provider
        RestRouter.injectWith((InjectionProvider) null);
    }

    void startWith(InjectionProvider provider) {

        Router router = new RestBuilder(vertx)
                            .injectWith(provider)
                            .register(InjectedServicesRest.class)
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    void startWithClass(Class<? extends InjectionProvider> provider) {

        Router router = new RestBuilder(vertx)
                            .injectWith(provider)
                            .register(InjectedServicesRest.class)
                            .build();

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(PORT);
    }

    @Test
    void guiceCallInjectedServiceRestTest(VertxTestContext context) {

        startWith(new GuiceInjectionProvider());

        client.get(PORT, HOST, "/getInstance/dummy").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("I'm so dummy!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void guiceCallInjectedServiceRestTest2(VertxTestContext context) {

        startWithClass(GuiceInjectionProvider.class);

        client.get(PORT, HOST, "/getInstance/other").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Oh yes I'm so dummy!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void featherCallInjectedServiceRestTest(VertxTestContext context) {

        startWith(new FeatherInjectionProvider());

        client.get(PORT, HOST, "/getInstance/dummy").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("I'm so dummy!", response.body());
                context.completeNow();
            })));
    }

    @Test
    void featherCallInjectedServiceRestTest2(VertxTestContext context) {

        startWithClass(FeatherInjectionProvider.class);

        client.get(PORT, HOST, "/getInstance/other").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                assertEquals("Oh yes I'm so dummy!", response.body());
                context.completeNow();
            })));
    }
}

