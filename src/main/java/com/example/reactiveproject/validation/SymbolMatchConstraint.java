package com.example.reactiveproject.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SymbolMatchValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SymbolMatchConstraint {
    String message() default "Symbol not supported. Use an accepted symbol: AAPL, AMZN, BAC, GOOG, IBM, MS, MSFT, TSLA";
    Class<?>[] groups() default  {};
    Class<? extends Payload>[] payload() default  {};
}
