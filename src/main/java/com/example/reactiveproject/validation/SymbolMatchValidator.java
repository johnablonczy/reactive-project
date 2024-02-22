package com.example.reactiveproject.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class SymbolMatchValidator implements ConstraintValidator<SymbolMatchConstraint, String> {
    List<String> acceptedSymbols = new ArrayList<>(){
        {
            add("AAPL");
            add("AMZN");
            add("BAC");
            add("IBM");
            add("GOOG");
            add("MS");
            add("MSFT");
            add("TSLA");
        }
    };
    @Override
    public boolean isValid(String symbolField, ConstraintValidatorContext cxt) {
        return acceptedSymbols.contains(symbolField);
    }
}
