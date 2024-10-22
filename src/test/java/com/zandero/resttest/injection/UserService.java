package com.zandero.resttest.injection;

import com.zandero.resttest.test.data.SimulatedUser;

/**
 *
 */
public interface UserService {

    SimulatedUser getUser(String token);
}
