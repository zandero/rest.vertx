package com.zandero.resttest.data;

import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.exception.*;
import com.zandero.rest.utils.Pair;
import com.zandero.resttest.test.data.*;
import com.zandero.resttest.test.json.Dummy;
import io.vertx.core.http.*;
import io.vertx.ext.web.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import static com.zandero.rest.data.ClassFactory.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 *
 */
@ExtendWith(MockitoExtension.class)
class ClassFactoryTest {

    @Mock
    RoutingContext context;

    @Mock
    HttpServerRequest request;

    @Test
    void constructTypeTest() throws ClassFactoryException {

        Object out = ClassFactory.constructType(Dummy.class, "{\"name\":\"unknown\", \"value\": \"user\"}");
        assertNotNull(out);
        assertTrue(out instanceof Dummy);

        Dummy dummy = (Dummy) out;
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
    void constructViaConstructorTest() {

        Pair<Boolean, Dummy> dummy = ClassFactory.constructViaConstructor(Dummy.class, "{\"name\":\"unknown\", \"value\": \"user\"}");
        assertNotNull(dummy);
        assertEquals("unknown", dummy.getValue().name);
        assertEquals("user", dummy.getValue().value);

        Pair<Boolean, SimulatedUser> user = ClassFactory.constructViaConstructor(SimulatedUser.class, "BLA");
        assertNotNull(user);
        assertEquals("BLA", user.getValue().getRole());

        Pair<Boolean, IntegerHolder> holder = constructViaConstructor(IntegerHolder.class, "10");
        assertNotNull(holder);
        assertEquals(10, holder.getValue().value);
    }

    @Test
    void constructViaMethodTest() {

        Pair<Boolean, Dummy> dummy = ClassFactory.constructViaMethod(Dummy.class, "{\"name\":\"unknown\", \"value\": \"user\"}");
        assertNotNull(dummy);
        assertEquals("unknown", dummy.getValue().name);
        assertEquals("user", dummy.getValue().value);

        Pair<Boolean, SimulatedUser> user = ClassFactory.constructViaMethod(SimulatedUser.class, "BLA");
        assertNotNull(user);

        assertNotNull(user.getKey());
        assertNotNull(user.getValue());

        assertEquals("BLA", user.getValue().getRole());

        Pair<Boolean, IntegerHolder> holder = constructViaMethod(IntegerHolder.class, "10");
        assertFalse(holder.getKey());
        assertNull(holder.getValue());
    }

    @Test
    void constructEnumTest() {
        Pair<Boolean, MyEnum> value = ClassFactory.constructViaMethod(MyEnum.class, "one");
        assertEquals(MyEnum.one, value.getValue());

        Pair<Boolean, MyOtherEnum> other = ClassFactory.constructViaMethod(MyOtherEnum.class, "one");
        assertEquals(MyOtherEnum.one, other.getValue());

        other = ClassFactory.constructViaMethod(MyOtherEnum.class, "2");
        assertEquals(MyOtherEnum.two, other.getValue());

        other = ClassFactory.constructViaMethod(MyOtherEnum.class, "");
        assertEquals(true, other.getKey());
        assertNull(other.getValue());
    }

    @Test
    void constructViaContext() throws ClassFactoryException {

        RoutingContext context = Mockito.mock(RoutingContext.class);
        HttpServerRequest request = Mockito.mock(HttpServerRequest.class);

        Mockito.when(context.request()).thenReturn(request);
        Mockito.when(request.getParam("path")).thenReturn("SomePath");
        Mockito.when(request.getHeader("MyHeader")).thenReturn("true");
        Mockito.when(request.query()).thenReturn("query=1");
        Mockito.when(request.getCookie("chocolate")).thenReturn(Cookie.cookie("chocolate", "tasty"));

        MyComplexBean instance = (MyComplexBean) ClassFactory.newInstanceOf(MyComplexBean.class, context);
        assertNotNull(instance);
        assertEquals("Header: true, Path: SomePath, Query: 1, Cookie: tasty, Matrix: null", instance.toString());
    }

    @Test
    void constructViaContextFail() {

        Mockito.when(context.request()).thenReturn(request);
        Mockito.when(request.getParam("path")).thenReturn("SomePath");
        Mockito.when(request.getHeader("MyHeader")).thenReturn("BLA"); // invalid type
        Mockito.when(request.query()).thenReturn("query=A"); // invalid type
        Mockito.when(request.getCookie("chocolate")).thenReturn(Cookie.cookie("chocolate", "tasty"));

        ClassFactoryException ex = assertThrows(ClassFactoryException.class, () -> ClassFactory.newInstanceOf(MyComplexBean.class, context));
        assertEquals("Failed to instantiate class, with constructor: " +
                         "com.zandero.resttest.test.data.MyComplexBean(String arg0=SomePath, boolean arg1=BLA, int arg2=A, String arg3=tasty). " +
                         "Failed to convert value: 'A', to primitive type: int",
                     ex.getMessage());
    }
}