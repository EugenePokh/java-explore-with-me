package com.explorewithme.server.validation;

import com.explorewithme.server.annotation.EventDateConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<EventDateConstraint, LocalDateTime> {

    @Override
    public void initialize(EventDateConstraint eventDate) {
    }

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext context) {
        if (eventDate == null) {
            return true;
        }

        return eventDate.isAfter(LocalDateTime.now().plusHours(2));
    }

}
