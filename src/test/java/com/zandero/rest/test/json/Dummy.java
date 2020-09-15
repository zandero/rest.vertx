package com.zandero.rest.test.json;

import com.fasterxml.jackson.annotation.*;
import com.zandero.utils.extra.JsonUtils;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dummy {

    public Dummy() {
    }

    public Dummy(String json) {
        // string only constructor
        Dummy out = JsonUtils.fromJson(json, Dummy.class);
        name = out.name;
        value = out.value;
    }

    public Dummy(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String name;

    public String value;

    public static Dummy valueOf(String json) {
        return JsonUtils.fromJson(json, Dummy.class);
    }
}
