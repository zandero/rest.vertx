package com.zandero.rest.bean;

import com.zandero.rest.injection.InjectionProvider;
import io.vertx.ext.web.RoutingContext;

public interface BeanProvider {

    /**
     * @param clazz             to be provided
     * @param context           current request context
     * @param injectionProvider injection provider
     * @param <T>               class type
     * @return object initialized from request / context
     * @throws Throwable exception in case context can't be provided
     */
    <T> T provide(Class<T> clazz, RoutingContext context, InjectionProvider injectionProvider) throws Throwable;
}
