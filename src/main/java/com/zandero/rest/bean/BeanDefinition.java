package com.zandero.rest.bean;

import com.zandero.rest.annotation.BodyParam;
import com.zandero.rest.annotation.ContextReader;
import com.zandero.rest.annotation.Raw;
import com.zandero.rest.annotation.RequestReader;
import com.zandero.rest.data.MethodParameter;
import com.zandero.rest.data.ParameterType;
import com.zandero.utils.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
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

            if (annotation instanceof MatrixParam) {
                parameter = getNewParameter(parameter, ParameterType.matrix, ((MatrixParam) annotation).value());
            }

            if (annotation instanceof BodyParam) {
                parameter = getNewParameter(parameter, ParameterType.body, "body"); // TODO: check what we could do with value
            }

            if (annotation instanceof Context) {
                parameter = getNewParameter(parameter, ParameterType.context, "context"); // TODO: check what we could do with value
            }
        }

        // read in additional info if present
        if (parameter != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Raw) {
                    parameter.setRaw();
                }

                if (annotation instanceof DefaultValue) {
                    parameter.setDefaultValue(((DefaultValue) annotation).value());
                }

                if (annotation instanceof ContextReader) {
                    //parameter.setContextProvider();
                }

                if (annotation instanceof RequestReader) {
                    //parameter.setValueReader();
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
}
