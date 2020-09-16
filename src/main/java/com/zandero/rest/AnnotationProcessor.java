package com.zandero.rest;

import com.zandero.rest.data.*;
import com.zandero.utils.Assert;

import java.lang.reflect.*;
import java.util.*;

/**
 * Collects all JAX-RS annotations to be transformed into routes
 */
public final class AnnotationProcessor {

    /**
     * Methods names of Object that will be ignored when binding RESTs
     */
    private static final Set<String> OBJECT_METHODS;

    static {
        OBJECT_METHODS = new HashSet<>();
        OBJECT_METHODS.add("equals");
        OBJECT_METHODS.add("toString");
        OBJECT_METHODS.add("wait");
    }

    private AnnotationProcessor() {
        // hide constructor
    }

    /**
     * Extracts all JAX-RS annotated methods from class and all its subclasses and interfaces
     *
     * @param clazz to extract from
     * @return map of routeDefinition and class method to execute
     */
    public static Map<RouteDefinition, Method> get(Class<?> clazz) {

        Map<RouteDefinition, Method> out = new HashMap<>();
        Map<RouteDefinition, Method> candidates = collect(clazz);

        // Final check if definitions are OK
        RouteDefinition classDefinition = null;
        for (RouteDefinition definition : candidates.keySet()) {

            if (definition.getMethod() == null) { // skip if class definition
                classDefinition = definition;
                continue;
            }

            Method method = candidates.get(definition);
            Assert.notNull(definition.getRoutePath(), getClassMethod(clazz, method) + " - Missing route @Path!");

            int bodyParamCount = 0;
            for (MethodParameter param : definition.getParameters()) {
                if (bodyParamCount > 0 && (ParameterType.body.equals(param.getType()) || ParameterType.unknown.equals(param.getType()))) {
                    // OK we have to body params ...
                    throw new IllegalArgumentException(getClassMethod(clazz, method) + " - to many body arguments given. " +
                                                           "Missing argument annotation (@PathParam, @QueryParam, @FormParam, @HeaderParam, @CookieParam or @Context) for: " +
                                                           param.getType() + " " + param.getName() + "!");
                }

                if (ParameterType.unknown.equals(param.getType())) { // proclaim as body param
                    // check if method allows for a body param
                    Assert.isTrue(definition.requestCanHaveBody(), getClassMethod(clazz, method) + " - " +
                                                                   "Missing argument annotation (@PathParam, @QueryParam, @FormParam, @HeaderParam, @CookieParam or @Context) for: " +
                                                                   param.getName() + "!");

                    param.setType(ParameterType.body);
                }

                if (ParameterType.body.equals(param.getType())) {
                    bodyParamCount++;
                }
            }

            out.put(definition, method);
        }

        join(out, classDefinition);
        return out;
    }

    /**
     * Gets all route definitions for base class / interfaces and inherited / abstract classes
     *
     * @param clazz to inspect
     * @return collection of all definitions
     */
    private static Map<RouteDefinition, Method> collect(Class<?> clazz) {

        // Addressing issue #55, where Guice alters annotations
        if (clazz.getName().contains("$$EnhancerByGuice$$")) { // Skip Guice enhanced clazz and go to "original"
            clazz = clazz.getSuperclass();
        }

        Map<RouteDefinition, Method> out = getDefinitions(clazz);
        RouteDefinition classDefinition = getClassDefinition(out);

        for (Class<?> inter : clazz.getInterfaces()) {
            Map<RouteDefinition, Method> found = collect(inter);

            RouteDefinition interfaceClassDefinition = getClassDefinition(found);
            if (classDefinition != null) {
                classDefinition.join(interfaceClassDefinition);
            }

            join(out, found);
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class && superClass != null) {
            Map<RouteDefinition, Method> found = collect(superClass);

            RouteDefinition superClassDefinition = getClassDefinition(found);
            if (classDefinition != null) {
                classDefinition.join(superClassDefinition);
            }

            join(out, found);
        }

        //
        out.put(classDefinition, null);
        return out;
    }

