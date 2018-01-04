package com.zandero.rest.injection;

import com.zandero.rest.annotation.RequestReader;
import com.zandero.rest.annotation.ResponseWriter;
import com.zandero.rest.test.json.Dummy;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.*;

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
	@ResponseWriter(GuicedResponseWriter.class)
	public String get(@PathParam("name") String name) {

		return settings.get().get(name);
	}

	@POST
	@Path("guice/json")
	@Consumes("application/json; charset=utf-8")
	@Produces("application/json; charset=utf-8")
	@RequestReader(GuicedRequestReader.class)
	public Dummy echoJsonPost(Dummy postParam) {

		postParam.name = "Received-" + postParam.name;
		postParam.value = "Received-" + postParam.value;

		return postParam;
	}
}
