package com.zandero.rest;

import com.zandero.rest.test.*;
import io.vertx.core.buffer.*;
import io.vertx.ext.web.*;
import io.vertx.ext.web.codec.*;
import io.vertx.junit5.*;
import jakarta.validation.*;
import org.hibernate.validator.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(VertxExtension.class)
class ValidationTest extends VertxTest {

    @BeforeAll
    static void start() {

        before();

        HibernateValidatorConfiguration configuration = Validation.byProvider(HibernateValidator.class)
                                                            .configure();

        try (ValidatorFactory factory = configuration.buildValidatorFactory()) {
            Validator validator = factory.getValidator();

            // Use the validator (Example usage)
            System.out.println("Validator is ready: " + (validator != null));

            Router router = new RestBuilder(vertx)
                                .register(TestValidRest.class)
                                .validateWith(validator)
                                .build();

            vertx.createHttpServer()
                .requestHandler(router)
                .listen(PORT, vertxTestContext.succeedingThenComplete());
        }
    }

    @Test
    void testDummyViolation(VertxTestContext context) {

        String content = "{\"name\": \"test\", \"size\": 12}";

        client.post(PORT, HOST, "/check/dummy")
            .putHeader("content-type", "application/json")
            .sendBuffer(Buffer.buffer(content), context.succeeding(response -> context.verify(() -> {
                assertEquals("body ValidDummy.value: must not be null", response.bodyAsString());
                assertEquals("Validation failed", response.getHeader("X-Status-Reason"));
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testDummyViolationSize(VertxTestContext context) {

        String content = "{\"name\": \"test\", \"value\": \"test\", \"size\": 30}";
        client.post(PORT, HOST, "/check/dummy").as(BodyCodec.string())
            .putHeader("content-type", "application/json")
            .sendBuffer(Buffer.buffer(content), context.succeeding(response -> context.verify(() -> {
                assertEquals("body ValidDummy.size: must be less than or equal to 20", response.body());
                assertEquals("Validation failed", response.getHeader("X-Status-Reason"));
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testThatOne(VertxTestContext context) {

        client.get(PORT, HOST, "/check/that")
            .send(context.succeeding(response -> context.verify(() -> {
                assertNull(response.bodyAsString());
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testThisOne(VertxTestContext context) {

        client.get(PORT, HOST, "/check/this").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals("@QueryParam(\"one\"): must not be null", response.body());
                assertEquals("Validation failed", response.getHeader("X-Status-Reason"));
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testTheOther(VertxTestContext context) {

        client.get(PORT, HOST, "/check/other?one=0&two=0&three=20").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                // : must be greater than or equal to 1, : must be greater than or equal to 1, : must be less than or equal to 10
                String content = response.body();
                assertTrue(content.contains("@QueryParam(\"one\"): must be greater than or equal to 1"));
                assertTrue(content.contains("@QueryParam(\"two\"): must be greater than or equal to 1"));
                assertTrue(content.contains("@QueryParam(\"three\"): must be less than or equal to 10"));

                assertEquals("Validation failed", response.getHeader("X-Status-Reason"));
                assertEquals(400, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testResult(VertxTestContext context) {

        client.get(PORT, HOST, "/check/result?one=1").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testResultInvalid(VertxTestContext context) {

        client.get(PORT, HOST, "/check/result?one=11").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                String content = response.body();
                assertEquals(400, response.statusCode());
                assertTrue(content.contains("must be less than or equal to 10"));
                assertEquals("Validation failed", response.getHeader("X-Status-Reason"));

                context.completeNow();
            })));
    }

    @Test
    void testResultInvalidNull(VertxTestContext context) {

        client.get(PORT, HOST, "/check/result?one=A").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                String content = response.body();
                assertTrue(content.contains("must not be null"));
                assertEquals("Validation failed", response.getHeader("X-Status-Reason"));

                context.completeNow();
            })));
    }

    @Test
    void testEmptyMethod(VertxTestContext context) {

        client.get(PORT, HOST, "/check/empty").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(200, response.statusCode());
                context.completeNow();
            })));
    }

    @Test
    void testHeaderViaCustomValidatorFail(VertxTestContext context) {
        client.get(PORT, HOST, "/check/header").as(BodyCodec.string())
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                assertEquals("Bad Request", response.statusMessage());
                context.completeNow();
            })));
    }

    @Test
    void testHeaderViaCustomValidatorFailWrongHeader(VertxTestContext context) {
        client.get(PORT, HOST, "/check/header").as(BodyCodec.string())
            .putHeader("check-me", "IAmOK")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                assertEquals("Bad Request", response.statusMessage());
                context.completeNow();
            })));
    }

    @Test
    void testHeaderViaCustomValidatorOK(VertxTestContext context) {
        client.get(PORT, HOST, "/check/header").as(BodyCodec.string())
            .putHeader("check-me", "IAmOK")
            .send(context.succeeding(response -> context.verify(() -> {
                assertEquals(400, response.statusCode());
                assertEquals("Bad Request", response.statusMessage());
                context.completeNow();
            })));
    }
}
