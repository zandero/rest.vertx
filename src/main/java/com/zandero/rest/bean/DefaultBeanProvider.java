package com.zandero.rest.bean;

import com.zandero.rest.data.*;
import com.zandero.rest.exception.ClassFactoryException;
import com.zandero.rest.injection.InjectionProvider;
import io.vertx.core.cli.impl.ReflectionUtils;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.*;

import java.lang.reflect.*;

import static com.zandero.rest.data.ClassUtils.*;

/**
 * Create bean of type and fill internal variables from request / context,
 * NOTE: supports beans with primitive type fields only!
 */
public class DefaultBeanProvider implements BeanProvider {

    private final static Logger log = LoggerFactory.getLogger(DefaultBeanProvider.class);

    @Override
    public Object provide(Class clazz, RoutingContext context, InjectionProvider injectionProvider) throws Throwable {

        log.info("Provisioning bean: '" + clazz.getTypeName() + "'");
        BeanDefinition definition = new BeanDefinition(clazz);
        Object instance = ClassFactory.newInstanceOf(clazz, injectionProvider, context);
        setFields(instance, context, definition);

        log.info("Successfully created new instance of: '" + clazz.getTypeName() + "'");
        return instance;
    }

    /**
     * Sets object instance fields
     *
     * @param instance   to set fields
     * @param context    routing context
     * @param definition bean definition
     */
    private void setFields(Object instance, RoutingContext context, BeanDefinition definition)
        throws ClassFactoryException {

        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {

            MethodParameter parameter = definition.get(field);
            if (parameter != null) {
                String value = ArgumentProvider.getValue(null, parameter, context, parameter.getDefaultValue());
                Object fieldValue = stringToPrimitiveType(value, field.getType());
                setField(instance, field, fieldValue);
            }
        }

        Method[] methods = instance.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (ReflectionUtils.isSetter(method)) {
                MethodParameter parameter = definition.get(method);
                if (parameter != null) {
                    String value = ArgumentProvider.getValue(null, parameter, context, parameter.getDefaultValue());
                    Object methodValue = stringToPrimitiveType(value, parameter.getDataType());
                    invokeMethod(instance, method, parameter, methodValue);
                }
            }
        }
    }

    /**
     * Invokes simple setter method - setField(fieldValue)
     *
     * @param instance of object holding field
     * @param field    to be set
     * @param value    of field
     */
    private void setField(Object instance, Field field, Object value) throws ClassFactoryException {

        checkIfCompatibleType(field.getType(), value.getClass(),
                              "Can't set field: '" + field.getName() + "', value to: " + value);

        boolean isAccessible = field.isAccessible();
        if (!isAccessible) {
            field.setAccessible(true);
        }

        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new ClassFactoryException("Can't access field: '" + field.getName() + "'!", e);
        }

        if (!isAccessible) {
            field.setAccessible(false);
        }
    }

    /**
     * Invokes simple setter method with one argument
     *
     * @param instance    class instance
     * @param method      method to be invoked
     * @param parameter   to check if compatible with method
     * @param methodValue single argument value
     */
    private void invokeMethod(Object instance,
                              Method method,
                              MethodParameter parameter,
                              Object methodValue) throws ClassFactoryException {

        checkIfCompatibleType(parameter.getDataType(), methodValue.getClass(),
                              "Can't set field: '" + parameter.getName() + "', value to: " + methodValue);

        boolean isAccessible = method.isAccessible();
        if (!isAccessible) {
            method.setAccessible(true);
        }
        try {
            method.invoke(instance, methodValue);
        } catch (IllegalAccessException e) {
            throw new ClassFactoryException("Can't access method: '" + method.getName() + "'!", e);
        } catch (InvocationTargetException e) {
            throw new ClassFactoryException("Can't invoke method: '" + method.getName() + "'!", e);
        }

        if (!isAccessible) {
            method.setAccessible(false);
        }
    }
}
