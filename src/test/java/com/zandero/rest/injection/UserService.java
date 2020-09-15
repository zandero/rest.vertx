package com.zandero.rest.injection;

import com.zandero.rest.test.data.SimulatedUser;

/**
 *
 */
public interface UserService {

    SimulatedUser getUser(String token);
}
