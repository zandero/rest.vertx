package com.zandero.rest.data;

import com.zandero.rest.exception.*;
import com.zandero.rest.reader.*;
import com.zandero.rest.test.data.SimulatedUser;
import com.zandero.rest.test.handler.IllegalArgumentExceptionHandler;
import io.vertx.ext.auth.AbstractUser;
import org.junit.jupiter.api.Test;

import javax.ws.rs.*;
import java.lang.reflect.Type;

import static com.zandero.rest.data.ClassUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClassUtilsTest {

    @Test
    void getGenericTypeTest() {
        assertNull(getGenericType(DummyBodyReader.class)); // type erasure ... we can't tell
        assertEquals(Integer.class, getGenericType(IntegerBodyReader.class)); // at least we know so much
        assertEquals(IllegalArgumentException.class, getGenericType(IllegalArgumentExceptionHandler.class)); // at least we know so much
        assertEquals(WebApplicationException.class, getGenericType(WebApplicationExceptionHandler.class)); // at least we know so much
    }

    @Test
    void typeAreCompatibleTest() {
        Type type = getGenericType(NumberFormatException.class);
        try {
            checkIfCompatibleType(IllegalArgumentException.class, type, "Fail");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void checkIfCompatibleTypes() {
        assertTrue(checkIfCompatibleType(SimulatedUser.class, AbstractUser.class));
    }

    @Test
    void inheritedTypeAreCompatibleTest() {

        Type type = getGenericType(WebApplicationExceptionHandler.class);
        checkIfCompatibleType(WebApplicationException.class, type, "Fail");
        checkIfCompatibleType(NotAllowedException.class, type, "Fail");
    }

    @Test
    void convertPrimitiveTypes() throws ClassFactoryException {
        assertEquals(1, stringToPrimitiveType("1", int.class));
        assertEquals(false, stringToPrimitiveType("FALSE", boolean.class));
        assertEquals('a', stringToPrimitiveType("a", char.class));
        assertEquals((short) 100, stringToPrimitiveType("100", short.class));
        assertEquals(100_100_100L, stringToPrimitiveType("100100100", long.class));
        assertEquals((float) 100100.98, stringToPrimitiveType("100100.98", float.class));
        assertEquals(100100.987, stringToPrimitiveType("100100.987", double.class));
    }

    @Test
    void convertNullableTypes() throws ClassFactoryException {
        assertEquals(1, stringToPrimitiveType("1", Integer.class));
        assertEquals(false, stringToPrimitiveType("FALSE", Boolean.class));
        assertEquals('a', stringToPrimitiveType("a", Character.class));
        assertEquals((short) 100, stringToPrimitiveType("100", Short.class));
        assertEquals(100_100_100L, stringToPrimitiveType("100100100", Long.class));
        assertEquals((float) 100100.98, stringToPrimitiveType("100100.98", Float.class));
        assertEquals(100100.987, stringToPrimitiveType("100100.987", Double.class));
    }

    @Test
    void convertFail() {
        ClassFactoryException e = assertThrows(ClassFactoryException.class,
                                               () -> stringToPrimitiveType("A", Integer.class));
        assertEquals("Failed to convert value: 'A', to primitive type: java.lang.Integer", e.getMessage());
    }
}
