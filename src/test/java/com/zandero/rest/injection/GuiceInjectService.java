package com.zandero.rest.injection;

import com.google.inject.Inject;

/**
 * Using Guice Inject annotation
 */
public class GuiceInjectService {
    private final OtherService service;

    @Inject
    public GuiceInjectService(OtherService otherService) {
        service = otherService;
    }

    public String getOther() {
        return service.other();
    }
}
