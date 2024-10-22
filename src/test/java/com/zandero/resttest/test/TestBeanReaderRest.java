package com.zandero.resttest.test;

import com.zandero.resttest.test.data.MyComplexBean;
import com.zandero.resttest.test.data.MySimpleBean;
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
