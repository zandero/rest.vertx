package com.zandero.resttest.injection;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;

/**
 *
 */
@Path("injected")
public class InjectedRest {

    private final DummyService service;

    private final OtherService other;

    @Inject
    public InjectedRest(DummyService dummyService, OtherService otherService) {

        service = dummyService;
        other = otherService;
    }

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
