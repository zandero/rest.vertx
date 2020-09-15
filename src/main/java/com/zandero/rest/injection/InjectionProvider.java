package com.zandero.rest.injection;

import org.slf4j.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * Common class to implement in order to provide injection for RESTs
 */
public interface InjectionProvider {

    Logger log = LoggerFactory.getLogger(InjectionProvider.class);

    String GUICE_INJECT = "com.google.inject.Inject";
    String JAVA_INJECT = "javax.inject.Inject";

    <T> T getInstance(Class<T> clazz) throws Throwable;

    /**
     * Class can potentially be injected if Injection provider is present
     *
     * @param clazz to be injected
     * @return true if only one constructor present, false otherwise
     */
    static boolean canBeInjected(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        return constructors.length == 1;
    }

    /**
     * Class is annotated with @Inject annotation
     *
     * @param clazz to be injected
     * @return true if injection is specified, false otherwise
     */
    static boolean hasInjection(Class<?> clazz) {

        // checks if any constructors are annotated with @Inject annotation
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        for (Constructor<?> constructor : constructors) {
            Annotation found = findAnnotation(constructor.getAnnotations(), JAVA_INJECT, GUICE_INJECT);
            if (found != null) {
                return true;
            }
        }

        // check if any class members are injected
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Annotation found = findAnnotation(field.getAnnotations(), JAVA_INJECT, GUICE_INJECT);
            if (found != null) {
                return true;
            }
        }

        // check methods if they need injection
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Annotation found = findAnnotation(method.getAnnotations(), JAVA_INJECT, GUICE_INJECT);
            if (found != null) {
                return true;
            }
        }

        return false;
    }

    static Annotation findAnnotation(Annotation[] annotations, String... packages) {

        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                for (String name : packages) {
                    if (annotation.annotationType().getName().equals(name)) {
                        return annotation;
                    }
                }
            }
        }

        return null;
    }
}
