package com.zandero.rest;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;

/**
 *
 */
@Disabled
public class VertxTest {

    public static final String API_ROOT = "/";

    protected static final int PORT = 4444;

    public static final String HOST = "localhost";

    protected static Vertx vertx = null;
    protected static VertxTestContext vertxTestContext;
    protected static WebClient client;

    public static void before() {

        vertx = Vertx.vertx();
        vertxTestContext = new VertxTestContext();

        RestRouter.clearCache();

        client = WebClient.create(vertx);
    }

    public static void listenAndAwait(Router router) {
        listenAndAwait(router, PORT);
    }

    public static void listenAndAwait(Router router, int port) {
        try {
            vertx.createHttpServer()
                .requestHandler(router)
                .listen(port)
                .toCompletionStage()
                .toCompletableFuture()
                .get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start HTTP server on port " + port, e);
        }
    }

    @AfterAll
    static void close() {
        if (client != null) {
            client.close();
        }
        if (vertx != null) {
            vertx.close();
        }
    }
}
