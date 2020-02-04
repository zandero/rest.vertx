package com.zandero.rest.data;

/**
 *
 */
public enum ParameterType {

	/**
	 * Placeholder until we know ... otherwise error is thrown
	 */
	unknown(""),

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
	matrix("@MatrixParam"),

	/**
	 * Bean parameter (aggregator)
	 */
	bean("@BeanParam"),

	/**
	 * Request body
	 */
	body("body"),

	/**
	 * Any Vert.x available context ...
	 */
	context("@Context");

	public final String description;

	ParameterType(String value) {
		description = value;
	}
}
