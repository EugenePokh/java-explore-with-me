package com.explorewithme.server.validation.annotation;


import com.explorewithme.server.validation.EventDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EventDateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventDateConstraint {
    String message() default "Invalid event date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
