package com.zandero.rest.test.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dummy {

	public Dummy() {
	}

	public Dummy(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String name;

	public String value;
}
