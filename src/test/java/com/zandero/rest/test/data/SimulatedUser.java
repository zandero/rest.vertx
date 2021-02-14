package com.zandero.rest.test.data;

import io.vertx.ext.auth.impl.UserImpl;

/**
 * Simplistic user where name is his role ...
 */
public class SimulatedUser extends UserImpl {

    private final String role;

    public SimulatedUser(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}


/*public class SimulatedUser implements User {

    private final String role; // role and role in one

    public SimulatedUser(String name) {
        role = name;
    }

    public String getRole() {
        return role;
    }

 *//*   @Override
    protected void doIsPermitted(String permission, Handler<AsyncResult<Boolean>> resultHandler) {

        resultHandler.handle(Future.succeededFuture(role != null && role.equals(permission)));
    }*//*

    @Override
    public JsonObject attributes() {
        return null;
    }

    @Override
    public boolean expired() {
        return false;
    }

    @Override
    public boolean expired(int leeway) {
        return false;
    }

    @Override
    public <T> T get(String key) {
        return null;
    }

    @Override
    public boolean containsKey(String key) {
        return getRole() == key;
    }

    @Override
    public Authorizations authorizations() {
        return null;
    }

    @Override
    public User isAuthorized(Authorization authority, Handler<AsyncResult<Boolean>> resultHandler) {
        return null;
    }

    @Override
    public User isAuthorized(String authority, Handler<AsyncResult<Boolean>> resultHandler) {
        return null;
    }

    @Override
    public Future<Boolean> isAuthorized(Authorization authority) {
        return null;
    }

    @Override
    public Future<Boolean> isAuthorized(String authority) {
        return null;
    }

    @Override
    public User clearCache() {
        return null;
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
}*/
