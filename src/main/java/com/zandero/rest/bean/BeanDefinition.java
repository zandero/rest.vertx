package com.zandero.rest.bean;

import com.zandero.rest.annotation.*;
import com.zandero.rest.data.*;
import com.zandero.utils.Assert;
import org.slf4j.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import static io.vertx.core.cli.impl.ReflectionUtils.isSetter;

public class BeanDefinition {

    private final static Logger log = LoggerFactory.getLogger(BeanDefinition.class);

    private static final String METHOD_PREFIX = "m:";
    private static final String FIELD_PREFIX = "p:";
    private static final String CONSTRUCTOR_SUFFIX = ":%d";

    Map<String, MethodParameter> parameters = new HashMap<>();

    public BeanDefinition(Class<?> clazz) {
        init(clazz);
    }

    public BeanDefinition(Constructor<?> constructor) {
        init(constructor);
    }

    private void init(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        Method[] methods = clazz.getDeclaredMethods();

        for (Field field : fields) {
            MethodParameter paramValues = getValueFromAnnotations(field.getAnnotations(), field.getType(), 0);
            if (paramValues != null) {
                parameters.put(FIELD_PREFIX + field.getName(), paramValues);
            }
        }

        for (Method method : methods) {
            if (isSetter(method)) {
                MethodParameter paramValues = getValueFromAnnotations(method.getAnnotations(), method.getParameterTypes()[0], 0);
                if (paramValues != null) {
                    parameters.put(METHOD_PREFIX + method.getName(), paramValues);
                }
            }
        }
    }

    private void init(Constructor<?> constructor) {
        Annotation[][] paramAnnotations = constructor.getParameterAnnotations();
        Class<?>[] types = constructor.getParameterTypes();

        for (int index = 0; index < paramAnnotations.length; index++) {
            Annotation[] annotations = paramAnnotations[index];
            Class<?> type = types[index];
            MethodParameter paramValues = getValueFromAnnotations(annotations, type, index);
            parameters.put(String.format(CONSTRUCTOR_SUFFIX, index), paramValues);
        }
    }

    private MethodParameter getValueFromAnnotations(Annotation[] annotations, Class<?> dataType, int index) {

        MethodParameter parameter = null;

        for (Annotation annotation : annotations) {
            if (annotation instanceof PathParam) {
                String value = ((PathParam) annotation).value();
                parameter = getNewParameter(parameter, ParameterType.path, value, dataType, index);
            }

            if (annotation instanceof QueryParam) {
                String value = ((QueryParam) annotation).value();
                parameter = getNewParameter(parameter, ParameterType.query, value, dataType, index);
            }

            if (annotation instanceof CookieParam) {
                String value = ((CookieParam) annotation).value();
                parameter = getNewParameter(parameter, ParameterType.cookie, value, dataType, index);
            }

            if (annotation instanceof HeaderParam) {
                String value = ((HeaderParam) annotation).value();
                parameter = getNewParameter(parameter, ParameterType.header, value, dataType, index);
            }

            if (annotation instanceof MatrixParam) {
                String value = ((MatrixParam) annotation).value();
                parameter = getNewParameter(parameter, ParameterType.matrix, value, dataType, index);
            }

            if (annotation instanceof BodyParam) {
                parameter = getNewParameter(parameter, ParameterType.body, "body", dataType, index); // TODO: check what we could do with value
            }

            if (annotation instanceof Context) {
                parameter = getNewParameter(parameter, ParameterType.context, "context", dataType, index); // TODO: check what we could do with value
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
                    log.warn("Can't provision " + parameter.getName() + " as ContextReader to: '" + dataType.getTypeName() + "'");
                }

                if (annotation instanceof RequestReader) {
                    log.warn("Can't provision " + parameter.getName() + " as RequestReader to: '" + dataType.getTypeName() + "'");
                }
            }
        }

        return parameter;
    }

    private MethodParameter getNewParameter(MethodParameter parameter,
                                            ParameterType type,
                                            String value,
                                            Class<?> dataType,
                                            int index) {
        if (parameter != null) {
            throw new IllegalArgumentException("Parameter: " + parameter.getName() + "  already defined with: " + parameter.getType());
        }
        return new MethodParameter(type, value, dataType, index);
    }

    public MethodParameter get(Field field) {
        Assert.notNull(field, "Missing field to get parameter!");
        return parameters.get(FIELD_PREFIX + field.getName());
    }

    public MethodParameter get(Method method) {
        Assert.notNull(method, "Missing method to get parameter!");
        return parameters.get(METHOD_PREFIX + method.getName());
    }

    public MethodParameter get(int index) {
        return parameters.get(String.format(CONSTRUCTOR_SUFFIX, index));
    }

    public int size() {
        return parameters.size();
    }
}
