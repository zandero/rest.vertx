package com.zandero.rest.test;

import com.zandero.rest.test.data.MyBean;

import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 *
 */
@Path("/read")
public class TestBeanReaderRest {

    @POST
    @Path("/bean")
    public String getWords(@BeanParam MyBean bean) {
        return bean.toString();
    }
}
