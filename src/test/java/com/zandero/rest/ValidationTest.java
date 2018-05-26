package com.zandero.rest;

import com.zandero.rest.test.TestValidRest;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.test.json.ValidDummy;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.Assert.assertEquals;

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
	public void testHibernate() {

		Dummy dummy = new Dummy();
		Set<ConstraintViolation<Dummy>> result = validator.validate(dummy);
		assertEquals(0, result.size());

		ValidDummy validDummy = new ValidDummy();
		Set<ConstraintViolation<ValidDummy>> result2 = validator.validate(validDummy);
		assertEquals(2, result2.size());
	}

	@Test
	public void testDummyViolation(TestContext context) {

		// call and check response
		final Async async = context.async();

		String content = "{\"name\": \"test\"}";
		client.post("/check/dummy", response -> {

			response.bodyHandler(body -> {
				context.assertEquals("value: must not be null", body.toString());
				context.assertEquals("Validation failed", response.getHeader("X-Status-Reason"));
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		}).putHeader("content-type", "application/x-www-form-urlencoded")
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
				context.assertEquals("thisOne.arg0: must not be null", body.toString());
				context.assertEquals("Validation failed", response.getHeader("X-Status-Reason"));
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}
}
