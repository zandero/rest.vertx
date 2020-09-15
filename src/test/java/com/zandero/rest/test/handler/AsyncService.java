package com.zandero.rest.test.handler;

import com.zandero.rest.test.json.Dummy;
import io.vertx.core.*;

/**
 *
 */
public class AsyncService {

    public void asyncExecutorFuture(WorkerExecutor executor, Future<Dummy> value) {

        executor.executeBlocking(fut -> {
                                     System.out.println("Process started!");
                                     try {
                                         Thread.sleep(1000);
                                     } catch (InterruptedException e) {
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

    public void asyncExecutorPromise(WorkerExecutor executor, Promise<Dummy> value) {

        executor.executeBlocking(fut -> {
                                     System.out.println("Process started!");
                                     try {
                                         Thread.sleep(1000);
                                     } catch (InterruptedException e) {
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

    public void asyncCallFuture(Vertx vertx, Future<Dummy> value) {

        vertx.executeBlocking(
            fut -> {
                System.out.println("Process started!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
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

    public void asyncCallPromise(Vertx vertx, Promise<Dummy> value) {

        vertx.executeBlocking(
            fut -> {
                System.out.println("Process started!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
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

    public void asyncCallReturnNullFuture(Vertx vertx, Future<Dummy> value) {

        vertx.executeBlocking(
            fut -> {
                System.out.println("Process started!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
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

    public void asyncCallReturnNullPromise(Vertx vertx, Promise<Dummy> value) {

        vertx.executeBlocking(
            fut -> {
                System.out.println("Process started!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
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

    public void asyncHandler(Handler<Dummy> handler) {

        (new Thread(() -> {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.handle(new Dummy("call", "finished"));
        })).start();
        System.out.println("I'm finished!");
    }
}
