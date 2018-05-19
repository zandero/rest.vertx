package com.zandero.rest.exception;

import com.zandero.rest.test.exceptions.BaseException;
import com.zandero.rest.test.exceptions.InheritedBaseException;
import com.zandero.rest.test.exceptions.InheritedFromInheritedException;
import com.zandero.rest.test.handler.*;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

}