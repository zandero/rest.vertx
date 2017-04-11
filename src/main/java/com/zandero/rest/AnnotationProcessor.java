package com.zandero.rest;

import com.zandero.rest.data.RouteDefinition;
import com.zandero.utils.Assert;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Collects all JAX-RS annotations to be transformed into routes
 */
public final class AnnotationProcessor {

	private AnnotationProcessor() {
		// hide constructor
	}

	/**
	 * Checks class for JAX-RS annotations and returns a list of route definitions to build routes upon
	 *
	 * @param clazz to be checked
	 * @return list of definitions or emtpy list if none present
	 */
	public static Map<RouteDefinition, Method> get(Class clazz) {

		Assert.notNull(clazz, "Missing class with JAX-RS annotations!");

		// base
		RouteDefinition root = new RouteDefinition(clazz.getAnnotations());

		// go over methods ...
		Map<RouteDefinition, Method> output = new HashMap<>();
		for (Method method : clazz.getMethods()) {

			if (method.getAnnotation(Path.class) != null) { // Path must be present
				RouteDefinition definition = new RouteDefinition(root, method.getAnnotations());
				output.put(definition, method);
			}
		}

		return output;
	}
}
