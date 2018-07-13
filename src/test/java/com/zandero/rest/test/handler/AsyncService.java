package com.zandero.rest.test.handler;

import com.zandero.rest.test.json.Dummy;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;

/**
 *
 */
public class AsyncService {

	public void asyncExecutor(WorkerExecutor executor, Future<Dummy> value) {

		executor.executeBlocking(fut -> {
			                         System.out.println("Process started!");
			                         try {
				                         Thread.sleep(1000);
			                         }
			                         catch (InterruptedException e) {
				                         value.fail("Fail");
			                         }
			                         value.complete(new Dummy("async", "called"));
			                         fut.complete();
		                         },
		                         false,
		                         fut -> {
			                         System.out.println("Process finished!");
		                         });
	}

	public void asyncCall(Vertx vertx, Future<Dummy> value) throws InterruptedException {

		vertx.executeBlocking(
			fut -> {
				System.out.println("Process started!");
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					value.fail("Fail");
				}
				value.complete(new Dummy("async", "called"));
				System.out.println("Process finished!");
				fut.complete();
			},
			false,
			fut -> {
			}
		);

		System.out.println("I'm finished!");
	}

	public void asyncCallReturnNUll(Vertx vertx, Future<Dummy> value) throws InterruptedException {

		vertx.executeBlocking(
			fut -> {
				System.out.println("Process started!");
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					value.fail("Fail");
				}
				value.complete(null);
				System.out.println("Process finished!");
				fut.complete();
			},
			false,
			fut -> {
			}
		);

		System.out.println("I'm finished!");
	}

	public void asyncHandler(Vertx vertx, Handler<Dummy> handler) throws InterruptedException {

		(new Thread(() -> {
			try {
				Thread.sleep(1000L);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			handler.handle(new Dummy("call", "finished"));
		})).start();
		System.out.println("I'm finished!");
	}
}
