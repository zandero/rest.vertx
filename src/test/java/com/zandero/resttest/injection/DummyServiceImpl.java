package com.zandero.resttest.injection;

/**
 * Is not annotated with @Inject ... but can still be injected due to empty constructor
 */
public class DummyServiceImpl implements DummyService {

    @Override
    public String get() {
        return "I'm so dummy!";
    }
}
