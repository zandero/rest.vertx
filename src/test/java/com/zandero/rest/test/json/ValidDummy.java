package com.zandero.rest.test.json;

import com.fasterxml.jackson.annotation.*;
import com.zandero.utils.extra.JsonUtils;

import javax.validation.constraints.*;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidDummy {

    public ValidDummy() {
    }

    public ValidDummy(@NotNull String json) {
        // string only constructor
        ValidDummy out = JsonUtils.fromJson(json, ValidDummy.class);
        name = out.name;
        value = out.value;
    }

    public ValidDummy(@NotNull String name, @NotNull String value) {
        this.name = name;
        this.value = value;
    }

    @NotNull
    public String name;

    @NotNull
    public String value;

    @Min(10)
    @Max(20)
    public int size;

    public static ValidDummy valueOf(String json) {
        return JsonUtils.fromJson(json, ValidDummy.class);
    }
}
