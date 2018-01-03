package com.zandero.rest.injection;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 */
@Path("inject")
public class InjectedServicesRest {

	@Inject
	private DummyService service;

	@Inject
	private OtherService other;

	@GET
	@Path("dummy")
	public String get() {
		return service.get();
	}

	@GET
	@Path("other")
	public String getOther() {
		return other.other();
	}
}
