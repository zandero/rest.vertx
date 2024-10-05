package com.zandero.resttest.test.data;

import com.zandero.resttest.test.json.Dummy;
import com.zandero.rest.utils.extra.JsonUtils;

import jakarta.ws.rs.ext.ParamConverter;

/**
 *
 */
public class DummyParamConverter implements ParamConverter<Dummy> {

    @Override
    public Dummy fromString(String value) {

        return JsonUtils.convert(value, Dummy.class);
    }

    @Override
    public String toString(Dummy value) {
        return value.toString();
    }
}
