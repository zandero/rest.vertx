package com.zandero.rest;
/*

import com.zandero.rest.reader.DummyBodyReader;
import com.zandero.rest.test.TestRegExRest;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.data.TokenProvider;
import com.zandero.rest.test.handler.ContextExceptionHandler;
import com.zandero.rest.test.handler.IllegalArgumentExceptionHandler;
import com.zandero.rest.test.handler.MyExceptionHandler;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.TestCustomWriter;
import io.vertx.ext.unit.VertxTestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

*/

import com.zandero.rest.reader.DummyBodyReader;
import com.zandero.rest.test.TestRegExRest;
import com.zandero.rest.test.TestRest;
import com.zandero.rest.test.data.TokenProvider;
import com.zandero.rest.test.handler.ContextExceptionHandler;
import com.zandero.rest.test.handler.IllegalArgumentExceptionHandler;
import com.zandero.rest.test.handler.MyExceptionHandler;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.writer.TestCustomWriter;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.core.MediaType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 */

@ExtendWith(VertxExtension.class)
public class RestBuilderTest extends VertxTest {

    @BeforeAll
    static void start() {
        before();
    }

    @Test
    void buildInterfaceTest() {

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
    void missingApiTest() {

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new RestBuilder(vertx).build());
        assertEquals("No REST API given, register at least one! Use: .register(api) call!", e.getMessage());
    }

    @Test
    void cantRegisterExceptionHandlerTest() {

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new RestBuilder(vertx)
                .register(TestRest.class)
                .errorHandler(new ContextExceptionHandler())
                .build());
        assertEquals("Exception handler utilizing @Context must be registered as class type not as instance!", e.getMessage());
    }
}
