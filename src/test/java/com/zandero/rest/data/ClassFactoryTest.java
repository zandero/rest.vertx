package com.zandero.rest.data;

import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.rest.exception.WebApplicationExceptionHandler;
import com.zandero.rest.reader.IntegerBodyReader;
import com.zandero.rest.test.data.IntegerHolder;
import com.zandero.rest.test.data.SimulatedUser;
import com.zandero.rest.test.handler.IllegalArgumentExceptionHandler;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.test.reader.DummyBodyReader;
import org.junit.Test;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Type;

import static org.junit.Assert.*;

/**
 *
 */
public class ClassFactoryTest {

	@Test
	public void getGenericTypeTest() {

		assertNull(ClassFactory.getGenericType(DummyBodyReader.class)); // type erasure ... we can't tell

		assertEquals(Integer.class, ClassFactory.getGenericType(IntegerBodyReader.class)); // at least we know so much

		assertEquals(IllegalArgumentException.class, ClassFactory.getGenericType(IllegalArgumentExceptionHandler.class)); // at least we know so much

		assertEquals(WebApplicationException.class, ClassFactory.getGenericType(WebApplicationExceptionHandler.class)); // at least we know so much
	}

	@Test
	public void typeAreCompatibleTest() {

		Type type = ClassFactory.getGenericType(NumberFormatException.class);
		try {
			ClassFactory.checkIfCompatibleTypes(IllegalArgumentException.class, type, "Fail");
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void inheritedTypeAreCompatibleTest() {

		Type type = ClassFactory.getGenericType(WebApplicationExceptionHandler.class);
		try {
			ClassFactory.checkIfCompatibleTypes(WebApplicationException.class, type, "Fail");
		}
		catch (Exception e) {
			fail(e.getMessage());
		}

		try {
			ClassFactory.checkIfCompatibleTypes(NotAllowedException.class, type, "Fail");
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void constructTypeTest() throws ClassFactoryException {

		Object out = ClassFactory.constructType(Dummy.class, "{\"name\":\"unknown\", \"value\": \"user\"}");
		assertNotNull(out);
		assertTrue(out instanceof Dummy);

		Dummy dummy = (Dummy)out;
		assertEquals("unknown", dummy.name);
		assertEquals("user", dummy.value);

		SimulatedUser user = (SimulatedUser) ClassFactory.constructType(SimulatedUser.class, "BLA");
		assertNotNull(user);
		assertEquals("BLA", user.getRole());
	}

	@Test
	public void constructIntegerTest() throws ClassFactoryException {

		IntegerHolder holder = (IntegerHolder) ClassFactory.constructType(IntegerHolder.class, "1");
		assertNotNull(holder);
		assertEquals(1, holder.value);
	}
}