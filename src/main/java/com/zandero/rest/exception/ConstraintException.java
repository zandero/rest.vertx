package com.zandero.rest.exception;

import com.zandero.rest.data.*;
import com.zandero.utils.StringUtils;

import javax.validation.*;
import java.util.*;

/**
 * Extends on constraint violation exception and adds route definition
 * Tries to produce a meaningful error message from the route/violation combination
 */
public class ConstraintException extends ConstraintViolationException {

    private final RouteDefinition definition;

    public ConstraintException(RouteDefinition definition, Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(createMessage(definition, constraintViolations), constraintViolations);
        this.definition = definition;
    }

    public RouteDefinition getDefinition() {
        return definition;
    }

    /**
     * Tries to produce some sensible message to make some informed decision
     *
     * @param definition           route definition to get parameter information
     * @param constraintViolations list of violations
     * @return message describing violation
     */
    private static String createMessage(RouteDefinition definition, Set<? extends ConstraintViolation<?>> constraintViolations) {

        List<String> messages = new ArrayList<>();
        for (ConstraintViolation<?> violation : constraintViolations) {

            StringBuilder message = new StringBuilder();
            for (Path.Node next : violation.getPropertyPath()) {

                if (next instanceof Path.ParameterNode &&
                        next.getKind().equals(ElementKind.PARAMETER)) {

                    Path.ParameterNode paramNode = (Path.ParameterNode) next;
                    int index = paramNode.getParameterIndex();
                    if (index < definition.getParameters().size()) {
                        MethodParameter param = definition.getParameters().get(index);
                        switch (param.getType()) {
                            case body:
                                message.append(param.toString());
                                message.append(" ").append(param.getDataType().getSimpleName());
                                break;

                            default:
                                message.append(param.toString());
                                break;
                        }

                    }
                }

                if (next instanceof Path.PropertyNode &&
                        next.getKind().equals(ElementKind.PROPERTY)) {

                    Path.PropertyNode propertyNode = (Path.PropertyNode) next;
                    message.append(".").append(propertyNode.getName());
                }
            }

            message.append(": ").append(violation.getMessage());

            messages.add(message.toString());
        }

        return StringUtils.join(messages, ", ");
    }
}
