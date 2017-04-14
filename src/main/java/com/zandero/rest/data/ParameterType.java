package com.zandero.rest.data;

/**
 *
 */
public enum ParameterType {

	/**
	 * REST path parameter
	 */
	path,

	/**
	 * Rest query parameter
	 */
	query,

	/**
	 * Form parameter
	 */
	form,

	/**
	 * Request header
	 */
	header,

	/**
	 * Request body
	 */
	body,

	/**
	 * Any Vert.x available context ...
	 */
	context
}
