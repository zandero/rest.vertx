package com.zandero.rest.test;

import com.zandero.rest.annotation.RequestReader;
import com.zandero.rest.reader.CustomWordListReader;
import com.zandero.rest.test.data.MyBean;
import com.zandero.rest.test.json.Dummy;
import com.zandero.rest.test.json.ExtendedDummy;
import com.zandero.utils.StringUtils;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
