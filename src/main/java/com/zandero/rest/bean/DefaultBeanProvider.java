package com.zandero.rest.bean;

import com.zandero.rest.data.ArgumentProvider;
import com.zandero.rest.data.ClassFactory;
import com.zandero.rest.data.MethodParameter;
import com.zandero.rest.injection.InjectionProvider;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Create bean of type and fill internal variables from request / context
 */
public class DefaultBeanProvider implements BeanProvider {

    private final static Logger log = LoggerFactory.getLogger(DefaultBeanProvider.class);

    @Override
    public Object provide(Class clazz, RoutingContext context, InjectionProvider injectionProvider) throws Throwable {

        BeanDefinition definition = new BeanDefinition(clazz);

        // TODO: allow instatianation from various constructors if definition has enough data ..
        // for now leave it simple
        Object instance = ClassFactory.newInstanceOf(clazz, injectionProvider, context);

        //)
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            MethodParameter parameter = definition.get(field);
            if (parameter != null) {
                String value = ArgumentProvider.getValue(null, parameter, context, parameter.getDefaultValue());

                // TODO: currently only basic primitive fields can be set
                Object fieldValue = ClassFactory.stringToPrimitiveType(value, field.getType());
                setField(instance, field, fieldValue);
            }
        }

        return instance;
    }

    /**
     * Set field
     *
     * @param instance of object holding field
     * @param field    to be set
     * @param value    of field
     * @throws IllegalAccessException should not be triggered
     */
    private void setField(Object instance, Field field, Object value) throws IllegalAccessException {
        boolean isAccessible = field.isAccessible();
        if (!isAccessible) {
            field.setAccessible(true);
        }

        field.set(instance, value);

        if (!isAccessible) {
            field.setAccessible(false);
        }
    }
}
