package com.zandero.rest.injection.test;


import com.zandero.rest.RestBuilder;
import com.zandero.rest.RestRouter;
import com.zandero.rest.VertxTest;
import com.zandero.rest.injection.FeatherInjectionProvider;
import com.zandero.rest.injection.GuiceInjectionProvider;
import com.zandero.rest.injection.InjectedServicesRest;
import com.zandero.rest.injection.InjectionProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
class InjectionServiceProviderTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();
    }

    @BeforeEach
    void cleanUp() {

        // clear all registered writers or reader and handlers
        RestRouter.validateWith((Validator) null);
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

