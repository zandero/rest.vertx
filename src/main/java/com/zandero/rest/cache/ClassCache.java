package com.zandero.rest.cache;

import com.zandero.utils.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

import static com.zandero.rest.provisioning.ClassUtils.*;

/**
 * Base class to cache class instances by name, type, media type ...
 */
public abstract class ClassCache<T> {

    private final static Logger log = LoggerFactory.getLogger(ClassCache.class);

    /**
     * Cache of class instances
     * Map of class(A)->name / class(A)->instance
     */
    protected final Map<String, T> instanceCache = new HashMap<>();

    /**
     * map of class associated with class type (to be instantiated)
     * once instantiated it is put into instanceCache
     * Map of class(B)->name -> class(A)->name
     */
    protected Map<Class<?>, Class<? extends T>> associatedTypeMap = new LinkedHashMap<>();

    public void clear() {
        instanceCache.clear();
        associatedTypeMap.clear();
    }

    /**
     * Find instance by type name / class name
     *
     * @param clazz to find cache instance for
     * @return found instance or null if not found
     */
    public T getInstanceByType(Class<? extends T> clazz) {
        Assert.notNull(clazz, "Missing class!");
        return instanceCache.get(clazz.getName());
    }

    /**
     * Tries to find registered class by associated type
     * @param type associated
     * @return found class instance or null if not found
     */
    public T getInstanceByAssociatedType(Class<?> type) {
        Class<? extends T> found = getAssociatedType(type);
        if (found != null) { // is registered ... try finding instance
            return getInstanceByType(found);
        }

        return null;
    }

    /**
     * Returns found type from typeCache if it was registered
     *
     * @param type to search for
     * @return found type or null if none registered
     */
    public Class<? extends T> getAssociatedType(Class<?> type) {
        if (type == null) {
            return null;
        }
        // try to find appropriate class if mapped (by type)
        for (Class<?> key : associatedTypeMap.keySet()) {
            if (key.isInstance(type) || key.isAssignableFrom(type)) {
                return associatedTypeMap.get(key);
            }
        }

        return null;
    }

    /**
     * Registers an instance of a class for this class (key = class itself)
     *
     * @param clazz instance
     */
    public void registerInstance(T clazz) {
        Assert.notNull(clazz, "Missing class instance!");

        // also register type
        instanceCache.put(clazz.getClass().getName(), clazz);
    }


    /**
     * Registers type associated with a type
     *
     * @param aClass key
     * @param clazz  associated type
     */
    public void registerAssociatedType(Class<?> aClass, Class<? extends T> clazz) {

        Assert.notNull(aClass, "Missing associated class!");
        Assert.notNull(clazz, "Missing response type class!");

        if (checkCompatibility(clazz)) {
            Type expected = getGenericType(clazz);
            checkIfCompatibleType(aClass, expected, "Incompatible types: '" + aClass + "' and: '" + expected + "' using: '" + clazz + "'!");
        }

        log.trace("Registering type: " + clazz.getName() + ", for: " + aClass.getName());
        associatedTypeMap.put(aClass, clazz);
    }

    /**
     * Registers type with instance
     *
     * @param aClass   key
     * @param instance of associated type
     */
    public void registerInstanceByAssociatedType(Class<?> aClass, T instance) {

        Assert.notNull(aClass, "Missing associated class!");
        Assert.notNull(instance, "Missing instance of class!");

       /*
       TODO: revisit this part ... if check is even feasible
       if (checkCompatibility(instance.getClass())) {
            Type expected = getGenericType(instance.getClass());
            if (expected != null) {
                checkIfCompatibleType(aClass,
                                      expected,
                                      "Incompatible types: '" + aClass + "' and: '" + expected + "' using: '" + instance.getClass() + "'!");
            }
        }*/

        // also register type
        log.trace("Storing instance: " + instance.getClass().getName() + " into cache, for: " + aClass.getName());
        associatedTypeMap.put(aClass, (Class<? extends T>) instance.getClass());
        instanceCache.put(instance.getClass().getName(), instance);
    }
}
