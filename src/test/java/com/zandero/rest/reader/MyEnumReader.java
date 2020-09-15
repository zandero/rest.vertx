package com.zandero.rest.reader;

import com.zandero.rest.test.data.MyEnum;

public class MyEnumReader implements ValueReader<MyEnum> {

    @Override
    public MyEnum read(String value, Class<MyEnum> type) throws Throwable {

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
}
