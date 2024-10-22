package com.zandero.resttest.injection;

import com.google.inject.*;
import com.zandero.rest.injection.InjectionProvider;

/**
 *
 */
public class GuiceInjectionProvider extends AbstractModule implements InjectionProvider {

    private final Injector injector;

    private Settings settings;

    public GuiceInjectionProvider() {
        injector = Guice.createInjector((com.google.inject.Module) this);
    }

    public GuiceInjectionProvider(com.google.inject.Module[] modules) {
        injector = Guice.createInjector(modules);
    }

    @Override
    protected void configure() {
        bind(DummyService.class).to(DummyServiceImpl.class);
        bind(OtherService.class).to(OtherServiceImpl.class);
        bind(UserService.class).to(UserServiceImpl.class);
        bind(GuiceInjectService.class); // using Guice injection
    }

    @Provides
    public Settings getSettings() {

        // provider all needed settings for running backend
        // simulate provided settings
        if (settings == null || settings.isEmpty()) {
            settings = new Settings();
            settings.put("A", "1");
            settings.put("B", "2");
        }

        return settings;
    }

    @Override
    public <T> T getInstance(Class<T> clazz) {
        return injector.getInstance(clazz);
    }
}
