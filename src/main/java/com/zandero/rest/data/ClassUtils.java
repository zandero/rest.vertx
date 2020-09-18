package com.zandero.rest.data;

import com.zandero.rest.annotation.SuppressCheck;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.utils.*;
import org.slf4j.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.lang.reflect.*;
import java.util.*;

public final class ClassUtils {

    private ClassUtils() {
        // hide constructor
    }

    private final static Logger log = LoggerFactory.getLogger(ClassUtils.class);

    /**
     * map of nullable to primitive type
     */
    private static final Map<String, Class<?>> PRIMITIVE_NULLABLE;

    static {
        PRIMITIVE_NULLABLE = new HashMap<>();
        PRIMITIVE_NULLABLE.put(Boolean.class.getTypeName(), boolean.class);
        PRIMITIVE_NULLABLE.put(Byte.class.getTypeName(), byte.class);
        PRIMITIVE_NULLABLE.put(Character.class.getTypeName(), char.class);
        PRIMITIVE_NULLABLE.put(Short.class.getTypeName(), short.class);
        PRIMITIVE_NULLABLE.put(Integer.class.getTypeName(), int.class);
        PRIMITIVE_NULLABLE.put(Long.class.getTypeName(), long.class);
        PRIMITIVE_NULLABLE.put(Float.class.getTypeName(), float.class);
        PRIMITIVE_NULLABLE.put(Double.class.getTypeName(), double.class);
    }

    public static final Class<?>[] PRIMITIVE_TYPE = new Class[]{
        String.class,
        int.class, Integer.class,
        boolean.class, Boolean.class,
        byte.class, Byte.class,
        char.class, Character.class,
        short.class, Short.class,
        long.class, Long.class,
        float.class, Float.class,
        double.class, Double.class
    };

    public static Type getGenericType(Class<?> clazz) {

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

    static boolean checkIfCompatibleTypes(Class<?> expected, Type... actual) {

        for (Type item : actual) {
            if (checkIfCompatibleType(expected, item)) {
                return true;
            }
        }

        return false;
    }

    public static void checkIfCompatibleType(Class<?> expected, Type actual, String message) {

        boolean compatibleTypes = checkIfCompatibleType(expected, actual);
        Assert.isTrue(compatibleTypes, message);
    }

    public static boolean checkIfCompatibleType(Class<?> expected, Type actual) {

        if (expected == null) {
            return false;
        }

        if (actual == null) {
            return true;
        }

        if (expected.isPrimitive()) {
            actual = convertToPrimitiveType(actual);
        }

        if (actual instanceof ParameterizedType) {
            return expected.isAssignableFrom(((ParameterizedTypeImpl) actual).getRawType());
        }

        if (actual instanceof TypeVariableImpl) { // we don't know at this point ... generic type
            return true;
        }

        return expected.equals(actual) || expected.isInstance(actual) || ((Class<?>) actual).isAssignableFrom(expected);
    }


    static <T> boolean isPrimitiveType(Class<T> type) {

        for (Class<?> primitive : PRIMITIVE_TYPE) {
            if (type.isAssignableFrom(primitive)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Converts type to primitive type if possible
     *
     * @param type to convert
     * @return primitive type or original if no conversion possible
     */
    private static Type convertToPrimitiveType(Type type) {

        if (type == null) {
            return type;
        }

        for (String typeName : PRIMITIVE_NULLABLE.keySet()) {
            if (type.getTypeName().equals(typeName)) {
                return PRIMITIVE_NULLABLE.get(typeName);
            }
        }

        return type;
    }

    public static Object stringToPrimitiveType(String value, Class<?> dataType) throws ClassFactoryException {

        if (value == null) {
            return null;
        }

        if (dataType.equals(String.class)) {
            return value;
        }

        try {

            // primitive types need to be cast differently
            if (dataType.isAssignableFrom(boolean.class) || dataType.isAssignableFrom(Boolean.class)) {
                return Boolean.valueOf(value);
            }

            if (dataType.isAssignableFrom(byte.class) || dataType.isAssignableFrom(Byte.class)) {
                return Byte.valueOf(value);
            }

            if (dataType.isAssignableFrom(char.class) || dataType.isAssignableFrom(Character.class)) {

                Assert.isTrue(value.length() != 0, "Expected Character but got: null");
                return value.charAt(0);
            }

            if (dataType.isAssignableFrom(short.class) || dataType.isAssignableFrom(Short.class)) {
                return Short.valueOf(value);
            }

            if (dataType.isAssignableFrom(int.class) || dataType.isAssignableFrom(Integer.class)) {
                return Integer.valueOf(value);
            }

            if (dataType.isAssignableFrom(long.class) || dataType.isAssignableFrom(Long.class)) {
                return Long.valueOf(value);
            }

            if (dataType.isAssignableFrom(float.class) || dataType.isAssignableFrom(Float.class)) {
                return Float.valueOf(value);
            }

            if (dataType.isAssignableFrom(double.class) || dataType.isAssignableFrom(Double.class)) {
                return Double.valueOf(value);
            }

            if (dataType.isEnum()) {
                Object[] constants = dataType.getEnumConstants();
                for (Object constant : constants) {
                    if (StringUtils.equals(value, constant.toString(), true)) {
                        return constant;
                    }
                }
            }
        } catch (Exception e) {
            String error = "Failed to convert value: '" + value + "', to primitive type: " + dataType.getName();
            log.error(error);
            throw new ClassFactoryException(error, e);
        }

        log.info(dataType.getName() + " not primitive!, Value: '" + value + "', was not converted to: " + dataType.getName());
        return null;
    }

    /**
     * checks if @SuppressCheck annotation is given
     *
     * @param clazz to inspect
     * @return true if compatible, false otherwise
     */
    public static boolean checkCompatibility(Class<?> clazz) {

        return clazz != null && clazz.getAnnotation(SuppressCheck.class) == null;
    }

    /**
     * Get methods in order desired to invoke them one by one until match or fail
     *
     * @param type  desired
     * @param names of methods in order
     * @param <T>   class to search for methods
     * @return method list to use
     */
    public static <T> List<Method> getMethods(Class<T> type, String... names) {

        Assert.notNull(type, "Missing class to search for methods!");
        Assert.notNullOrEmpty(names, "Missing method names to search for!");

        Map<String, Method> candidates = new HashMap<>();
        for (Method method : type.getMethods()) {

            if (Modifier.isStatic(method.getModifiers()) &&
                    method.getReturnType().equals(type) &&
                    method.getParameterTypes().length == 1 &&
                    method.getParameterTypes()[0].isAssignableFrom(String.class)) {

                candidates.put(method.getName(), method);
            }
        }

        List<Method> output = new ArrayList<>();

        // go in order desired
        for (String name : names) {
            if (candidates.containsKey(name)) {
                output.add(candidates.get(name));
            }
        }

        return output;
    }
}
