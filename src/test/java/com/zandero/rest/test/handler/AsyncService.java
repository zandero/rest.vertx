package com.zandero.rest.test.handler;

import com.zandero.rest.test.json.Dummy;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 *
 */
public class AsyncService {

	public void handle(Dummy dummy, Handler<AsyncResult<Dummy>> handler) throws InterruptedException {

		Thread.sleep(1000L);
		handler.handle(Future.succeededFuture(dummy));
		System.out.println(Thread.currentThread().getName() + " async service called ");
	}
}
