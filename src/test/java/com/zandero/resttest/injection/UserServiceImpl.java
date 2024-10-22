package com.zandero.resttest.injection;

import com.zandero.resttest.test.data.SimulatedUser;

/**
 *
 */
public class UserServiceImpl implements UserService {

    @Override
    public SimulatedUser getUser(String token) {
        return new SimulatedUser(token);
    }
}
