package com.zandero.rest.bean;

import com.zandero.rest.annotation.Header;
import com.zandero.rest.annotation.Raw;
import com.zandero.rest.data.MethodParameter;
import com.zandero.rest.data.ParameterType;
import com.zandero.utils.Assert;
import com.zandero.utils.Pair;

import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static io.vertx.core.cli.impl.ReflectionUtils.isSetter;

public class BeanDefinition {

    Map<String, MethodParameter> parameters = new HashMap<>();

    public BeanDefinition(Class clazz) {

        init(clazz);
    }

    private void init(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getMethods();

        for (Field field : fields) {

            MethodParameter paramValues = getValueFromAnnotations(field.getAnnotations());

            if (paramValues != null) {
                parameters.put(field.getName(), paramValues);
            }
        }

        for (Method method : methods) {
            if (isSetter(method)) {
                MethodParameter paramValues = getValueFromAnnotations(method.getAnnotations());
                if (paramValues != null) {
                    parameters.put(method.getName(), paramValues);
                }
            }
        }
    }

    private MethodParameter getValueFromAnnotations(Annotation[] annotations) {

        MethodParameter parameter = null;

        for (Annotation annotation : annotations) {
            if (annotation instanceof PathParam) {
                parameter = getNewParameter(parameter, ParameterType.path, ((PathParam) annotation).value());
            }

            if (annotation instanceof QueryParam) {
                parameter = getNewParameter(parameter, ParameterType.query, ((QueryParam) annotation).value());
            }

            if (annotation instanceof CookieParam) {
                parameter = getNewParameter(parameter, ParameterType.cookie, ((CookieParam) annotation).value());
            }

            if (annotation instanceof HeaderParam) {
                parameter = getNewParameter(parameter, ParameterType.header, ((HeaderParam) annotation).value());
            }


            // TODO: add others
        }

        // read in additional info if present
        if (parameter != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Raw) {
                    parameter.setRaw();
                }
            }
        }

        return parameter;
    }

    private MethodParameter getNewParameter(MethodParameter parameter, ParameterType type, String value) {
        if (parameter != null) {
            throw new IllegalArgumentException("Parameter: " + parameter.getName() +"  already defined with: " + parameter.getType());
        }
        return new MethodParameter(type, value);
    }

    public MethodParameter get(Field field) {
        Assert.notNull(field, "Missing field to get parameter!");
        return parameters.get(field.getName());
    }

    /*public Pair<ParameterType, String> get(Field field) {
        return parameters.get(field.getName());
    }*/
/*
    public Map<String, Map<ParameterType, Object>> getFieldValues() {
        return fieldValues;
    }*/
}
