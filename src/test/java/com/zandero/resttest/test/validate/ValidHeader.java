package com.zandero.resttest.test.validate;

import jakarta.validation.*;
import java.lang.annotation.*;

@Constraint(validatedBy = RequestHeaderInspector.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER } )
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidHeader {
    //error message
    String regex() default "[a-zA-Z]+";

    String message() default "Expected header as characters only!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}