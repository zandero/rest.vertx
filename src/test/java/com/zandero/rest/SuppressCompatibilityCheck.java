package com.zandero.rest;

import com.zandero.rest.test.TestEchoRest;
import com.zandero.rest.writer.TestSuppressedWriter;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.fail;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class SuppressCompatibilityCheck extends VertxTest {

	@Before
	public void start(TestContext context) {

		super.before();

		Router router = new RestBuilder(vertx)
			                .writer(String.class, TestSuppressedWriter.class)
			                .register(TestEchoRest.class) // returns String
			                .build();

		HttpServerOptions serverOptions = new HttpServerOptions();
		serverOptions.setCompressionSupported(true);

		vertx.createHttpServer(serverOptions)
		     .requestHandler(router::accept)
		     .listen(PORT);
	}

	// incompatible response / writer combination
	@Test
	public void testSuppressedCheck(TestContext context) {
		try {
			new RestBuilder(vertx)
				.writer(String.class, TestSuppressedWriter.class)
				.register(TestEchoRest.class) // returns String
				.build();
		}
		catch (Exception e) {
			// should not fail
			fail(e.getMessage());
		}
	}
}
