package com.zandero.rest;

import com.zandero.rest.test.TestValidRest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class ValidationTest extends VertxTest {

	HibernateValidatorConfiguration configuration = Validation.byProvider(HibernateValidator.class)
	                                                          .configure();

	Validator validator = configuration.buildValidatorFactory()
	                                   .getValidator();

	@Before
	public void start(TestContext context) {

		super.before(context);

		Router router = new RestBuilder(vertx)
			                .register(TestValidRest.class)
			                .validateWith(validator)
			                .build();

		vertx.createHttpServer()
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	@Test
	public void testDummyViolation(TestContext context) {

		// call and check response
		final Async async = context.async();

		String content = "{\"name\": \"test\", \"size\": 12}";
		client.post("/check/dummy", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("body ValidDummy.value: must not be null", body.toString());
				context.assertEquals("Validation failed", response.getHeader("X-Status-Reason"));
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		}).putHeader("content-type", "application/json")
		      .end(content);
	}

	@Test
	public void testDummyViolationSize(TestContext context) {

		// call and check response
		final Async async = context.async();

		String content = "{\"name\": \"test\", \"value\": \"test\", \"size\": 30}";
		client.post("/check/dummy", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("body ValidDummy.size: must be less than or equal to 20", body.toString());
				context.assertEquals("Validation failed", response.getHeader("X-Status-Reason"));
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		}).putHeader("content-type", "application/json")
		      .end(content);
	}

	@Test
	public void testThatOne(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/check/that", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("", body.toString());
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void testThisOne(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/check/this", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("@QueryParam(\"one\"): must not be null", body.toString());
				context.assertEquals("Validation failed", response.getHeader("X-Status-Reason"));
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void testTheOther(TestContext context) {

		// call and check response
		final Async async = context.async();

		client.getNow("/check/other?one=0&two=0&three=20", response -> {

			response.bodyHandler(body -> {

				String content = body.toString();
				context.assertTrue(content.contains("@QueryParam(\"one\"): must be greater than or equal to 1"));
				context.assertTrue(content.contains("@QueryParam(\"two\"): must be greater than or equal to 1"));
				context.assertTrue(content.contains("@QueryParam(\"three\"): must be less than or equal to 10"));

				context.assertEquals("Validation failed", response.getHeader("X-Status-Reason"));
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}
}
