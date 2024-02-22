package com.example.reactiveproject.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateBeforeValidator implements ConstraintValidator<DateBeforeConstraint, LocalDate> {
    private LocalDate lastDate;
    @Override
    public void initialize(DateBeforeConstraint constraint) {
        this.lastDate = LocalDate.parse(constraint.value());
    }
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext ctx){
        return date.isBefore(lastDate.plusDays(1));
    }
}
