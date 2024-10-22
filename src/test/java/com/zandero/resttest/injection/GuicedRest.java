package com.zandero.resttest.injection;

import com.zandero.rest.annotation.*;
import com.zandero.resttest.test.json.Dummy;

import jakarta.inject.*;
import jakarta.ws.rs.*;

/**
 *
 */
@Singleton
public class GuicedRest {

    private final Provider<Settings> settings;
    private final GuiceInjectService service;

    @Inject
    public GuicedRest(Provider<Settings> someSettings, GuiceInjectService guicedService) {

        settings = someSettings;
        service = guicedService;
    }

    @GET
    @Path("guice/{name}")
    @ResponseWriter(GuicedResponseWriter.class)
    public String get(@PathParam("name") String name) {

        return settings.get().get(name);
    }

    @GET
    @Path("guiceit")
    public String doubleGuice() {

        return service.getOther();
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
