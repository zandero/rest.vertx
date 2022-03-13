package com.zandero.rest.provisioning;

import com.zandero.rest.cache.ContextProviderCache;
import com.zandero.rest.context.ContextProvider;
import com.zandero.rest.exception.ContextException;
import io.vertx.ext.web.RoutingContext;

import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public class ContextInjector {

    private final ContextProviderCache cache;

    public ContextInjector(ContextProviderCache contextProviderCache) {
        this.cache = contextProviderCache;
    }

    public <T> boolean hasContext(Class<? extends T> clazz) {
        return cache.getContextFields(clazz).size() > 0;
    }

    public void injectContext(Object instance, RoutingContext routeContext) throws ContextException {

        if (instance == null) {
            return;
        }

        List<Field> contextFields = cache.getContextFields(instance.getClass());

        for (Field field : contextFields) {
            Annotation found = field.getAnnotation(Context.class);
            if (found != null) {

                Object context = ContextProvider.provide(field.getType(), routeContext);
                try {
                    field.setAccessible(true);
                    field.set(instance, context);
                } catch (IllegalAccessException e) {
                    throw new ContextException("Can't provide @Context for: " + field.getType() + " - " + e.getMessage());
                }
            }
        }
    }
}
