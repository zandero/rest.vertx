package com.zandero.rest;
/*

import com.zandero.rest.test.TestMatrixParamRest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

*/
/**
 *
 *//*

@RunWith(VertxUnitRunner.class)
public class RouteWithMatrixTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before();

		Router router = RestRouter.register(vertx, TestMatrixParamRest.class);
		vertx.createHttpServer()
		.requestHandler(router::accept)
		.listen(PORT);
	}

	@Test
	public void matrixExtractTest(TestContext context) {

		final Async async = context.async();

		client.getNow("/matrix/extract/result;one=1;two=2", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("result=3", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void matrixRegExTest(TestContext context) {

		final Async async = context.async();

		client.getNow("/matrix/direct/param;one=1;two=2", response -> {

			//context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("3", body.toString());
				async.complete();
			});
		});
	}
}*/
