package com.zandero.rest.test;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.test.handler.AsyncService;
import com.zandero.rest.test.handler.DummyWriter;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 *
 */
@Path("async")
public class TestAsyncRest {

	private AsyncService spaceService = new AsyncService();

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
