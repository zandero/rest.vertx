package com.zandero.resttest.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.zandero.rest.reader.ValueReader;
import com.zandero.resttest.test.data.MyEnum;
import io.vertx.ext.web.RoutingContext;

public class MyEnumReader implements ValueReader<MyEnum> {

    @Override
    public MyEnum read(String value, Class<MyEnum> type, RoutingContext context) throws Throwable {
        switch (value.trim()) {
            case "1":
                return MyEnum.one;
            case "2":
                return MyEnum.two;
            case "3":
                return MyEnum.three;
            default:
                return null;
        }
    }

    @Override
    public MyEnum read(String value, TypeReference<MyEnum> type, RoutingContext context) throws Throwable {
        return null;
    }

    @Override
    public MyEnum read(String value, JavaType jt, RoutingContext context) {
        return null;
    }
}
