package com.example.reactiveproject.controller;

import static org.junit.Assert.assertTrue;

import com.example.reactiveproject.domain.RecordTxnRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ValidatorTest {
  private static ValidatorFactory validatorFactory;

  private static Validator validator;

  @BeforeClass
  public static void createValidator() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @AfterClass
  public static void close() {
    validatorFactory.close();
  }

  @Test
  public void testValidRecordTxnRequest(){
    RecordTxnRequest recordTxnRequest = RecordTxnRequest.builder()
        .firm("GS")
        .symbol("AAPL")
        .txnDate(LocalDate.ofYearDay(2024, 30))
        .build();

    Set<ConstraintViolation<RecordTxnRequest>> violations = validator.validate(recordTxnRequest);

    assertTrue(violations.isEmpty());
  }
}
