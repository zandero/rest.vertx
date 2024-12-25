package com.zandero.rest.jakarta.test;

import com.zandero.rest.test.data.*;
import jakarta.ws.rs.*;

/**
 *
 */
@Path("/bean")
public class TestBeanReaderRest {

    @POST
    @Path("/read/{param}")
    public String read(@BeanParam MySimpleBean bean) {
        return bean.toString();
    }

    @GET
    @Path("/write/{param}")
    public String write(@BeanParam MySimpleBean bean) {
        return bean.toString();
    }

    @POST
    @Path("/complex/read/{path}")
    public String complexRead(@BeanParam MyComplexBean bean) {
        return bean.toString();
    }
}
