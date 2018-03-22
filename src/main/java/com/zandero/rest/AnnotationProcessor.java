package com.zandero.rest;

import com.zandero.rest.annotation.CONNECT;
import com.zandero.rest.annotation.TRACE;
import com.zandero.rest.data.MethodParameter;
import com.zandero.rest.data.ParameterType;
import com.zandero.rest.data.RouteDefinition;
import com.zandero.utils.Assert;
import com.zandero.utils.StringUtils;

import javax.ws.rs.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Collects all JAX-RS annotations to be transformed into routes
 */
public final class AnnotationProcessor {

	private static final List<Class<? extends Annotation>> REST_ANNOTATIONS = Arrays.asList(Path.class,
	                                                                                        HttpMethod.class,
	                                                                                        GET.class,
	                                                                                        POST.class,
	                                                                                        PUT.class,
	                                                                                        DELETE.class,
	                                                                                        PATCH.class,
	                                                                                        OPTIONS.class,
	                                                                                        TRACE.class,
	                                                                                        CONNECT.class,
	                                                                                        HEAD.class);

	private AnnotationProcessor() {
		// hide constructor
	}

	public static Map<RouteDefinition, Method> get(Class clazz) {

		Map<RouteDefinition, Method> out = collect(clazz);

		// Final check if definitions are OK
		for (RouteDefinition definition: out.keySet()) {

			Method method = out.get(definition);
			Assert.notNull(definition.getMethod(), "Missing @GET, @POST, @PUT, @DELETE, @PATCH, @OPTIONS, @TRACE, @CONNECT or @HEAD annotation on: " +
			                                       getClassMethod(clazz, method) + "!");

			Assert.notNull(definition.getRoutePath(), getClassMethod(clazz, method) + " - Missing route @Path!");

			int bodyParamCount = 0;
			for (MethodParameter param: definition.getParameters()) {
				if (bodyParamCount > 0 && (ParameterType.body.equals(param.getType()) || ParameterType.unknown.equals(param.getType()))) {
					// OK we have to body params ...
					throw new IllegalArgumentException(getClassMethod(clazz, method) + " - two or more body arguments given. " +
					                                   "Missing argument annotation (@PathParam, @QueryParam, @FormParam, @HeaderParam, @CookieParam or @Context) for: " +
					                                   param.getType() + " " + param.getName() + "!");
				}

				if (ParameterType.unknown.equals(param.getType())) { // proclaim as body param
					// check if method allows for a body param
					Assert.isTrue(definition.requestHasBody(), getClassMethod(clazz, method) + " - " +
					                                           "Missing argument annotation (@PathParam, @QueryParam, @FormParam, @HeaderParam, @CookieParam or @Context) for: " + param.getName() + "!");

					param.setType(ParameterType.body);
				}

				if (ParameterType.body.equals(param.getType())) {
					bodyParamCount++;
				}
			}
		}

		return out;
	}

	/**
	 * Gets all route definitions for base class / interfaces and inherited / abstract classes
	 * @param clazz to inspect
	 * @return collection of all definitions
	 */
	private static Map<RouteDefinition, Method> collect(Class clazz) {

		Map<RouteDefinition, Method> out = getDefinitions(clazz);
		for (Class inter : clazz.getInterfaces()) {

			Map<RouteDefinition, Method> found = collect(inter);
			out = join(out, found);
		}

		Class superClass = clazz.getSuperclass();
		if (superClass != Object.class && superClass != null) {
			Map<RouteDefinition, Method> found = collect(superClass);
			out = join(out, found);
		}

		return out;
	}
	/**
	 * Joins additional data provided in subclass/ interfaces with base definition
	 * @param base base definition
	 * @param add additional definition
	 * @return joined definition
	 */
	private static Map<RouteDefinition, Method> join(Map<RouteDefinition, Method> base, Map<RouteDefinition, Method> add) {

		for (RouteDefinition definition: base.keySet()) {
			Method method = base.get(definition);

			RouteDefinition additional = find(add, definition, method);
			definition.join(additional);
		}

		return base;
	}

	/**
	 * Find mathing definition for same method ...
	 * @param add to search
	 * @param definition base
	 * @param method base
	 * @return found definition or null if no match found
	 */
	private static RouteDefinition find(Map<RouteDefinition, Method> add, RouteDefinition definition, Method method) {

		if (add == null || add.size() == 0) {
			return null;
		}

		for (RouteDefinition additional: add.keySet()) {
			Method match = add.get(additional);

			if (isMatching(method, match)) {
				return additional;
			}
		}

		return null;
	}

	private static boolean isMatching(Method base, Method compare) {

		if (base.getName().equals(compare.getName()) &&
			base.getParameterCount() == compare.getParameterCount()) {

			Class<?>[] typeBase = base.getParameterTypes();
			Class<?>[] typeCompare = compare.getParameterTypes();

			for (int index = 0; index < typeBase.length; index++) {
				Class clazzBase = typeBase[index];
				Class clazzCompare = typeCompare[index];
				if (!clazzBase.equals(clazzCompare)) {
					return false;
				}
			}

			return true;
		}

		return false;
	}


	/**
	 * Checks class for JAX-RS annotations and returns a list of route definitions to build routes upon
	 *
	 * @param clazz to be checked
	 * @return list of definitions or empty list if none present
	 */
	private static Map<RouteDefinition, Method> getDefinitions(Class clazz) {

		Assert.notNull(clazz, "Missing class with JAX-RS annotations!");

		// base
		RouteDefinition root = new RouteDefinition(clazz);

		// go over methods ...
		Map<RouteDefinition, Method> output = new LinkedHashMap<>();
		for (Method method : clazz.getMethods()) {

			if (isRestCompatible(method)) { // Path must be present

				try {
					RouteDefinition definition = new RouteDefinition(root, method);
					output.put(definition, method);
				}
				catch (IllegalArgumentException e) {

					throw new IllegalArgumentException(getClassMethod(clazz, method) + " - " + e.getMessage());
				}
			}
		}

		return output;
	}

	private static boolean isRestCompatible(Method method) {

		return (!method.getDeclaringClass().isInstance(Object.class) &&
		        !isNative(method) && !isFinal(method) &&
		        (isPublic(method) || isInterface(method) || isAbstract(method)));
	}

	private static boolean isNative(Method method) {
		return ((method.getModifiers() & Modifier.NATIVE) != 0);
	}

	private static boolean isFinal(Method method) {
		return ((method.getModifiers() & Modifier.FINAL) != 0);
	}

	private static boolean isPublic(Method method) {
		return ((method.getModifiers() & Modifier.PUBLIC) != 0);
	}

	private static boolean isInterface(Method method) {
		return ((method.getModifiers() & Modifier.INTERFACE) != 0);
	}

	private static boolean isAbstract(Method method) {
		return ((method.getModifiers() & Modifier.ABSTRACT) != 0);
	}

	private static String getClassMethod(Class clazz, Method method) {
		StringBuilder builder = new StringBuilder();
		builder.append(clazz.getName()).append(".").append(method.getName());
		builder.append("(");
		if (method.getParameterCount() > 0) {
			for (int i = 0; i < method.getParameterCount(); i++) {
				Parameter param = method.getParameters()[i];
				builder.append(param.getType().getSimpleName()).append(" ").append(param.getName());

				if (i + 1 < method.getParameterCount()) {
					builder.append(", ");
				}
			}
		}
		builder.append(")");
		return builder.toString();
	}
}
