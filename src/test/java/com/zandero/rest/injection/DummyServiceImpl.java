package com.zandero.rest.injection;

/**
 *
 */
public class DummyServiceImpl implements DummyService {

	@Override
	public String get() {
		return "I'm so dummy!";
	}
}
