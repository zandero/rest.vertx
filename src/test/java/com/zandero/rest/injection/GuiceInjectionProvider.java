package com.zandero.rest.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 *
 */
public class GuiceInjectionProvider extends AbstractModule implements InjectionProvider  {

	private Injector injector;

	private Settings settings;

	public GuiceInjectionProvider() {

		injector = Guice.createInjector(this);
	}

	@Override
	protected void configure() {
		bind(DummyService.class).to(DummyServiceImpl.class);
		bind(OtherService.class).to(OtherServiceImpl.class);
		bind(UserService.class).to(UserServiceImpl.class);
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
	public Object getInstance(Class clazz) {

		return injector.getInstance(clazz);
	}
}
