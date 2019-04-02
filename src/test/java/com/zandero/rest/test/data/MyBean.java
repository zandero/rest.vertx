package com.zandero.rest.test.data;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class MyBean {

    @HeaderParam("header")
    private String header;

    @PathParam("path")
    private String path;

    @QueryParam("query")
    private String query;

    @Override
    public String toString() {
        return "Header: " + header + ", Path: " + path + ", Query: " + query;
    }
}
