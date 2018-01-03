package com.zandero.rest.injection;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;

/**
 * Common class to implement in order to provide injection for RESTs
 */
public interface InjectionProvider {

	Object inject(Class clazz);

	static boolean hasInjection(Class<?> clazz) {

		// checks if any constructors are annotated with @Inject annotation
		Constructor<?>[] constructors = clazz.getDeclaredConstructors();

		for (Constructor<?> constructor : constructors) {
			Annotation found = constructor.getAnnotation(Inject.class);
			if (found != null) {
				return true;
			}
		}

		return false;
	}
}
