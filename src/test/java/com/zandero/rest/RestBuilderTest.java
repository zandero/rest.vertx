package com.zandero.rest;

import com.zandero.rest.reader.DummyBodyReader;
import com.zandero.rest.test.TestRegExRest;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.data.TokenProvider;
import com.zandero.rest.test.handler.ContextExceptionHandler;
import com.zandero.rest.test.handler.IllegalArgumentExceptionHandler;
import com.zandero.rest.test.handler.MyExceptionHandler;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.TestCustomWriter;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RestBuilderTest extends VertxTest {

	@Before
	public void start(TestContext context) {
		super.before();
	}

	@Test
	public void buildInterfaceTest() {

		new RestBuilder(vertx)
			.register(TestRest.class, TestRegExRest.class)
			.reader(Dummy.class, DummyBodyReader.class)
			.writer(MediaType.APPLICATION_JSON, TestCustomWriter.class)
			.errorHandler(IllegalArgumentExceptionHandler.class)
			.errorHandler(MyExceptionHandler.class)
			.provide(request -> new Dummy("test", "name"))
			.addProvider(TokenProvider.class)
			.build();
	}

	@Test
	public void missingApiTest() {

		try {
			new RestBuilder(vertx)
				.build();
			fail();
		}
		catch (IllegalArgumentException e) {
			assertEquals("No REST API given, register at least one! Use: .register(api) call!", e.getMessage());
		}
	}

	@Test
	public void cantRegisterExceptionHandlerTest() {

		try {
			new RestBuilder(vertx)
				.register(TestRest.class)
				.errorHandler(new ContextExceptionHandler())
				.build();
			fail();
		}
		catch (IllegalArgumentException e) {
			assertEquals("Exception handler utilizing @Context must be registered as class type not as instance!", e.getMessage());
		}
	}
}