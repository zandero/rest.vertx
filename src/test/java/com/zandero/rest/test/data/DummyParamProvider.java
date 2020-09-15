package com.zandero.rest.test.data;

import com.zandero.rest.test.json.Dummy;

import javax.ws.rs.ext.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * see: https://github.com/javaee-samples/javaee7-samples/tree/master/jaxrs/paramconverter/src/main/java/org/javaee7/jaxrs/paramconverter
 */
@Deprecated
@Provider
public class DummyParamProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {

        if (rawType.getName().equals(Dummy.class.getName())) {
            return new ParamConverter<T>() {

                @Override
                public T fromString(String value) {
                    Dummy dummy = new Dummy(value);
                    return rawType.cast(dummy);
                }

                @Override
                public String toString(T myDummy) {
                    if (myDummy == null) {
                        return null;
                    }
                    return myDummy.toString();
                }
            };
        }
        return null;
    }
}
