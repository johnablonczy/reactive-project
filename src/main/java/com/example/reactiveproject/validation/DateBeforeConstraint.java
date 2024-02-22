package com.example.reactiveproject.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.time.LocalDate;

@Documented
@Constraint(validatedBy = DateBeforeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateBeforeConstraint {
    String message() default "Date not supported. Use a date on or before 2024-02-01";
    Class<?>[] groups() default  {};
    Class<? extends Payload>[] payload() default  {};

    String value();
}
