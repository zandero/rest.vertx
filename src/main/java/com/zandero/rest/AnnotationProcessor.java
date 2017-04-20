package com.zandero.rest;

import com.zandero.rest.data.RouteDefinition;
import com.zandero.utils.Assert;

import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
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
		RouteDefinition root = new RouteDefinition(clazz);

		// go over methods ...
		Map<RouteDefinition, Method> output = new HashMap<>();
		for (Method method : clazz.getMethods()) {

			if (method.getAnnotation(Path.class) != null) { // Path must be present

				RouteDefinition definition = new RouteDefinition(root, method.getAnnotations());
				definition.setArguments(method);

				output.put(definition, method);
			}
		}

		return output;
	}

	/**
	 * Tries to find class with given annotation ... class it's interface or parent class
	 * @param clazz to search
	 * @param annotation to search for
	 * @return found class with annotation or null if no class with given annotation could be found
	 */
	public static Class getClassWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation)
	{
		if (clazz.isAnnotationPresent(annotation)) {
			return clazz;
		}

		for (Class inter : clazz.getInterfaces()) {
			if (inter.isAnnotationPresent(annotation)) {
				return inter;
			}
		}

		Class superClass = clazz.getSuperclass();
		if (superClass != Object.class && superClass != null) {
			return getClassWithAnnotation(superClass, annotation);
		}

		return null;
	}
}
