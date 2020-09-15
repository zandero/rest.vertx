package com.zandero.rest.injection;

import com.zandero.rest.test.data.SimulatedUser;

/**
 *
 */
public class UserServiceImpl implements UserService {

    @Override
    public SimulatedUser getUser(String token) {
        return new SimulatedUser(token);
    }
}
