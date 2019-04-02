package com.zandero.rest;

import com.zandero.rest.injection.InjectionProvider;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxTestContext;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;

import javax.validation.Validator;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
@Ignore
public class VertxTest {

    public static final String API_ROOT = "/";

    protected static final int PORT = 4444;

    static final String HOST = "localhost";

    protected static final String ROOT_PATH = "http://" + HOST + ":" + PORT;

    protected Vertx vertx;
    protected VertxTestContext testContext;
    protected WebClient client;

    public void before() {

        vertx = Vertx.vertx();
        testContext = new VertxTestContext();

        // clear all registered writers or reader and handlers
        RestRouter.getReaders().clear();
        RestRouter.getWriters().clear();
        RestRouter.getExceptionHandlers().clear();
        RestRouter.getContextProviders().clear();
        // clear
        RestRouter.validateWith((Validator) null);
        RestRouter.injectWith((InjectionProvider) null);

        client = WebClient.create(vertx);
    }

     @AfterEach
     public void after() throws InterruptedException {

    //     assertTrue(testContext.awaitCompletion(5, TimeUnit.SECONDS));
//         assertFalse(testContext.failed(), testContext.causeOfFailure().getMessage());
     }

}
