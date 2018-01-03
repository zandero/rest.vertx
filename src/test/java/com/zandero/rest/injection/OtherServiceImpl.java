package com.zandero.rest.injection;

import javax.inject.Inject;

/**
 *
 */
public class OtherServiceImpl implements OtherService {

	@Inject
	DummyService dummyService;

	@Override
	public String other() {
		return "Oh yes " + dummyService.get();
	}
}
