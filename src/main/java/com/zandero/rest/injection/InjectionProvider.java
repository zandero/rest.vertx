package com.zandero.rest.injection;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Common class to implement in order to provide injection for RESTs
 */
public interface InjectionProvider {

	Object getInstance(Class clazz);

	static boolean hasInjection(Class<?> clazz) {

		// checks if any constructors are annotated with @Inject annotation
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();

		for (Constructor<?> constructor : constructors) {
			Annotation found = constructor.getAnnotation(Inject.class);
			if (found != null) {
				return true;
			}
		}
		
		// check if any class members are injected
		Field[] fields = clazz.getDeclaredFields();
		for (Field field: fields) {
			Annotation found = field.getAnnotation(Inject.class);
			if (found != null) {
				return true;
			}
		}

		// check methods if they need injection
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method: methods) {
			Annotation found = method.getAnnotation(Inject.class);
			if (found != null) {
				return true;
			}
		}

		return false;
	}
}
