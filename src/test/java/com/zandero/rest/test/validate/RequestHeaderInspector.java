package com.zandero.rest.test.validate;

import com.zandero.utils.StringUtils;

import jakarta.validation.*;

public class RequestHeaderInspector implements ConstraintValidator<ValidHeader, String>
{
    private String regularExpression;

    @Override
    public void initialize(ValidHeader constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);

        regularExpression = constraintAnnotation.regex();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        if (StringUtils.isNullOrEmptyTrimmed(regularExpression) || value == null) {
            return false;
        }

        return regularExpression.matches(value);
    }
}
