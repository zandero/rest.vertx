package com.zandero.rest.injection;

import javax.inject.Inject;

/**
 *
 */
public class OtherServiceImpl implements OtherService {

    protected OtherServiceImpl() {

    }

    public OtherServiceImpl(DummyService service) {
        dummyService = service;
    }

    @Inject
    DummyService dummyService;

    @Override
    public String other() {
        return "Oh yes " + dummyService.get();
    }
}
