package com.zandero.rest.injection;

import org.codejargon.feather.Provides;

import javax.inject.Singleton;

/**
 *
 */
public class FeatherModule {

    @Provides
    @Singleton
    public DummyService dummyService() {

        return new DummyServiceImpl();
    }

    @Provides
    @Singleton
    public OtherService otherService() {

        return new OtherServiceImpl(dummyService());
    }
}
