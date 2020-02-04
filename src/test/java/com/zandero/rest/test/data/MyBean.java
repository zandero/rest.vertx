package com.zandero.rest.test.data;

import com.zandero.rest.annotation.Raw;

import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class MyBean {

    @HeaderParam("MyHeader")
    private boolean header;

    @PathParam("path")
    private String path;

    @QueryParam("query")
    @Raw
    private int query;

    @CookieParam("chocolate")
    private String cookie;

    @Override
    public String toString() {
        return "Header: " + header +
                ", Path: " + path +
                ", Query: " + query +
                ", Cookie: " + cookie;
    }
}
