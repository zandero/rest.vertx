package com.zandero.rest.injection;

import javax.inject.Inject;
import javax.ws.rs.*;

/**
 *
 */
@Path("getInstance")
public class InjectedServicesRest {

    @Inject
    private DummyService service;

    @Inject
    private OtherService other;

    @GET
    @Path("dummy")
    public String get() {
        return service.get();
    }

    @GET
    @Path("other")
    public String getOther() {
        return other.other();
    }
}
