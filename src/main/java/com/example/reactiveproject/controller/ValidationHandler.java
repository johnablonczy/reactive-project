package com.example.reactiveproject.controller;

import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;

@RestControllerAdvice
@Log
public class ValidationHandler {

  @ExceptionHandler(WebExchangeBindException.class)
  public Flux<String> handleException(WebExchangeBindException e) {
      String errMessage = e.getFieldError().getDefaultMessage();
      log.severe("Bad request reveieved: "+errMessage);
      return Flux.just(errMessage);
  }
}
