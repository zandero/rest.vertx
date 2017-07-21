package com.zandero.rest;

import com.zandero.rest.data.RouteDefinition;
import com.zandero.utils.Assert;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
	 * @return list of definitions or empty list if none present
	 */
	public static Map<RouteDefinition, Method> get(Class clazz) {

		Assert.notNull(clazz, "Missing class with JAX-RS annotations!");

		// base
		RouteDefinition root = new RouteDefinition(clazz);

		// go over methods ...
		Map<RouteDefinition, Method> output = new LinkedHashMap<>();
		for (Method method : clazz.getMethods()) {

			if (isRestMethod(method)) { // Path must be present

				try {
					RouteDefinition definition = new RouteDefinition(root, method.getAnnotations());
					definition.setArguments(method);

					// check route path is not null
					Assert.notNullOrEmptyTrimmed(definition.getRoutePath(), "Missing route @Path!");

					output.put(definition, method);

				} catch (IllegalArgumentException e) {

					throw new IllegalArgumentException(clazz + "." + method.getName() + "() - " + e.getMessage());
				}
			}
		}

		return output;
	}

	/**
	 * A Rest method can have a Path and must have GET, POST ...
	 * @param method to examine
	 * @return true if REST method, false otherwise
	 */
	private static boolean isRestMethod(Method method) {

		List<Class<? extends Annotation>> search = Arrays.asList(Path.class,
		                                                         HttpMethod.class,
		                                                         GET.class,
		                                                         POST.class,
		                                                         PUT.class,
		                                                         DELETE.class,
		                                                         OPTIONS.class,
		                                                         HEAD.class);

		for (Class<? extends Annotation> item: search) {
			if (method.getAnnotation(item) != null) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Tries to find class with given annotation ... class it's interface or parent class
	 *
	 * @param clazz      to search
	 * @param annotation to search for
	 * @return found class with annotation or null if no class with given annotation could be found
	 */
	public static Class getClassWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
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
