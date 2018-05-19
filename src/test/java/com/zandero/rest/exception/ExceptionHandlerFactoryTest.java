package com.zandero.rest.exception;

import com.zandero.rest.test.exceptions.BaseException;
import com.zandero.rest.test.exceptions.InheritedBaseException;
import com.zandero.rest.test.exceptions.InheritedFromInheritedException;
import com.zandero.rest.test.handler.*;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;

import static org.junit.Assert.*;

/**
 *
 */
public class ExceptionHandlerFactoryTest {

	private ExceptionHandlerFactory factory;

	@Before public void setUp() {
		factory = new ExceptionHandlerFactory();
	}

	@Test public void findMatchingHandler() throws Exception {

		factory.register(BaseExceptionHandler.class);

		// find it
		ExceptionHandler found = factory.getExceptionHandler(BaseException.class, null, null, null);
		assertNotNull(found);
		assertTrue(found instanceof BaseExceptionHandler);
	}

	@Test public void findMatchingInheritedHandler() throws Exception {

		factory.register(InheritedBaseExceptionHandler.class, BaseExceptionHandler.class);

		// find it
		ExceptionHandler found = factory.getExceptionHandler(BaseException.class, null, null, null);
		assertNotNull(found);
		assertTrue(found instanceof BaseExceptionHandler);

		found = factory.getExceptionHandler(InheritedBaseException.class, null, null, null);
		assertNotNull(found);
		assertTrue(found instanceof InheritedBaseExceptionHandler);
	}

	@Test public void findMatchingInheritedInheritedHandler() throws Exception {

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
	@Test public void findMatchingInstanceHandler() throws Exception {

		factory.register(new BaseExceptionHandler());

		// find it
		ExceptionHandler found = factory.getExceptionHandler(BaseException.class, null, null, null);
		assertTrue(found instanceof BaseExceptionHandler);
	}

	@Test public void findMatchingInheritedInstanceHandler() throws Exception {

		factory.register(new InheritedBaseExceptionHandler(), new BaseExceptionHandler());

		// find it
		ExceptionHandler found = factory.getExceptionHandler(BaseException.class, null, null, null);
		assertTrue(found instanceof BaseExceptionHandler);

		found = factory.getExceptionHandler(InheritedBaseException.class, null, null, null);
		assertTrue(found instanceof InheritedBaseExceptionHandler);
	}

	@Test public void findMatchingInheritedInheritedInstanceHandler() throws Exception {

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

	@Test public void findMatchingHandlerWithDefinition() throws Exception {

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

	@Test public void getDefaultGenericExceptionHandler() throws Exception {

		ExceptionHandler found = factory.getExceptionHandler(BaseException.class, null, null, null);
		assertTrue(found instanceof GenericExceptionHandler);

		found = factory.getExceptionHandler(WebApplicationException.class, null, null, null);
		assertTrue(found instanceof WebApplicationExceptionHandler);
	}

	@Test public void doubleHandlerRegistration() {

		factory.register(MyExceptionHandler.class);
		try {
			factory.register(new MyExceptionHandler());
			fail();
		}
		catch (IllegalArgumentException e) {
			assertEquals("Exception handler for: com.zandero.rest.test.handler.MyExceptionClass " +
			             "already registered with: com.zandero.rest.test.handler.MyExceptionHandler", e.getMessage());
		}
	}

	@Test public void doubleHandlerRegistrationReversed() {

		factory.register(new MyExceptionHandler());
		try {
			factory.register(MyExceptionHandler.class);
			fail();
		}
		catch (IllegalArgumentException e) {
			assertEquals("Exception handler for: com.zandero.rest.test.handler.MyExceptionClass " +
			             "already registered with: com.zandero.rest.test.handler.MyExceptionHandler", e.getMessage());
		}
	}

	@Test public void doubleHandlerRegistrationSameException() {

		factory.register(new IllegalArgumentExceptionHandler());
		try {
			factory.register(ContextExceptionHandler.class);
			fail();
		}
		catch (IllegalArgumentException e) {
			assertEquals("Exception handler for: java.lang.IllegalArgumentException " +
			             "already registered with: com.zandero.rest.test.handler.IllegalArgumentExceptionHandler", e.getMessage());
		}
	}

	@Test public void doubleHandlerRegistrationSameExceptionReversed() {

		factory.register(ContextExceptionHandler.class);
		try {
			factory.register(new IllegalArgumentExceptionHandler());
			fail();
		}
		catch (IllegalArgumentException e) {
			assertEquals("Exception handler for: java.lang.IllegalArgumentException " +
			             "already registered with: com.zandero.rest.test.handler.ContextExceptionHandler", e.getMessage());
		}
	}
}