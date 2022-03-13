package com.zandero.rest.cache;

import com.zandero.rest.context.ContextProvider;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Storage of context providers
 */
public class ContextProviderCache extends ClassCache<ContextProvider> {

    /**
     * Cache of classes that need or don't need context injection
     * If class needs context injection ... a list of Fields to inject is provided
     * If class doesn't need context injection the list of fields is empty (not null)
     * <p>
     * HashMap contains pairs by full class name -> list of fields
     */
    private final HashMap<String, List<Field>> contextCache = new HashMap<>();

    public void register(Class<?> aClass, Class<? extends ContextProvider> clazz) {
        super.registerAssociatedType(aClass, clazz);
    }

    public void register(Class<?> aClass, ContextProvider instance) {
        super.registerInstanceByAssociatedType(aClass, instance);
    }

    public List<Field> getContextFields(Class<?> clazz) {

        List<Field> contextFields = contextCache.get(clazz.getName());
        if (contextFields == null) {
            contextFields = ContextProvider.getContextFields(clazz);
            contextCache.put(clazz.getName(), contextFields);
        }

        return contextFields;
    }

    public <T> boolean hasContext(Class<? extends T> clazz) {
        return getContextFields(clazz).size() > 0;
    }
}
