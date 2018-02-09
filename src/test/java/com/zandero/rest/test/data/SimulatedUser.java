package com.zandero.rest.test.data;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;

/**
 * Simplistic user where name is his role ...
 */
public class SimulatedUser extends AbstractUser {

	private final String role; // role and role in one

	public SimulatedUser(String name) {

		role = name;
	}

	public String getRole() {
		return role;
	}

	@Override
	protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {

		resultHandler.handle(Future.succeededFuture(role != null && role.equals(permission)));
	}

	@Override
	public JsonObject principal() {

		JsonObject json = new JsonObject();
		json.put("role", role);
		return json;
	}

	@Override
	public void setAuthProvider(AuthProvider authProvider) {

	}

	public static SimulatedUser fromString(String value) {
		return new SimulatedUser(value);
	}
}
