package com.zandero.resttest;

import com.zandero.rest.RestRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.*;

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

    @AfterEach
    void lastChecks(Vertx vertx) {
        vertx.close(vertxTestContext.succeedingThenComplete());
    }

    @AfterAll
    static void close() {
        vertx.close();
    }
}
