package com.zandero.rest;

import com.zandero.rest.data.RouteDefinition;
import com.zandero.utils.Assert;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

import javax.ws.rs.Path;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
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

			if (method.getAnnotation(Path.class) != null) { // Path must be present

				try {
					RouteDefinition definition = new RouteDefinition(root, method.getAnnotations());
					definition.setArguments(method);

					output.put(definition, method);

				} catch (IllegalArgumentException e) {

					throw new IllegalArgumentException(clazz + "." + method.getName() + "() - " + e.getMessage());
				}
			}
		}

		return output;
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

	public static Type getGenericType(Class clazz) {

		Assert.notNull(clazz, "Missing class!");
		Type[] genericInterfaces = clazz.getGenericInterfaces();
		for (Type genericInterface : genericInterfaces) {

			if (genericInterface instanceof ParameterizedType) {

				Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
				return genericTypes[0];
			}
		}

		return null;
	}

	public static void checkIfCompatibleTypes(Class<?> expected, Type actual, String message) {

		boolean compatibleTypes = checkIfCompatibleTypes(expected, actual);
		Assert.isTrue(compatibleTypes, message);
	}

	public static boolean checkIfCompatibleTypes(Class<?> expected, Type actual) {

		if (actual == null) {
			return true;
		}

		if (actual instanceof ParameterizedType) {
			return expected.isAssignableFrom(((ParameterizedTypeImpl) actual).getRawType());
		} else if (actual instanceof TypeVariableImpl) { // we don't know at this point ... generic type
			return true;
		} else {
			return expected.equals(actual) || expected.isInstance(actual) || ((Class)actual).isAssignableFrom(expected);
		}
	}
}
