package com.zandero.rest.cache;

import com.zandero.rest.data.MediaTypeHelper;
import com.zandero.utils.Assert;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.*;

import static com.zandero.rest.data.ClassUtils.*;

/**
 *
 */
public abstract class ClassCache<T> {

    /**
     * Cache of class instances
     */
    protected final Map<String, T> instanceCache = new HashMap<>();

    /**
     * map of class associated with class type (to be instantiated)
     */
    protected Map<Class<?>, Class<? extends T>> typeCache = new LinkedHashMap<>();

    /**
     * map of media type associated with class type (to be instantiated)
     */
    protected Map<String, Class<? extends T>> mediaTypeCache = new LinkedHashMap<>();

    public void setDefaults() {

        // clears caches
        instanceCache.clear();
        typeCache.clear();
        mediaTypeCache.clear();
    }

    public T get(String name) {
        return instanceCache.get(name);
    }

    public T get(Class<? extends T> clazz) {
        return instanceCache.get(clazz.getName());
    }

    public Class<? extends T> getFromType(Class<?> type) {
        if (type == null) {
            return null;
        }
        // try to find appropriate class if mapped (by type)
        for (Class<?> key : typeCache.keySet()) {
            if (key.isInstance(type) || key.isAssignableFrom(type)) {
                return typeCache.get(key);
            }
        }

        return null;
    }

    public Class<? extends T> getFromMediaType(MediaType mediaType) {
        if (mediaType == null) {
            return null;
        }

        return mediaTypeCache.get(MediaTypeHelper.getKey(mediaType));
    }

    public void put(T clazz) {

        Assert.notNull(clazz, "Missing class instance!");
        instanceCache.put(clazz.getClass().getName(), clazz);
    }

    protected void register(String mediaType, T clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class instance!");

        MediaType type = MediaTypeHelper.valueOf(mediaType);
        Assert.notNull(type, "Unknown media type given: " + mediaType + "!");

        String key = MediaTypeHelper.getKey(type);
        instanceCache.put(key, clazz);
    }

    protected void register(MediaType mediaType, T clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class instance!");

        String key = MediaTypeHelper.getKey(mediaType);
        instanceCache.put(key, clazz);
    }

    protected void register(String mediaType, Class<? extends T> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class");

        MediaType type = MediaTypeHelper.valueOf(mediaType);
        Assert.notNull(type, "Unknown media type given: " + mediaType + "!");

        String key = MediaTypeHelper.getKey(type);
        mediaTypeCache.put(key, clazz);
    }

    protected void register(MediaType mediaType, Class<? extends T> clazz) {

        Assert.notNull(mediaType, "Missing media type!");
        Assert.notNull(clazz, "Missing media type class!");

        String key = MediaTypeHelper.getKey(mediaType);
        mediaTypeCache.put(key, clazz);
    }

    protected void register(Class<?> aClass, Class<? extends T> clazz) {

        Assert.notNull(aClass, "Missing associated class!");
        Assert.notNull(clazz, "Missing response type class!");

        if (checkCompatibility(clazz)) {
            Type expected = getGenericType(clazz);
            checkIfCompatibleType(aClass, expected, "Incompatible types: '" + aClass + "' and: '" + expected + "' using: '" + clazz + "'!");
        }

        typeCache.put(aClass, clazz);
    }

    protected void register(Class<?> aClass, T instance) {

        Assert.notNull(aClass, "Missing associated class!");
        Assert.notNull(instance, "Missing instance of class!");

        if (checkCompatibility(instance.getClass())) {
            Type expected = getGenericType(instance.getClass());
            checkIfCompatibleType(aClass,
                                  expected,
                                  "Incompatible types: '" + aClass + "' and: '" + expected + "' using: '" + instance.getClass() + "'!");
        }

        instanceCache.put(aClass.getName(), instance);
    }
}
