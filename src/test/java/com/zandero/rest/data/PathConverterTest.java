package com.zandero.rest.data;

import org.junit.Test;

import javax.ws.rs.Path;

import java.lang.reflect.Parameter;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class PathConverterTest {

	@Test
	public void convertTest() throws Exception {

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

		assertEquals("/:test/", PathConverter.convert("/{test}/"));
		assertEquals("/:test/:test2/", PathConverter.convert("/{test}/{test2}/"));
		assertEquals("/:test/:test2/:test3/", PathConverter.convert("/{test}/{test2}/{test3}/"));

		assertEquals("/:test/", PathConverter.convert("/:test/"));
		assertEquals("/:test/:test2/", PathConverter.convert("/:test/{test2}/"));
		assertEquals("/:test/:test2/:test3/", PathConverter.convert("/{test}/:test2/{test3}/"));
	}

	@Test
	public void convertTest_2() {

		assertEquals("/a", PathConverter.convert("/a"));
		assertEquals("/a/b", PathConverter.convert("/a/b"));
		assertEquals("/a/b/c", PathConverter.convert("/a/b/c"));

		assertEquals("/a/:test/b", PathConverter.convert("/a/{test}/b"));
	}

	@Test
	public void extractTest() {

		List<MethodParameter> list = PathConverter.extract("/a/:test/b");
		assertEquals(1, list.size());
		assertEquals("test", list.get(0).getName());
		assertEquals(ParameterType.path, list.get(0).getType());

		// 2.
		list = PathConverter.extract("/:test/:test2/:test3/");
		assertEquals(3, list.size());

		assertEquals("test", list.get(0).getName());
		assertEquals(ParameterType.path, list.get(0).getType());

		assertEquals("test2", list.get(1).getName());
		assertEquals(ParameterType.path, list.get(1).getType());

		assertEquals("test3", list.get(2).getName());
		assertEquals(ParameterType.path, list.get(2).getType());
	}

}