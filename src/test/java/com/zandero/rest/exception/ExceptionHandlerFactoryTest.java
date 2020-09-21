package com.zandero.rest.exception;

import com.zandero.rest.cache.ExceptionHandlerCache;
import com.zandero.rest.test.exceptions.*;
import com.zandero.rest.test.handler.*;
import org.junit.jupiter.api.*;

import javax.ws.rs.WebApplicationException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class ExceptionHandlerFactoryTest {

    static private ExceptionHandlerCache factory;

    @BeforeEach
    void setUp() {
        factory = new ExceptionHandlerCache();
    }

    @Test
    void findMatchingHandler() throws Exception {

        factory.register(BaseExceptionHandler.class);

        // find it
        ExceptionHandler found = factory.getExceptionHandler(BaseException.class, null, null, null);
        assertNotNull(found);
        assertTrue(found instanceof BaseExceptionHandler);
    }

    @Test
    void findMatchingInheritedHandler() throws Exception {

        factory.register(InheritedBaseExceptionHandler.class, BaseExceptionHandler.class);

        // find it
        ExceptionHandler found = factory.getExceptionHandler(BaseException.class, null, null, null);
        assertNotNull(found);
        assertTrue(found instanceof BaseExceptionHandler);

        found = factory.getExceptionHandler(InheritedBaseException.class, null, null, null);
        assertNotNull(found);
        assertTrue(found instanceof InheritedBaseExceptionHandler);
    }

    @Test
    void findMatchingInheritedInheritedHandler() throws Exception {

        factory.register(InheritedFromInheritedExceptionHandler.class, InheritedBaseExceptionHandler.class, BaseExceptionHandler.class);

        // find it
        ExceptionHandler found = factory.getExceptionHandler(BaseException.class, null, null, null);
        assertTrue(found instanceof BaseExceptionHandler);

        found = factory.getExceptionHandler(InheritedBaseException.class, null, null, null);
        assertTrue(found instanceof InheritedBaseExceptionHandler);

        found = factory.getExceptionHandler(InheritedFromInheritedException.class, null, null, null);
        assertTrue(found instanceof InheritedFromInheritedExceptionHandler);

        found = factory.getExceptionHandler(MyExceptionClass.class, null, null, null);
        assertTrue(found instanceof GenericExceptionHandler);
    }

    // Exception handler instance tests
    @Test
    void findMatchingInstanceHandler() throws Exception {

        factory.register(new BaseExceptionHandler());

        // find it
        ExceptionHandler found = factory.getExceptionHandler(BaseException.class, null, null, null);
        assertTrue(found instanceof BaseExceptionHandler);
    }

    @Test
    void findMatchingInheritedInstanceHandler() throws Exception {

        factory.register(new InheritedBaseExceptionHandler(), new BaseExceptionHandler());

        // find it
        ExceptionHandler found = factory.getExceptionHandler(BaseException.class, null, null, null);
        assertTrue(found instanceof BaseExceptionHandler);

        found = factory.getExceptionHandler(InheritedBaseException.class, null, null, null);
        assertTrue(found instanceof InheritedBaseExceptionHandler);
    }

    @Test
    void findMatchingInheritedInheritedInstanceHandler() throws Exception {

        factory.register(new InheritedFromInheritedExceptionHandler(),
                         new InheritedBaseExceptionHandler(),
                         new BaseExceptionHandler());

        // find it
        ExceptionHandler found = factory.getExceptionHandler(BaseException.class, null, null, null);
        assertTrue(found instanceof BaseExceptionHandler);

        found = factory.getExceptionHandler(InheritedBaseException.class, null, null, null);
        assertTrue(found instanceof InheritedBaseExceptionHandler);

        found = factory.getExceptionHandler(InheritedFromInheritedException.class, null, null, null);
        assertTrue(found instanceof InheritedFromInheritedExceptionHandler);

        found = factory.getExceptionHandler(MyExceptionClass.class, null, null, null);
        assertTrue(found instanceof GenericExceptionHandler);
    }

    @Test
    void findMatchingHandlerWithDefinition() throws Exception {

        factory.register(new InheritedFromInheritedExceptionHandler(),
                         new InheritedBaseExceptionHandler(),
                         new BaseExceptionHandler());

        // definition should take over
        Class<? extends ExceptionHandler>[] defined = new Class[1];
        defined[0] = JsonExceptionHandler.class;

        ExceptionHandler found = factory.getExceptionHandler(BaseException.class, defined, null, null);
        assertTrue(found instanceof JsonExceptionHandler);

        // 2.
        defined[0] = MyExceptionHandler.class;
        found = factory.getExceptionHandler(BaseException.class, defined, null, null);
        assertTrue(found instanceof BaseExceptionHandler);

        //
        found = factory.getExceptionHandler(MyExceptionClass.class, defined, null, null);
        assertTrue(found instanceof MyExceptionHandler);
    }

    @Test
    void getDefaultGenericExceptionHandler() throws Exception {

        ExceptionHandler found = factory.getExceptionHandler(BaseException.class, null, null, null);
        assertTrue(found instanceof GenericExceptionHandler);

        found = factory.getExceptionHandler(WebApplicationException.class, null, null, null);
        assertTrue(found instanceof WebApplicationExceptionHandler);
    }

    @Test
    void doubleHandlerRegistration() {

        factory.register(MyExceptionHandler.class);

        Exception e = assertThrows(IllegalArgumentException.class, () -> factory.register(new MyExceptionHandler()));
        assertEquals("Exception handler for: com.zandero.rest.test.handler.MyExceptionClass " +
                         "already registered with: com.zandero.rest.test.handler.MyExceptionHandler", e.getMessage());
    }

    @Test
    void doubleHandlerRegistrationReversed() {

        factory.register(new MyExceptionHandler());
        Exception e = assertThrows(IllegalArgumentException.class, () -> factory.register(MyExceptionHandler.class));
        assertEquals("Exception handler for: com.zandero.rest.test.handler.MyExceptionClass " +
                         "already registered with: com.zandero.rest.test.handler.MyExceptionHandler", e.getMessage());
    }

    @Test
    void doubleHandlerRegistrationSameException() {

        factory.register(new IllegalArgumentExceptionHandler());
        Exception e = assertThrows(IllegalArgumentException.class, () -> factory.register(ContextExceptionHandler.class));
        assertEquals("Exception handler for: java.lang.IllegalArgumentException " +
                         "already registered with: com.zandero.rest.test.handler.IllegalArgumentExceptionHandler", e.getMessage());
    }

    @Test
    void doubleHandlerRegistrationSameExceptionReversed() {

        factory.register(ContextExceptionHandler.class);
        Exception e = assertThrows(IllegalArgumentException.class, () -> factory.register(new IllegalArgumentExceptionHandler()));
        assertEquals("Exception handler for: java.lang.IllegalArgumentException " +
                         "already registered with: com.zandero.rest.test.handler.ContextExceptionHandler", e.getMessage());
    }
}