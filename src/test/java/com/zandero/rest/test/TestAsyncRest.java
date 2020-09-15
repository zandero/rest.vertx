package com.zandero.rest.test;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.test.handler.*;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.*;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
@Path("async")
public class TestAsyncRest {

    private final AsyncService spaceService = new AsyncService();
    private final WorkerExecutor executor = Vertx.vertx().createSharedWorkerExecutor("shared");

    @GET
    @Path("call_future")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseWriter(DummyWriter.class)
    public Future<Dummy> createFuture(@Context Vertx vertx) {

        System.out.println(Thread.currentThread().getName() + " REST invoked");

        Future<Dummy> res = Future.future();
        spaceService.asyncCallFuture(vertx, res);
        System.out.println("Rest finished");
        return res;
    }

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
    @Path("executor_future")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseWriter(DummyWriter.class)
    public Future<Dummy> executorFuture(@Context Vertx vertx) {

        System.out.println(Thread.currentThread().getName() + " REST invoked");

        Future<Dummy> res = Future.future();
        spaceService.asyncExecutorFuture(executor, res);
        System.out.println("Rest finished");
        return res;
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
    public Future<Dummy> empty(@Context Vertx vertx) {

        System.out.println(Thread.currentThread().getName() + " REST invoked");

        Future<Dummy> res = Future.future();
        spaceService.asyncCallReturnNullFuture(vertx, res);
        System.out.println("Rest finished");
        return res;
    }

    @GET
    @Path("null")
    @Produces(MediaType.APPLICATION_JSON)
    public Future<Dummy> nullItIs(@Context Vertx vertx) {

        System.out.println(Thread.currentThread().getName() + " REST invoked");

        Future<Dummy> res = Future.future();
        spaceService.asyncCallReturnNullFuture(vertx, res);
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
