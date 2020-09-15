package com.zandero.rest;

import com.zandero.rest.injection.GuiceInjectionProvider;
import com.zandero.rest.test.TestEchoRest;
import com.zandero.rest.test.handler.*;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.junit5.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.*;

@ExtendWith(VertxExtension.class)
class RouteWithCorsTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();
    }

    @Test
    void corsTest(VertxTestContext context) {

        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("Access-Control-Request-Method");
        allowedHeaders.add("Access-Control-Allow-Credentials");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("Access-Control-Allow-Headers");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("Origin");

        Router router = new RestBuilder(vertx)
                            .injectWith(new GuiceInjectionProvider())
                            .enableCors("*", false, -1, allowedHeaders,
                                        HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS)
                            .register(TestEchoRest.class)
                            .notFound(".*", RestNotFoundHandler.class) // rest not found info
                            .notFound(NotFoundHandler.class) // last resort 404 page
                            .build();

        // TODO: create some test checking this

        context.completeNow();
    }
}
