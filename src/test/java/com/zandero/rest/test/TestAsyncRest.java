package com.zandero.rest.test;

import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.test.handler.AsyncService;
import com.zandero.rest.test.handler.AsyncWriter;
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
	@ResponseWriter(AsyncWriter.class)
	public Dummy create() throws InterruptedException {

		System.out.println(Thread.currentThread().getName() + "create start");

		Handler<Dummy> output = res -> {
			res.value = res.value + "++";
			res.name = res.name + "++";
		};

		Dummy dummy = new Dummy("async", "called");
		spaceService.handle(dummy, handlerResult -> {
			System.out.println(Thread.currentThread().getName() + "complete");
			output.handle(handlerResult.result());
		});

		return dummy;
	}
}
