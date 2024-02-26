package com.example.reactiveproject.controller;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;

@RestControllerAdvice
public class ValidationHandler {

  @ExceptionHandler(WebExchangeBindException.class)
  public Flux<String> handleException(WebExchangeBindException e) {
   return Flux.just(e.getFieldError().getDefaultMessage());
  }
}
