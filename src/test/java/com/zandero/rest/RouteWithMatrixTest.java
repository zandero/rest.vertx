package com.zandero.rest;
/*

import com.zandero.rest.test.TestMatrixParamRest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.VertxTestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

*/
/**
 *
 *//*

@ExtendWith(VertxExtension.class)
public class RouteWithMatrixTest extends VertxTest {

	@BeforeAll
	static void start() {

		super.before();

		Router router = RestRouter.register(vertx, TestMatrixParamRest.class);
		vertx.createHttpServer()
		.requestHandler(router)
		.listen(PORT);
	}

	@Test
	public void matrixExtractTest(VertxTestContext context) {

		final Async async = context.async();

		client.get(PORT, HOST, "/matrix/extract/result;one=1;two=2").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("result=3", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void matrixRegExTest(VertxTestContext context) {

		final Async async = context.async();

		client.get(PORT, HOST, "/matrix/direct/param;one=1;two=2").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			//context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("3", body.toString());
				async.complete();
			});
		});
	}
}*/
