package com.zandero.rest.exception;

import com.zandero.rest.provisioning.ClassForge;
import com.zandero.rest.test.exceptions.*;
import com.zandero.rest.test.handler.*;
import org.junit.jupiter.api.*;

import javax.ws.rs.WebApplicationException;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */
class ExceptionHandlerFactoryTest {

    static private ClassForge forge;

    @BeforeEach
    void setUp() {
        forge = new ClassForge();
    }

    @Test
    void findMatchingHandler() throws Exception {

        forge.getExceptionHandlers().register(BaseExceptionHandler.class);

        // find it
        ExceptionHandler found = forge.getExceptionHandler(BaseException.class, null, null);
        assertNotNull(found);
        assertTrue(found instanceof BaseExceptionHandler);
    }

    @Test
    void findMatchingInheritedHandler() throws Exception {

        forge.getExceptionHandlers().register(InheritedBaseExceptionHandler.class, BaseExceptionHandler.class);

        // find it
        ExceptionHandler found = forge.getExceptionHandler(BaseException.class, null, null);
        assertNotNull(found);
        assertTrue(found instanceof BaseExceptionHandler);

        found = forge.getExceptionHandler(InheritedBaseException.class, null, null);
        assertNotNull(found);
        assertTrue(found instanceof InheritedBaseExceptionHandler);
    }

    @Test
    void findMatchingInheritedInheritedHandler() throws Exception {

        forge.getExceptionHandlers().register(InheritedFromInheritedExceptionHandler.class, InheritedBaseExceptionHandler.class, BaseExceptionHandler.class);

        // find it
        ExceptionHandler found = forge.getExceptionHandler(BaseException.class, null, null);
        assertTrue(found instanceof BaseExceptionHandler);

        found = forge.getExceptionHandler(InheritedBaseException.class, null, null);
        assertTrue(found instanceof InheritedBaseExceptionHandler);

        found = forge.getExceptionHandler(InheritedFromInheritedException.class, null, null);
        assertTrue(found instanceof InheritedFromInheritedExceptionHandler);

        found = forge.getExceptionHandler(MyExceptionClass.class, null, null);
        assertTrue(found instanceof GenericExceptionHandler);
    }

    // Exception handler instance tests
    @Test
    void findMatchingInstanceHandler() throws Exception {

        forge.getExceptionHandlers().register(new BaseExceptionHandler());

        // find it
        ExceptionHandler found = forge.getExceptionHandler(BaseException.class, null, null);
        assertTrue(found instanceof BaseExceptionHandler);
    }

    @Test
    void findMatchingInheritedInstanceHandler() throws Exception {

        forge.getExceptionHandlers().register(new InheritedBaseExceptionHandler(), new BaseExceptionHandler());

        // find it
        ExceptionHandler found = forge.getExceptionHandler(BaseException.class, null, null);
        assertTrue(found instanceof BaseExceptionHandler);

        found = forge.getExceptionHandler(InheritedBaseException.class, null, null);
        assertTrue(found instanceof InheritedBaseExceptionHandler);
    }

    @Test
    void findMatchingInheritedInheritedInstanceHandler() throws Exception {

        forge.getExceptionHandlers().register(new InheritedFromInheritedExceptionHandler(),
                                              new InheritedBaseExceptionHandler(),
                                              new BaseExceptionHandler());

        // find it
        ExceptionHandler found = forge.getExceptionHandler(BaseException.class, null, null);
        assertTrue(found instanceof BaseExceptionHandler);

        found = forge.getExceptionHandler(InheritedBaseException.class, null, null);
        assertTrue(found instanceof InheritedBaseExceptionHandler);

        found = forge.getExceptionHandler(InheritedFromInheritedException.class, null, null);
        assertTrue(found instanceof InheritedFromInheritedExceptionHandler);

        found = forge.getExceptionHandler(MyExceptionClass.class, null, null);
        assertTrue(found instanceof GenericExceptionHandler);
    }

    @Test
    void findMatchingHandlerWithDefinition() throws Exception {

        forge.getExceptionHandlers().register(new InheritedFromInheritedExceptionHandler(),
                                              new InheritedBaseExceptionHandler(),
                                              new BaseExceptionHandler());

        // definition should take over
        Class<? extends ExceptionHandler>[] defined = new Class[1];
        defined[0] = JsonExceptionHandler.class;

        ExceptionHandler found = forge.getExceptionHandler(BaseException.class, defined, null);
        assertTrue(found instanceof JsonExceptionHandler);

        // 2.
        defined[0] = MyExceptionHandler.class;
        found = forge.getExceptionHandler(BaseException.class, defined, null);
        assertTrue(found instanceof BaseExceptionHandler);

        //
        found = forge.getExceptionHandler(MyExceptionClass.class, defined, null);
        assertTrue(found instanceof MyExceptionHandler);
    }

    @Test
    void getDefaultGenericExceptionHandler() throws Exception {

        ExceptionHandler found = forge.getExceptionHandler(BaseException.class, null, null);
        assertTrue(found instanceof GenericExceptionHandler);

        found = forge.getExceptionHandler(WebApplicationException.class, null, null);
        assertTrue(found instanceof WebApplicationExceptionHandler);
    }

    @Test
    void doubleHandlerRegistration() {

        forge.getExceptionHandlers().register(MyExceptionHandler.class);

        Exception e = assertThrows(IllegalArgumentException.class, () -> forge.getExceptionHandlers().register(new MyExceptionHandler()));
        assertEquals("Exception handler for: com.zandero.rest.test.handler.MyExceptionClass " +
                         "already registered with: com.zandero.rest.test.handler.MyExceptionHandler", e.getMessage());
    }

    @Test
    void doubleHandlerRegistrationReversed() {

        forge.getExceptionHandlers().register(new MyExceptionHandler());
        Exception e = assertThrows(IllegalArgumentException.class, () -> forge.getExceptionHandlers().register(MyExceptionHandler.class));
        assertEquals("Exception handler for: com.zandero.rest.test.handler.MyExceptionClass " +
                         "already registered with: com.zandero.rest.test.handler.MyExceptionHandler", e.getMessage());
    }

    @Test
    void doubleHandlerRegistrationSameException() {

        forge.getExceptionHandlers().register(new IllegalArgumentExceptionHandler());
        Exception e = assertThrows(IllegalArgumentException.class, () -> forge.getExceptionHandlers().register(ContextExceptionHandler.class));
        assertEquals("Exception handler for: java.lang.IllegalArgumentException " +
                         "already registered with: com.zandero.rest.test.handler.IllegalArgumentExceptionHandler", e.getMessage());
    }

    @Test
    void doubleHandlerRegistrationSameExceptionReversed() {

        forge.getExceptionHandlers().register(ContextExceptionHandler.class);
        Exception e = assertThrows(IllegalArgumentException.class, () -> forge.getExceptionHandlers().register(new IllegalArgumentExceptionHandler()));
        assertEquals("Exception handler for: java.lang.IllegalArgumentException " +
                         "already registered with: com.zandero.rest.test.handler.ContextExceptionHandler", e.getMessage());
    }
}