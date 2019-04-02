package com.zandero.rest;
/*

import com.zandero.rest.handler.UserHandler;
import com.zandero.rest.injection.GuiceInjectionProvider;
import com.zandero.rest.test.TestAuthorizationRest;
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
public class RouteAuthorizationInjectionTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before();

		// 2. REST with @RolesAllowed annotations
		Router router = new RestBuilder(vertx)
			                .injectWith(new GuiceInjectionProvider())
			                .provide(UserHandler.class)
			                .register(TestAuthorizationRest.class)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void testGetAll(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/private/all", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("all", body.toString());
				async.complete();
			});
		});
	}

	@Test
	public void testGetNobody(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/private/nobody", response -> {

			context.assertEquals(401, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void testGetUserNonAuthorized(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/private/user", response -> {

			context.assertEquals(401, response.statusCode());
			async.complete();
		});
	}

	@Test
	public void testGetUserAuthorized(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/private/user", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("user", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "user").end();
	}

	@Test
	public void testGetAdminAuthorized(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/private/admin", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("admin", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "admin").end();
	}

	@Test
	public void testGetAdminUnAuthorized(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/private/admin", response -> {

			context.assertEquals(401, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("HTTP 401 Unauthorized", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "user").end();
	}

	@Test
	public void testGetOtherUnAuthorized(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/private/other", response -> {

			context.assertEquals(401, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("HTTP 401 Unauthorized", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "user").end();
	}

	@Test
	public void testGetOtherOneAuthorized(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/private/other", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("{\"role\":\"one\"}", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "one").end();
	}

	@Test
	public void testGetOtherTwoAuthorized(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/private/other", response -> {

			context.assertEquals(200, response.statusCode());

			response.bodyHandler(body -> {
				context.assertEquals("{\"role\":\"two\"}", body.toString());
				async.complete();
			});
		}).putHeader("X-Token", "two").end();
	}

	@Test
	public void testPostUserAuthorized(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.post("/private/user", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("HELLO", body.toString());
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		}).putHeader("X-Token", "user")
		      .end("HELLO");
	}

	@Test
	public void testPostUserUnauthorized(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.post("/private/user", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("HTTP 401 Unauthorized", body.toString());
				context.assertEquals(401, response.statusCode());
				async.complete();
			});
		}).end("HELLO");
	}
}
*/
