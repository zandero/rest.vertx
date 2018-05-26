package com.zandero.rest.test.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zandero.utils.extra.JsonUtils;

import javax.validation.constraints.NotNull;

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

	public static ValidDummy valueOf(String json) {
		return JsonUtils.fromJson(json, ValidDummy.class);
	}
}
