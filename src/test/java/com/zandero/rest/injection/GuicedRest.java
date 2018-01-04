package com.zandero.rest.injection;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 */
@Singleton
public class GuicedRest {

	private final Provider<Settings> settings;

	@Inject
	public GuicedRest(Provider<Settings> someSettings) {

		settings = someSettings;
	}

	@GET
	@Path("guice/{name}")
	public String get(@PathParam("name") String name) {

		return settings.get().get(name);
	}
}
