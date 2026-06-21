package com.zandero.rest.test;

import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.UserContextInternal;

/**
 * Vert.x 5 removed {@link RoutingContext#setUser(User)}; tests set the user via {@link UserContextInternal}.
 */
public final class RoutingContextTestUtils {

    private RoutingContextTestUtils() {
    }

    public static void setUser(RoutingContext context, User user) {
        ((UserContextInternal) context.userContext()).setUser(user);
    }
}
