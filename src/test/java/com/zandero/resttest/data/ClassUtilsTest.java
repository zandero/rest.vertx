package com.zandero.resttest.data;

import com.zandero.rest.exception.*;
import com.zandero.rest.writer.*;
import com.zandero.resttest.reader.DummyBodyReader;
import com.zandero.resttest.reader.IntegerBodyReader;
import com.zandero.resttest.test.data.SimulatedUser;
import com.zandero.resttest.test.handler.DummyWriter;
import com.zandero.resttest.test.handler.FutureDummyWriter;
import com.zandero.resttest.test.handler.IllegalArgumentExceptionHandler;
import com.zandero.resttest.test.json.Dummy;
import com.zandero.resttest.writer.TestDummyWriter;
import io.vertx.ext.auth.User;
import org.junit.jupiter.api.*;

import jakarta.ws.rs.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

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

        FutureDummyWriter futureWriter = new FutureDummyWriter();
        Type futureWriterType = getGenericType(futureWriter.getClass());
        assertTrue(checkIfCompatibleType(CompletableFuture.class, futureWriterType));

        List<User> list = new ArrayList<>();
        assertTrue(checkIfCompatibleType(list.getClass(), List.class));

        DummyWriter writer = new DummyWriter();
        Type writerType = getGenericType(writer.getClass());
        assertTrue(checkIfCompatibleType(Dummy.class, writerType));

        assertTrue(checkIfCompatibleType(GenericExceptionHandler.class, ExceptionHandler.class));
        assertTrue(checkIfCompatibleType(TestDummyWriter.class, HttpResponseWriter.class));
        assertTrue(checkIfCompatibleType(SimulatedUser.class, User.class));
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
