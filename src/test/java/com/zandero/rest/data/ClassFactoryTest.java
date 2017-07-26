package com.zandero.rest.data;

import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.rest.exception.WebApplicationExceptionHandler;
import com.zandero.rest.reader.DummyBodyReader;
import com.zandero.rest.reader.IntegerBodyReader;
import com.zandero.rest.test.data.IntegerHolder;
import com.zandero.rest.test.data.SimulatedUser;
import com.zandero.rest.test.handler.IllegalArgumentExceptionHandler;
import com.zandero.rest.test.json.Dummy;
import org.junit.Test;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Type;

import static com.zandero.rest.data.ClassFactory.constructViaConstructor;
import static com.zandero.rest.data.ClassFactory.constructViaMethod;
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
	public void convertPrimitiveTypes() {

		assertEquals(1, ClassFactory.stringToPrimitiveType("1", int.class));
		assertEquals(false, ClassFactory.stringToPrimitiveType("FALSE", boolean.class));
		assertEquals('a', ClassFactory.stringToPrimitiveType("a", char.class));
		assertEquals((short)100, ClassFactory.stringToPrimitiveType("100", short.class));
		assertEquals(100_100_100L, ClassFactory.stringToPrimitiveType("100100100", long.class));
		assertEquals((float)100100.98, ClassFactory.stringToPrimitiveType("100100.98", float.class));
		assertEquals(100100.987, ClassFactory.stringToPrimitiveType("100100.987", double.class));
	}

	@Test
	public void convertNullableTypes() {

		assertEquals(1, ClassFactory.stringToPrimitiveType("1", Integer.class));
		assertEquals(false, ClassFactory.stringToPrimitiveType("FALSE", Boolean.class));
		assertEquals('a', ClassFactory.stringToPrimitiveType("a", Character.class));
		assertEquals((short)100, ClassFactory.stringToPrimitiveType("100", Short.class));
		assertEquals(100_100_100L, ClassFactory.stringToPrimitiveType("100100100", Long.class));
		assertEquals((float)100100.98, ClassFactory.stringToPrimitiveType("100100.98", Float.class));
		assertEquals(100100.987, ClassFactory.stringToPrimitiveType("100100.987", Double.class));
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

		IntegerHolder holder = (IntegerHolder) ClassFactory.constructType(IntegerHolder.class, "1");
		assertNotNull(holder);
		assertEquals(1, holder.value);
	}

	@Test
	public void constructViaConstructorTest() {

		Dummy dummy = (Dummy) ClassFactory.constructViaConstructor(Dummy.class, "{\"name\":\"unknown\", \"value\": \"user\"}");
		assertNotNull(dummy);
		assertEquals("unknown", dummy.name);
		assertEquals("user", dummy.value);

		SimulatedUser user = (SimulatedUser) ClassFactory.constructViaConstructor(SimulatedUser.class, "BLA");
		assertNotNull(user);
		assertEquals("BLA", user.getRole());

		IntegerHolder holder = (IntegerHolder) constructViaConstructor(IntegerHolder.class, "10");
		assertNotNull(holder);
		assertEquals(10, holder.value);
	}

	@Test
	public void constructViaMethodTest() {

		Dummy dummy = (Dummy) ClassFactory.constructViaMethod(Dummy.class, "{\"name\":\"unknown\", \"value\": \"user\"}");
		assertNotNull(dummy);
		assertEquals("unknown", dummy.name);
		assertEquals("user", dummy.value);

		SimulatedUser user = (SimulatedUser) ClassFactory.constructViaMethod(SimulatedUser.class, "BLA");
		assertNotNull(user);
		assertEquals("BLA", user.getRole());

		IntegerHolder holder = (IntegerHolder) constructViaMethod(IntegerHolder.class, "10");
		assertNull(holder);
	}
}