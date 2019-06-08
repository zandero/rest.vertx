package com.zandero.rest;

import com.zandero.rest.test.TestValidRest;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@ExtendWith(VertxExtension.class)
@ExtendWith(VertxExtension.class)
public class ValidationTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        HibernateValidatorConfiguration configuration = Validation.byProvider(HibernateValidator.class)
                .configure();

        Validator validator = configuration.buildValidatorFactory()
                .getValidator();


        Router router = new RestBuilder(vertx)
                .register(TestValidRest.class)
                .validateWith(validator)
                .build();

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(PORT, VertxTestContext.completing());
    }

    @Test
    public void testDummyViolation() {

        // call and check response
        //final Async async = context.async();

        String content = "{\"name\": \"test\", \"size\": 12}";


        WebClient client = WebClient.create(vertx);

        client.post(PORT, HOST, "/check/dummy")
                .putHeader("content-type", "application/json")
                .sendBuffer(Buffer.buffer(content), VertxTestContext.succeeding(response -> VertxTestContext.verify(() -> {
                    assertEquals(response.body().toString(), "Plop");
                    VertxTestContext.completeNow();
                })));


		/*client.post("/check/dummy").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("body ValidDummy.value: must not be null", body.toString());
				context.assertEquals("Validation failed", response.getHeader("X-Status-Reason"));
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		}).putHeader("content-type", "application/json")
		      .end(content);*/
    }

/*	@Test
	public void testDummyViolationSize(VertxTestContext context) {



		String content = "{\"name\": \"test\", \"value\": \"test\", \"size\": 30}";
		client.post("/check/dummy").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

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
	public void testThatOne(VertxTestContext context) {



		client.get(PORT, HOST, "/check/that").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("", body.toString());
				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void testThisOne(VertxTestContext context) {



		client.get(PORT, HOST, "/check/this").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {
				context.assertEquals("@QueryParam(\"one\"): must not be null", body.toString());
				context.assertEquals("Validation failed", response.getHeader("X-Status-Reason"));
				context.assertEquals(400, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void testTheOther(VertxTestContext context) {



		client.get(PORT, HOST, "/check/other?one=0&two=0&three=20").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

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

	@Test
	public void testResult(VertxTestContext context) {



		client.get(PORT, HOST, "/check/result?one=1").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {

				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		});
	}

	@Test
	public void testResultInvalid(VertxTestContext context) {



		client.get(PORT, HOST, "/check/result?one=11").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {

				context.assertEquals(400, response.statusCode());

				String content = body.toString();
				context.assertTrue(content.contains("must be less than or equal to 10"));
				context.assertEquals("Validation failed", response.getHeader("X-Status-Reason"));

				async.complete();
			});
		});
	}

	@Test
	public void testResultInvalidNull(VertxTestContext context) {



		client.get(PORT, HOST, "/check/result?one=A").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {

				context.assertEquals(400, response.statusCode());

				String content = body.toString();
				context.assertTrue(content.contains("must not be null"));
				context.assertEquals("Validation failed", response.getHeader("X-Status-Reason"));

				async.complete();
			});
		});
	}

	@Test
	public void testEmptyMethod(VertxTestContext context) {



		client.get(PORT, HOST, "/check/empty").as(BodyCodec.string())
                .send(context.succeeding(response -> context.verify(() ->

			response.bodyHandler(body -> {

				context.assertEquals(200, response.statusCode());
				async.complete();
			});
		});
	}*/
}
