package com.zandero.resttest.test;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.resttest.test.handler.AsyncService;
import com.zandero.resttest.test.handler.DummyWriter;
import com.zandero.resttest.test.handler.FutureDummyWriter;
import com.zandero.resttest.test.json.Dummy;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerResponse;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.*;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
@Path("async")
public class TestAsyncRest {

    private final AsyncService spaceService = new AsyncService();
    private final WorkerExecutor executor = Vertx.vertx().createSharedWorkerExecutor("shared");

    @GET
    @Path("call_promise")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseWriter(DummyWriter.class)
    public Promise<Dummy> createPromise(@Context Vertx vertx) {

        System.out.println(Thread.currentThread().getName() + " REST invoked");

        Promise<Dummy> res = Promise.promise();
        spaceService.asyncCallPromise(vertx, res);
        System.out.println("Rest finished");
        return res;
    }

    @GET
    @Path("completable")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseWriter(FutureDummyWriter.class)
    public CompletableFuture<Dummy> complete(@Context Vertx vertx, @Context HttpServerResponse response)
        throws InterruptedException {

        System.out.println(Thread.currentThread().getName() + " REST invoked");

        CompletableFuture<Dummy> res = getFuture();
        System.out.println("Rest finished");
        return res;
    }

    private CompletableFuture<Dummy> getFuture() throws InterruptedException {

        CompletableFuture<Dummy> future = new CompletableFuture<>();
        future.complete(getDummy());
        return future;
    }

    private Dummy getDummy() throws InterruptedException {
        Thread.sleep(1000);
        return new Dummy("hello", "world");
    }

    @GET
    @Path("executor_promise")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseWriter(DummyWriter.class)
    public Promise<Dummy> executorPromise(@Context Vertx vertx) {

        System.out.println(Thread.currentThread().getName() + " REST invoked");

        Promise<Dummy> res = Promise.promise();
        spaceService.asyncExecutorPromise(executor, res);
        System.out.println("Rest finished");
        return res;
    }

    @GET
    @Path("empty")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseWriter(DummyWriter.class)
    public Promise<Dummy> emptyPromise(@Context Vertx vertx) {

        System.out.println(Thread.currentThread().getName() + " REST invoked");

        Promise<Dummy> res = Promise.promise();
        spaceService.asyncCallReturnNullPromise(vertx, res);
        System.out.println("Rest finished");
        return res;
    }

    @GET
    @Path("null")
    @Produces(MediaType.APPLICATION_JSON)
    public Promise<Dummy> nullItIsPromise(@Context Vertx vertx) {

        System.out.println(Thread.currentThread().getName() + " REST invoked");

        Promise<Dummy> res = Promise.promise();
        spaceService.asyncCallReturnNullPromise(vertx, res);
        System.out.println("Rest finished");
        return res;
    }

    @GET
    @Path("handler")
    @Produces(MediaType.APPLICATION_JSON)
    public String nullHandler(@Context Vertx vertx) {

        System.out.println(Thread.currentThread().getName() + " REST invoked");

        Handler<Dummy> output = res -> {
            res.value = res.value + "++";
            res.name = res.name + "++";
        };

        spaceService.asyncHandler(output);
        return "invoked";
    }
}