package com.zandero.rest;

import com.zandero.rest.reader.DummyBodyReader;
import com.zandero.rest.test.TestRegExRest;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.data.TokenProvider;
import com.zandero.rest.test.handler.IllegalArgumentExceptionHandler;
import com.zandero.rest.test.handler.MyExceptionHandler;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.TestCustomWriter;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;

/**
 *
 */
@RunWith(VertxUnitRunner.class)
public class RestBuilderTest extends VertxTest {

	@Test
	public void buildInterfaceTest(TestContext context) {

		Router router = new RestBuilder(vertx)
		                .register(TestRest.class, TestRegExRest.class)
		                .reader(Dummy.class, DummyBodyReader.class)
		                .writer(MediaType.APPLICATION_JSON, TestCustomWriter.class)
		                .errorHandler(IllegalArgumentExceptionHandler.class)
		                .errorHandler(MyExceptionHandler.class)
		                .provide(request -> new Dummy("test", "name"))
		                .addProvider(TokenProvider.class)
		                .build();
	}

}