    private static RouteDefinition getClassDefinition(Map<RouteDefinition, Method> out) {
        for (RouteDefinition def : out.keySet()) {
            if (out.get(def) == null) {
                return def;
            }
        }
        return null;
    }

    /**
     * Joins additional data provided in subclass/ interfaces with base definition
     *
     * @param base base definition
     * @param add  additional definition
     */
    private static void join(Map<RouteDefinition, Method> base, Map<RouteDefinition, Method> add) {

        for (RouteDefinition definition : base.keySet()) {
            Method method = base.get(definition);

            RouteDefinition additional = find(add, method);
            definition.join(additional);
        }
    }

    private static void join(Map<RouteDefinition, Method> base, RouteDefinition classDefinition) {

        for (RouteDefinition definition : base.keySet()) {
            definition.join(classDefinition);
        }
    }

    /**
     * Find matching definition for same method ...
     *
     * @param add    to search
     * @param method base
     * @return found definition or null if no match found
     */
    private static RouteDefinition find(Map<RouteDefinition, Method> add, Method method) {

        if (add == null || add.size() == 0) {
            return null;
        }

        for (RouteDefinition additional : add.keySet()) {
            Method match = add.get(additional);

            if (match == null) {
                // root / class definition ...
                continue;
            }

            if (isMatching(method, match)) {
                return additional;
            }
        }

        return null;
    }

    /**
     * Checks if methods in base and inherited/abstract class are the same method
     *
     * @param base    method
     * @param compare to compare
     * @return true if the same, false otherwise
     */
    private static boolean isMatching(Method base, Method compare) {

        if (base == null && compare == null) {
            return true;
        }

        if (base == null || compare == null) {
            return false;
        }

        // if names and argument types match ... then this are the same method
        if (base.getName().equals(compare.getName()) &&
                base.getParameterCount() == compare.getParameterCount()) {

            Class<?>[] typeBase = base.getParameterTypes();
            Class<?>[] typeCompare = compare.getParameterTypes();

            for (int index = 0; index < typeBase.length; index++) {
                Class<?> clazzBase = typeBase[index];
                Class<?> clazzCompare = typeCompare[index];
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
    private static Map<RouteDefinition, Method> getDefinitions(Class<?> clazz) {

        Assert.notNull(clazz, "Missing class with JAX-RS annotations!");

        Map<RouteDefinition, Method> output = new LinkedHashMap<>();

        // base
        RouteDefinition root = new RouteDefinition(clazz);
        output.put(root, null); // add class definition

        // go over methods ...
        for (Method method : clazz.getMethods()) {
            if (isRestCompatible(method)) {
                try {
                    RouteDefinition definition = new RouteDefinition(root, method);
                    output.put(definition, method);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(getClassMethod(clazz, method) + " - " + e.getMessage());
                }
            }
        }

        return output;
    }

    /**
     * @param method to check if REST compatible
     * @return true if REST method, false otherwise
     */
    private static boolean isRestCompatible(Method method) {

        return (!method.getDeclaringClass().isInstance(Object.class) &&
                    !isNative(method) && !isObjectMethod(method) &&
                    (isPublic(method) || isInterface(method) || isAbstract(method)));
    }

    private static boolean isObjectMethod(Method method) {
        return OBJECT_METHODS.contains(method.getName());
    }

    private static boolean isNative(Method method) {
        return ((method.getModifiers() & Modifier.NATIVE) != 0);
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

    /**
     * Helper to convert class/method to String for reporting purposes
     *
     * @param clazz  holding method
     * @param method method in class
     * @return class.method(type arg0, type arg1 .. type argN)
     */
    private static String getClassMethod(Class<?> clazz, Method method) {
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

    public static Map<String, String> getNameValuePairs(String[] values) {
        if (values == null || values.length == 0) {
            return null;
        }

        Map<String, String> output = new HashMap<>();
        for (String item : values) {

            // default if split point can not be found
            String name = item;
            String value = "";

            int idx = item.indexOf(":");
            if (idx <= 0) {
                idx = item.indexOf(" ");
            }

            if (idx > 0) {
                name = item.substring(0, idx).trim();
                value = item.substring(idx + 1).trim();
            }

            output.put(name, value);
        }

        return output;
    }
}
