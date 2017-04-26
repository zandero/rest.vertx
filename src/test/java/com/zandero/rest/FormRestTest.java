package com.zandero.rest;

import com.zandero.rest.test.TestFormRest;
import com.zandero.rest.test.VertxTest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class FormRestTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);

		TestFormRest testRest = new TestFormRest();

		Router router = RestRouter.register(vertx, testRest);

		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(PORT);
	}

	@Test
	public void loginFormTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		String content = "username=value&password=another";

		client.post("/form/login", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("value:another", body.toString());
				async.complete();
			});
		}).putHeader("content-type", "application/x-www-form-urlencoded")
			.end(content);
	}

	@Test
	public void multipartFormTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		String content = "username=value&password=another";

		client.post("/form/login", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("value:another", body.toString());
				async.complete();
			});
		}).putHeader("content-type", "multipart/form-data")
			.end(content);
	}

	@Test
	public void sendCookieTest(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.post("/form/cookie", response -> {

			context.assertEquals(200, response.statusCode());

			response.handler(body -> {
				context.assertEquals("blabla", body.toString());
				async.complete();
			});
		}).putHeader("Cookie", "username=blabla").end();
	}
}
