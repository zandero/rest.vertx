package com.zandero.rest.injection;

import org.codejargon.feather.Feather;

/**
 *
 */
public class FeatherInjectionProvider implements InjectionProvider {

    private final Feather injector;

    public FeatherInjectionProvider() {
        injector = Feather.with(new FeatherModule());
    }

    @Override
    public Object getInstance(Class clazz) {

        Object instance = injector.instance(clazz);
        injector.injectFields(instance);

        return instance;
    }
}
