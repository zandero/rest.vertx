package com.zandero.rest.test;

import com.zandero.rest.test.handler.AsyncService;
import com.zandero.rest.test.json.Dummy;
import io.vertx.core.Handler;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
	//@ResponseWriter(AsyncWriter.class)
	public Handler<Dummy> create() throws InterruptedException {

		System.out.println(Thread.currentThread().getName() + "create start");

		Handler<Dummy> output = res -> {
			res.value = res.value + "++";
			res.name = res.name + "++";
			System.out.println(Thread.currentThread().getName() + " handler invoked");
		};

		Dummy dummy = new Dummy("async", "called");
		spaceService.handle(dummy, handlerResult -> {
			System.out.println(Thread.currentThread().getName() + " complete");
			output.handle(handlerResult.result());
		});

		return output;
	}
}
