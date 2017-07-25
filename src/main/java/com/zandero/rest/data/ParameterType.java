package com.zandero.rest.data;

/**
 *
 */
public enum ParameterType {

	/**
	 * REST path parameter
	 */
	path("@PathParam"),

	/**
	 * Rest query parameter
	 */
	query("@QueryParam"),

	/**
	 * Cookie in request
	 */
	cookie("@CookieParam"),

	/**
	 * Form parameter
	 */
	form("@FormParam"),

	/**
	 * Request header
	 */
	header("@HeaderParam"),

	/**
	 * Matrix parameter
	 */
	matrix("@Matrix"),

	/**
	 * Request body
	 */
	body("body"),

	/**
	 * Any Vert.x available context ...
	 */
	context("@Context");

	private final String description;

	ParameterType(String value) {

		description = value;
	}

	public String getDescription() {

		return description;
	}
}
