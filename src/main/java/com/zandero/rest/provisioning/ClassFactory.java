package com.zandero.rest.provisioning;

import com.zandero.rest.annotation.SuppressCheck;
import com.zandero.rest.bean.BeanDefinition;
import com.zandero.rest.cache.ContextProviderCache;
import com.zandero.rest.data.*;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.utils.*;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

import static com.zandero.rest.provisioning.ClassUtils.*;

/**
 * A class factory utility
 * Takes care of object instance creation from class types
 */
public class ClassFactory {

    private final static Logger log = LoggerFactory.getLogger(ClassFactory.class);

    private static final Set<String> INJECTION_ANNOTATIONS = ArrayUtils.toSet("Inject", "Injection", "InjectionProvider");

    public static Object newInstanceOf(Class<?> clazz, RoutingContext context) throws ClassFactoryException {

        if (clazz == null) {
            return null;
        }

        try {
            for (Constructor<?> c : clazz.getDeclaredConstructors()) {
                boolean isAccessible = c.isAccessible();

                if (!isAccessible) {
                    c.setAccessible(true);
                }

                Object instance;
                if (c.getParameterCount() == 0) {
                    // initialize with empty constructor
                    instance = c.newInstance();
                } else {
                    // try to call constructor and provide parameters via context
                    instance = constructWithContext(c, context);
                }

                if (!isAccessible) {
                    c.setAccessible(false);
                }

                if (instance != null) { // managed to create new object instance ...
                    return instance;
                }
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            log.error("Failed to instantiate class '" + clazz.getName() + "' " + e.getMessage(), e);
            throw new ClassFactoryException("Failed to instantiate class of type: " + clazz.getName() + ", class needs empty constructor!", e);
        }

        throw new ClassFactoryException("Failed to instantiate class of type: " + clazz.getName() + ", class needs empty constructor!", null);
    }

    public static Object newInstanceOf(Class<?> clazz) throws ClassFactoryException {
        return newInstanceOf(clazz, null);
    }

    public static Object newInstanceOf(Class<?> clazz,
                                       InjectionProvider provider,
                                       ContextProviderCache contextProviderCache,
                                       RoutingContext context) throws ClassFactoryException, ContextException {

        if (clazz == null) {
            return null;
        }

        boolean canBeInjected = InjectionProvider.canBeInjected(clazz);
        boolean hasInjection = InjectionProvider.hasInjection(clazz);

        Object instance;
        if (provider != null && (hasInjection || canBeInjected)) {

            try {
                instance = provider.getInstance(clazz);
                if (instance == null) {
                    throw new ClassFactoryException("Failed to getInstance class of type: " + clazz.getName() + ", with injector: " +
                                                        provider.getClass().getName() + "!", null);
                }
            } catch (Throwable e) {
                throw new ClassFactoryException("Failed to getInstance class of type: " + clazz.getName() + ", with injector: " +
                                                    provider.getClass().getName() + "!", e);
            }
        } else {

            SuppressCheck suppress = clazz.getAnnotation(SuppressCheck.class);
            if (hasInjection && (suppress == null || !INJECTION_ANNOTATIONS.contains(suppress.value()))) {
                log.warn(clazz.getName() + " uses @Inject but no InjectionProvider registered!");
            }

            instance = newInstanceOf(clazz, context);
        }

        // TODO: remove this or move this method out of ClassFactory ... use some other means to inject context ...
        if (context != null && ContextProviderCache.hasContext(clazz)) {
            contextProviderCache.injectContext(instance, context);
        }

        return instance;
    }

    /**
     * Creates new instance of object using context as provider for constructor parameters
     *
     * @param constructor to be used
     * @param context     to extract parameters from
     * @return instance or null;
     */
    private static Object constructWithContext(Constructor<?> constructor, RoutingContext context) throws ClassFactoryException {
        // Try to initialize class from context if arguments fit
        if (context != null) {
            BeanDefinition definition = new BeanDefinition(constructor);
            if (definition.size() == constructor.getParameterCount()) {
                Object[] params = new Object[definition.size()];
                String[] values = new String[params.length];

                try {
                    for (int index = 0; index < params.length; index++) {
                        MethodParameter parameter = definition.get(index);
                        values[index] = ArgumentProvider.getValue(null, parameter, context, parameter.getDefaultValue());
                    }

                    for (int index = 0; index < params.length; index++) {
                        MethodParameter parameter = definition.get(index);
                        params[index] = stringToPrimitiveType(values[index], parameter.getDataType());
                    }

                    // TODO: log params before invoking
                    log.info("Invoking: " + describeConstructor(constructor, values));
                    return constructor.newInstance(params);
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException | ClassFactoryException e) {
                    String error = "Failed to instantiate class, with constructor: " +
                                       describeConstructor(constructor, values) + ". " + e.getMessage();
                    log.error(error, e);

                    throw new ClassFactoryException(error, e);
                }
            }
        }

        return null;
    }

    private static String describeConstructor(Constructor<?> constructor, Object[] params) {

        assert constructor != null;
        StringBuilder builder = new StringBuilder();
        builder.append(constructor.getName())
            .append("(");

        if (constructor.getParameterCount() > 0) {
            for (int i = 0; i < constructor.getParameterCount(); i++) {

                Object paramValue = params.length > i ? params[i] : null;

                Parameter param = constructor.getParameters()[i];
                builder.append(param.getType().getSimpleName())
                    .append(" ")
                    .append(param.getName())
                    .append("=")
                    .append(paramValue);

                if (i + 1 < constructor.getParameterCount()) {
                    builder.append(", ");
                }
            }
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * Aims to construct given type utilizing a constructor that takes String or other primitive type values
     *
     * @param type      to be constructed
     * @param fromValue constructor param
     * @param <T>       type of value
     * @return class object
     * @throws ClassFactoryException in case type could not be constructed
     */
    public static <T> Object constructType(Class<T> type, String fromValue) throws ClassFactoryException {

        Assert.notNull(type, "Missing type!");

        // a primitive or "simple" type
        if (isPrimitiveType(type)) {
            return stringToPrimitiveType(fromValue, type);
        }

        // have a constructor that accepts a single argument (String or any other primitive type that can be converted from String)
        Pair<Boolean, T> result = constructViaConstructor(type, fromValue);
        if (result.getKey()) {
            return result.getValue();
        }

        result = constructViaMethod(type, fromValue);
        if (result.getKey()) {
            return result.getValue();
        }

        //
        // have a registered implementation of ParamConverterProvider JAX-RS extension SPI that returns a ParamConverter instance capable of a "from string" conversion for the type.
        //

        // Be List, Set or SortedSet, where T satisfies 2, 3 or 4 above. The resulting collection is read-only.
		/*if (type.isAssignableFrom(List.class) ||
			type.isAssignableFrom(Set.class) ||
			type.isAssignableFrom(SortedSet.class))*/


        throw new ClassFactoryException("Could not construct: " + type + " with default value: '" + fromValue + "', " +
                                            "must provide String only or primitive type constructor, " +
                                            "static fromString() or valueOf() methods!", null);
    }

    /**
     * have a constructor that accepts a single argument
     * (String or any other primitive type that can be converted from String)
     * Pair(success, object)
     */
    static <T> Pair<Boolean, T> constructViaConstructor(Class<T> type, String fromValue) {

        Constructor<?>[] allConstructors = type.getDeclaredConstructors();
        for (Constructor<?> ctor : allConstructors) {

            Class<?>[] pType = ctor.getParameterTypes();
            if (pType.length == 1) { // ok ... match ... might be possible to use

                try {

                    for (Class<?> primitive : ClassUtils.PRIMITIVE_TYPE) {

                        if (pType[0].isAssignableFrom(primitive)) {

                            Object value = stringToPrimitiveType(fromValue, primitive);
                            return new Pair(true, ctor.newInstance(value));
                        }
                    }
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException | ClassFactoryException e) {
                    //continue; // try next one ... if any
                    log.warn("Failed constructing: " + ctor, e);
                }
            }
        }

        return new Pair<>(false, null);
    }

    /**
     * Constructs type via static method fromString(String value) or valueOf(String value)
     *
     * @param type      to be constructed
     * @param fromValue value to take
     * @param <T>       class type
     * @return Object of type or null if failed to construct
     */
    static <T> Pair<Boolean, T> constructViaMethod(Class<T> type, String fromValue) {

        // Try to use fromString before valueOf (enums have valueOf already defined) - in case we override fromString()
        List<Method> methods = ClassUtils.getMethods(type, "fromString", "valueOf");
        if (methods.size() > 0) {

            for (Method method : methods) {

                try {
                    Object value = method.invoke(null, fromValue);
                    return new Pair(true, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    // failed with this one ... try the others ...
                    log.warn("Failed invoking static method: " + method.getName());
                }
            }
        }

        return new Pair<>(false, null);
    }
}
