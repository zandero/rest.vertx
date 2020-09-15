package com.zandero.rest.injection;

import com.zandero.rest.reader.ValueReader;
import com.zandero.rest.test.json.Dummy;

import javax.inject.Inject;

/**
 *
 */
public class GuicedRequestReader implements ValueReader<Dummy> {

    @Inject
    OtherService other;

    @Override
    public Dummy read(String value, Class<Dummy> type) {
        return new Dummy(other.other(), value);
    }
}
