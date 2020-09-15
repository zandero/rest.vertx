package com.zandero.rest.test.data;

import com.zandero.rest.test.json.Dummy;
import com.zandero.utils.extra.JsonUtils;

import javax.ws.rs.ext.ParamConverter;

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
