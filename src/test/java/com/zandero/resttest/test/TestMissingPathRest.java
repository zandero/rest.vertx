package com.zandero.resttest.test;

import jakarta.ws.rs.GET;

/**
 * At least one path ...
 */
public class TestMissingPathRest {

    @GET
    public String echo() {

        return "not possible";
    }
}
