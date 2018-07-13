package com.zandero.rest.test;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.test.handler.AsyncService;
import com.zandero.rest.test.handler.DummyWriter;
import com.zandero.rest.test.handler.FutureDummyWriter;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;
import io.vertx.core.http.HttpServerResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletableFuture;

/**
 *
 */
@Path("async")
public class TestAsyncRest {

	private AsyncService spaceService = new AsyncService();
	private WorkerExecutor executor = Vertx.vertx().createSharedWorkerExecutor("shared");

	@GET
	@Path("call")
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseWriter(DummyWriter.class)
	public Future<Dummy> create(@Context Vertx vertx) throws InterruptedException {

		System.out.println(Thread.currentThread().getName() + " REST invoked");

		Future<Dummy> res = Future.future();
		spaceService.asyncCall(vertx, res);
		System.out.println("Rest finished");
		return res;
	}

	@GET
	@Path("completable")
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseWriter(FutureDummyWriter.class)
	public CompletableFuture<Dummy> complete(@Context Vertx vertx, @Context HttpServerResponse response) throws InterruptedException {

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
	@Path("executor")
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseWriter(DummyWriter.class)
	public Future<Dummy> executor(@Context Vertx vertx) throws InterruptedException {

		System.out.println(Thread.currentThread().getName() + " REST invoked");

		Future<Dummy> res = Future.future();
		spaceService.asyncExecutor(executor, res);
		System.out.println("Rest finished");
		return res;
	}

	@GET
	@Path("empty")
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseWriter(DummyWriter.class)
	public Future<Dummy> empty(@Context Vertx vertx) throws InterruptedException {

		System.out.println(Thread.currentThread().getName() + " REST invoked");

		Future<Dummy> res = Future.future();
		spaceService.asyncCallReturnNUll(vertx, res);
		System.out.println("Rest finished");
		return res;
	}

	@GET
	@Path("null")
	@Produces(MediaType.APPLICATION_JSON)
	public Future<Dummy> nullItIs(@Context Vertx vertx) throws InterruptedException {

		System.out.println(Thread.currentThread().getName() + " REST invoked");

		Future<Dummy> res = Future.future();
		spaceService.asyncCallReturnNUll(vertx, res);
		System.out.println("Rest finished");
		return res;
	}

	/*@GET
	@Path("handler")
	@Produces(MediaType.APPLICATION_JSON)
	public Handler<Dummy> nullHandler(@Context Vertx vertx) throws InterruptedException {

		System.out.println(Thread.currentThread().getName() + " REST invoked");

		Handler<Dummy> output = res -> {
			res.value = res.value + "++";
			res.name = res.name + "++";
		};

		spaceService.asyncHandler(vertx, output);
		System.out.println("Rest finished");
		return output;
	}*/
}
