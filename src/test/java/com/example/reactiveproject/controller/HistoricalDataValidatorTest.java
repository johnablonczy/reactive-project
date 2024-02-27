package com.example.reactiveproject.controller;

import com.example.reactiveproject.domain.GetPricesRequest;
import com.example.reactiveproject.domain.RecordTxnRequest;
import com.example.reactiveproject.validation.DateBeforeConstraint;
import com.example.reactiveproject.validation.SymbolMatchConstraint;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Pattern;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class HistoricalDataValidatorTest {
  final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  public void testRecordTxnRequestValid() {
      RecordTxnRequest recordTxnRequest = RecordTxnRequest.builder()
              .firm("GS")
              .symbol("aapl")
              .txnDate(LocalDate.of(2024, 2, 1))
              .build();

      Set<ConstraintViolation<RecordTxnRequest>> constraintViolations =
              validator.validate(recordTxnRequest);

      assertTrue(constraintViolations.isEmpty());
  }

  @Test
  public void testRecordTxnRequestInvalidSymbol() {
      RecordTxnRequest recordTxnRequest = RecordTxnRequest.builder()
              .firm("GS")
              .symbol("asdf")
              .txnDate(LocalDate.of(2024, 2, 1))
              .build();

      Set<ConstraintViolation<RecordTxnRequest>> constraintViolations =
              validator.validate(recordTxnRequest);

      assertEquals(1, constraintViolations.size());
      assertEquals(SymbolMatchConstraint.class, constraintViolations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
  }

  @Test
  public void testRecordTxnRequestInvalidDate() {
      RecordTxnRequest recordTxnRequest = RecordTxnRequest.builder()
              .firm("GS")
              .symbol("aapl")
              .txnDate(LocalDate.of(2024, 2, 2))
              .build();

      Set<ConstraintViolation<RecordTxnRequest>> constraintViolations =
              validator.validate(recordTxnRequest);

      assertEquals(1, constraintViolations.size());
      assertEquals(DateBeforeConstraint.class, constraintViolations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
  }

  @Test
  public void testGetPricesRequestValid() {
    GetPricesRequest getPricesRequest = GetPricesRequest.builder()
            .symbol("aapl")
            .range("ytd")
            .build();

    Set<ConstraintViolation<GetPricesRequest>> constraintViolations =
            validator.validate(getPricesRequest);

    assertTrue(constraintViolations.isEmpty());
  }

  @Test
  public void testGetPricesRequestInvalidSymbol() {
    GetPricesRequest getPricesRequest = GetPricesRequest.builder()
            .symbol("asdf")
            .range("ytd")
            .build();

    Set<ConstraintViolation<GetPricesRequest>> constraintViolations =
            validator.validate(getPricesRequest);

    assertEquals(1, constraintViolations.size());
    assertEquals(SymbolMatchConstraint.class, constraintViolations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
  }

  @Test
  public void testGetPricesRequestInvalidRange() {
    GetPricesRequest getPricesRequest = GetPricesRequest.builder()
            .symbol("aapl")
            .range("1h")
            .build();

    Set<ConstraintViolation<GetPricesRequest>> constraintViolations =
            validator.validate(getPricesRequest);

    assertEquals(1, constraintViolations.size());
    assertEquals(Pattern.class, constraintViolations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
  }


}
