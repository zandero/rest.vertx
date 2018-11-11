package com.zandero.rest;

import com.zandero.rest.test.TestAuthorizationRest;
import com.zandero.rest.test.data.SimulatedUser;
import io.vertx.core.Handler;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RouteAuthorizationTest extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before(context);


		// 1. register handler to initialize User
		Router router = Router.router(vertx);
		router.route().handler(getUserHandler());

		// 2. REST with @RolesAllowed annotations
		TestAuthorizationRest testRest = new TestAuthorizationRest();
		RestRouter.register(router, testRest);

		vertx.createHttpServer()
			.requestHandler(router::accept)
			.listen(PORT);
	}

	public Handler<RoutingContext> getUserHandler() {

		return context -> {

			// read header ... if present ... create user with given value
			String token = context.request().getHeader("X-Token");

			// set user ...
			if (token != null) {
				context.setUser(new SimulatedUser(token));
			}

			context.next();
		};
	}

	@Test
	public void testGetAll(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/private/all", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("all", body.toString());
				context.assertEquals(200, response.statusCode());
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

			response.bodyHandler(body -> {
				context.assertEquals("user", body.toString());
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		}).putHeader("X-Token", "user").end();
	}

	@Test
	public void testPostUserAuthorized(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.post("/private/user", response -> {

			response.bodyHandler(body -> {
				context.assertEquals(200, response.statusCode());
				context.assertEquals("HELLO", body.toString());
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

	@Test
	public void testGetAdminAuthorized(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.get("/private/admin", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("admin", body.toString());
				context.assertEquals(200, response.statusCode());
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
}
