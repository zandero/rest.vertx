package com.zandero.rest.data;

import com.zandero.rest.annotation.*;
import com.zandero.rest.bean.BeanDefinition;
import com.zandero.rest.cache.*;
import com.zandero.rest.exception.*;
import com.zandero.rest.injection.InjectionProvider;
import com.zandero.utils.*;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.*;
import java.util.*;

import static com.zandero.rest.data.ClassUtils.*;

/**
 * A class factory utility
 */
public class ClassFactory {

    private final static Logger log = LoggerFactory.getLogger(ClassFactory.class);

    private static final Set<String> INJECTION_ANNOTATIONS = ArrayUtils.toSet("Inject", "Injection", "InjectionProvider");

    @SuppressWarnings("unchecked")
    public static Object getClassInstance(Class<?> clazz,
                                          ClassCache classCache,
                                          InjectionProvider provider,
                                          RoutingContext context) throws ClassFactoryException,
                                                                 ContextException {

        if (clazz == null) {
            return null;
        }

        // only use cache if no @Context is needed (TODO: join this two calls into one!)
        boolean hasContext = ContextProviderCache.hasContext(clazz); // TODO: move this method somewhere else
        boolean cacheIt = clazz.getAnnotation(NoCache.class) == null; // caching disabled / enabled

        Object instance = null;
        if (!hasContext && cacheIt) { // no Context ... we can get it from cache
            instance = classCache.getInstanceByType(clazz);
        }

        if (instance == null) {

            instance =  newInstanceOf(clazz, provider, context);

            if (!hasContext && cacheIt) { // no context .. we can cache this instance
                classCache.registerInstance(instance);
            }
        }

        return instance;
    }

    // TODO: improve with additional context provider
    public static Object newInstanceOf(Class<?> clazz,
                                       InjectionProvider provider,
                                       RoutingContext context) throws ClassFactoryException, ContextException {

        if (clazz == null) {
            return null;
        }

        Object instance;

        boolean canBeInjected = InjectionProvider.canBeInjected(clazz);
        boolean hasInjection = InjectionProvider.hasInjection(clazz);

        if (provider == null || (!hasInjection && !canBeInjected)) {

            SuppressCheck suppress = clazz.getAnnotation(SuppressCheck.class);
            if (hasInjection &&
                    (suppress == null || !INJECTION_ANNOTATIONS.contains(suppress.value()))) {
                log.warn(clazz.getName() + " uses @Inject but no InjectionProvider registered!");
            }

            instance = newInstanceOf(clazz, context);
        } else {

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
        }

        // TODO: remove this or move this method out of ClassFactory ... use some other means to inject context ...
        if (ContextProviderCache.hasContext(clazz)) {
            ContextProviderCache.injectContext(instance, context);
        }

        return instance;
    }

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
                // initialize with empty constructor
                Object instance;
                if (c.getParameterCount() == 0) {
                    instance = c.newInstance();
                } else {
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

    /**
     * Creates new instance of object using context as provider for contructor parameters
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

    public static Object newInstanceOf(Class<?> clazz) throws ClassFactoryException {
        return newInstanceOf(clazz, null);
    }

    // TODO : move media type specific into a new class that Reader, Writer factory derives from
    public static Object get(Class<?> type,
                             ClassCache classCache,
                             Class<?> byDefinition,
                             InjectionProvider provider,
                             RoutingContext routeContext,
                             MediaType[] mediaTypes) throws ClassFactoryException,
                                                       ContextException {

        Class<?> clazz = byDefinition;

        // No class defined ... try by type
        if (clazz == null) {
            clazz = classCache.getInstanceFromType(type);
        }

        // try with media type ...
        if (clazz == null && mediaTypes != null && mediaTypes.length > 0) {

            for (MediaType mediaType : mediaTypes) {
                clazz = classCache.getInstanceFromMediaType(mediaType);

                if (clazz != null) {
                    break;
                }
            }
        }

        if (clazz != null) {
            return getClassInstance(clazz, classCache, provider, routeContext);
        }

        // 3. find cached instance ... if any
        return classCache.getInstanceByName(type.getName());
    }

    public static Object get(String mediaType,
                 ClassCache classCache,
                 RoutingContext routeContext) throws ClassFactoryException,
                                                                           ContextException {

        Class<?> clazz = classCache.getInstanceFromMediaType(MediaTypeHelper.valueOf(mediaType));
        return getClassInstance(clazz, classCache, null, routeContext);
    }

    /*public Class<? extends T> get(Class<?> type) {

        if (type == null) {
            return null;
        }
        // try to find appropriate class if mapped (by type)
        for (Class<?> key : classTypes.keySet()) {
            if (key.isInstance(type) || key.isAssignableFrom(type)) {
                return classTypes.get(key);
            }
        }

        return null;
    }*/

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

        // Try to usse fromString before valueOf (enums have valueOf already defined) - in case we override fromString()
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
