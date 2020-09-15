package com.zandero.rest.data;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
class PathConverterTest {

    @Test
    void convertTest() {

        assertEquals(":test", PathConverter.convert("{test}"));
        assertEquals(":test/:test2", PathConverter.convert("{test}/{test2}"));
        assertEquals(":test/:test2/:test3", PathConverter.convert("{test}/{test2}/{test3}"));

        assertEquals(":test", PathConverter.convert(":test"));
        assertEquals(":test/:test2", PathConverter.convert(":test/{test2}"));
        assertEquals(":test/:test2/:test3", PathConverter.convert("{test}/:test2/{test3}"));

        assertEquals("/:test", PathConverter.convert("/{test}"));
        assertEquals("/:test/:test2", PathConverter.convert("/{test}/{test2}"));
        assertEquals("/:test/:test2/:test3", PathConverter.convert("/{test}/{test2}/{test3}"));

        assertEquals("/:test", PathConverter.convert("/:test"));
        assertEquals("/:test/:test2", PathConverter.convert("/:test/{test2}"));
        assertEquals("/:test/:test2/:test3", PathConverter.convert("/{test}/:test2/{test3}"));

        assertEquals("/:test", PathConverter.convert("/{test}/"));
        assertEquals("/:test/:test2", PathConverter.convert("/{test}/{test2}/"));
        assertEquals("/:test/:test2/:test3", PathConverter.convert("/{test}/{test2}/{test3}/"));

        assertEquals("/:test", PathConverter.convert("/:test/"));
        assertEquals("/:test/:test2", PathConverter.convert("/:test/{test2}/"));
        assertEquals("/:test/:test2/:test3", PathConverter.convert("/{test}/:test2/{test3}/"));
    }

    @Test
    void convertVertXRegExPath() {

        assertEquals("\\d", PathConverter.convert(":test:\\d"));
        assertEquals("\\d/:test/.*", PathConverter.convert(":test:\\d/:test/:test:.*"));
        assertEquals(".", PathConverter.convert(":test:."));
        assertEquals(":test/*.", PathConverter.convert("{test}/:test2:*."));
    }

    @Test
    void convertTest_2() {

        assertEquals("/a", PathConverter.convert("/a"));
        assertEquals("/a/b", PathConverter.convert("/a/b"));
        assertEquals("/a/b/c", PathConverter.convert("/a/b/c"));

        assertEquals("/a/:test/b", PathConverter.convert("/a/{test}/b"));
    }

    @Test
    void convertRegExTest() {

        assertEquals("/:one/\\d/:three", PathConverter.convert("/{one}/{two:\\d}/{three}"));
        assertEquals("/a/\\d/b", PathConverter.convert("/a/\\d/b"));

        assertEquals("/:+", PathConverter.convert("/:a::+"));
    }

    @Test
    void convertRegEx2Test() {

        assertEquals("/regEx/^(?!\\/api\\/).*", PathConverter.convert("/regEx/{path:^(?!\\/api\\/).*}"));
        assertEquals("\\/api", PathConverter.convert("\\/api"));
    }

    @Test
    void extractTest() {

        List<MethodParameter> list = PathConverter.extract("/a/:test/b");
        assertEquals(1, list.size());

        MethodParameter param = list.get(0);
        assertEquals("test", param.getName());
        assertEquals(ParameterType.path, param.getType());

        // 2.
        list = PathConverter.extract("/:test/:test2/:test3/");
        assertEquals(3, list.size());

        param = list.get(0);
        assertEquals("test", param.getName());
        assertEquals(ParameterType.path, param.getType());
        assertEquals(-1, param.getIndex());
        assertEquals(-1, param.getRegExIndex());
        assertEquals(1, param.getPathIndex());

        param = list.get(1);
        assertEquals("test2", param.getName());
        assertEquals(ParameterType.path, param.getType());
        assertEquals(-1, param.getIndex());
        assertEquals(-1, param.getRegExIndex());
        assertEquals(2, param.getPathIndex());

        param = list.get(2);
        assertEquals("test3", param.getName());
        assertEquals(ParameterType.path, param.getType());
        assertEquals(-1, param.getIndex());
        assertEquals(-1, param.getRegExIndex());
        assertEquals(3, param.getPathIndex());
    }

    @Test
    void extractRegExTest() {

        List<MethodParameter> list = PathConverter.extract("/a/{test:\\d}/b");
        assertEquals(1, list.size());

        MethodParameter param = list.get(0);
        assertEquals("test", param.getName());
        assertEquals("\\d", param.getRegEx());
        assertTrue(param.isRegEx());
        assertEquals(ParameterType.path, param.getType());


        list = PathConverter.extract("/:one:[A-Z]/{test:\\d}/b");
        assertEquals(2, list.size());

        param = list.get(0);
        assertEquals("one", param.getName());
        assertEquals("[A-Z]", param.getRegEx());
        assertEquals(-1, param.getIndex());
        assertEquals(0, param.getRegExIndex());
        assertEquals(1, param.getPathIndex());
        assertTrue(param.isRegEx());
        assertEquals(ParameterType.path, param.getType());

        param = list.get(1);
        assertEquals("test", param.getName());
        assertEquals("\\d", param.getRegEx());
        assertEquals(-1, param.getIndex());
        assertEquals(1, param.getRegExIndex());
        assertEquals(2, param.getPathIndex());
        assertTrue(param.isRegEx());
        assertEquals(ParameterType.path, param.getType());
    }

    @Test
    void extractRegExTest2() {

        List<MethodParameter> list = PathConverter.extract("/:one:\\d+/minus/:two:\\w+");
        assertEquals(2, list.size());

        MethodParameter param = list.get(0);
        assertEquals("one", param.getName());
        assertEquals("\\d+", param.getRegEx());
        assertTrue(param.isRegEx());
        assertEquals(-1, param.getIndex());
        assertEquals(0, param.getRegExIndex());
        assertEquals(1, param.getPathIndex());
        assertEquals(ParameterType.path, param.getType());

        param = list.get(1);
        assertEquals("two", param.getName());
        assertEquals("\\w+", param.getRegEx());
        assertTrue(param.isRegEx());
        assertEquals(-1, param.getIndex());
        assertEquals(1, param.getRegExIndex());
        assertEquals(3, param.getPathIndex());
        assertEquals(ParameterType.path, param.getType());
    }

    @Test
    void extractRegExTest3() {

        List<MethodParameter> list = PathConverter.extract("direct/{placeholder:.*}");
        assertEquals(1, list.size());

        MethodParameter param = list.get(0);
        assertEquals("placeholder", param.getName());
        assertEquals(".*", param.getRegEx());
        assertTrue(param.isRegEx());
        assertEquals(-1, param.getIndex());
        assertEquals(0, param.getRegExIndex());
        assertEquals(1, param.getPathIndex());
        assertEquals(ParameterType.path, param.getType());
    }

    @Test
    void cleanTest() {

        assertEquals("/test/test", PathConverter.clean("  //test//test  "));
    }
}