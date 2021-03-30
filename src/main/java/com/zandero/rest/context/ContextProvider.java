package com.zandero.rest.context;

import com.zandero.rest.exception.*;
import com.zandero.rest.provisioning.ClassFactory;
import com.zandero.utils.Assert;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.*;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 *
 */
public interface ContextProvider<T> {

    /**
     * @param request current request
     * @return object to be pushed into context storage
     * @throws Throwable exception in case context can't be provided
     */
    T provide(HttpServerRequest request) throws Throwable;


    String CONTEXT_DATA_KEY_PREFIX = "RestRouter-";

    /**
     * Provides vertx context of desired type if possible
     *
     * @param type         context type
     * @param defaultValue default value if given
     * @param context      to provider / extract values from
     * @return found context or null if not found
     * @throws ContextException in case context could not be provided
     */
    static Object provideContext(Class<?> type,
                                 String defaultValue,
                                 RoutingContext context) throws ContextException {

        Assert.notNull(type, "Missing class type!");
        Assert.notNull(context, "Missing context!");

        // vert.x context
        if (type.isAssignableFrom(HttpServerResponse.class)) {
            return context.response();
        }

        if (type.isAssignableFrom(HttpServerRequest.class)) {
            return context.request();
        }

        if (type.isAssignableFrom(RoutingContext.class)) {
            return context;
        }

        // provide vertx via @Context
        if (type.isAssignableFrom(Vertx.class)) {
            return context.vertx();
        }

        // provide event bus via @Context
        if (type.isAssignableFrom(EventBus.class)) {
            return context.vertx().eventBus();
        }

        if (type.isAssignableFrom(User.class)) {
            return context.user();
        }

        // internal context / reflection of route definition
/*        if (type.isAssignableFrom(RouteDefinition.class)) {
            return definition;
        }*/

        // browse through context storage
        if (context.data() != null && context.data().size() > 0) {

            Object item = context.data().get(getContextDataKey(type));
            if (item != null) { // found in storage ... return
                return item;
            }
        }

        if (defaultValue != null) {
            // check if type has constructor that can be used with defaultValue ...
            // and create Context type on the fly constructed with defaultValue
            try {
                return ClassFactory.constructType(type, defaultValue);
            } catch (ClassFactoryException e) {
                throw new ContextException("Can't provide @Context of type: " + type + ". " + e.getMessage());
            }
        }

        throw new ContextException("Can't provide @Context of type: " + type);
    }

    static List<Field> checkForContext(Class<?> clazz) {

        // check if any class members are injected
        Field[] fields = clazz.getDeclaredFields();
        List<Field> contextFields = new ArrayList<>();
        for (Field field : fields) {
            Annotation found = field.getAnnotation(Context.class);
            if (found != null) {
                contextFields.add(field);
            }
        }

        return contextFields;
    }

	/*public static <T> boolean hasContext(Class<? extends T> clazz) {
		return getContextFields(clazz).size() > 0;
	}

	// TODO: this must not be a static method we need access to providers stored in factory
	static void injectContext(Object instance, RoutingContext routeContext) throws ContextException {

        if (instance == null) {
            return;
        }

        List<Field> contextFields = getContextFields(instance.getClass());

        for (Field field : contextFields) {
            Annotation found = field.getAnnotation(Context.class);
            if (found != null) {

                Object context = provideContext(field.getType(), null, routeContext);
                try {
                    field.setAccessible(true);
                    field.set(instance, context);
                } catch (IllegalAccessException e) {
                    throw new ContextException("Can't provide @Context for: " + field.getType() + " - " + e.getMessage());
                }
            }
        }
    }*/

    static String getContextDataKey(Object object) {

        Assert.notNull(object, "Expected object but got null!");
        if (object instanceof Class) {
            return CONTEXT_DATA_KEY_PREFIX + ((Class) object).getName();
        }

        return CONTEXT_DATA_KEY_PREFIX + object.getClass().getName();
    }
}
