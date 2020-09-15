package com.zandero.rest.test;

import javax.ws.rs.GET;

/**
 * At least one path ...
 */
public class TestMissingPathRest {

    @GET
    public String echo() {

        return "not possible";
    }
}
