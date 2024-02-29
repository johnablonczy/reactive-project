package com.example.reactiveproject.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.example.reactiveproject.domain.GetPricesRequest;
import com.example.reactiveproject.domain.RecordTxnRequest;
import com.example.reactiveproject.domain.StockData;
import com.example.reactiveproject.domain.Transaction;
import com.example.reactiveproject.service.HistoricalDataService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(MockitoJUnitRunner.class)
public class HistoricalDataControllerTest {

  @InjectMocks
  private HistoricalDataController historicalDataController;

  @Mock
  private HistoricalDataService historicalDataService;

  @Test
  @SuppressWarnings("unchecked")
  public void testPostHistoricalData() {
    GetPricesRequest request = GetPricesRequest.builder()
            .symbol("aapl")
            .range("ytd")
            .build();

    Flux<StockData> response = Flux.just(
        generateTestStockData(0),
        generateTestStockData(1),
        generateTestStockData(2),
        generateTestStockData(3),
        generateTestStockData(4)
    );

    when(historicalDataService.getHistoricalDataForSymbolAndRange(request))
        .thenReturn(response);

    Flux<StockData> stockDataFlux = historicalDataController.postPricesFromSymbolAndRange(Mono.just(request));

    StepVerifier.create(stockDataFlux)
        .expectNext(generateTestStockData(0))
        .expectNext(generateTestStockData(1))
        .expectNext(generateTestStockData(2))
        .expectNext(generateTestStockData(3))
        .expectNext(generateTestStockData(4))
        .verifyComplete();
  }

  public StockData generateTestStockData(int n) {
    return StockData.builder()
        .date(LocalDate.ofYearDay(2024, 30-n))
        .open(BigDecimal.valueOf(10))
        .close(BigDecimal.valueOf(20))
        .high(BigDecimal.valueOf(30))
        .low(BigDecimal.valueOf(5))
        .volume(1)
        .symbol("AAPL")
        .build();
  }

  @Test
  public void testRecordTransaction() {
    UUID uuid = UUID.randomUUID();

    RecordTxnRequest recordTxnRequest = RecordTxnRequest.builder()
        .txnId(uuid)
        .firm("GS")
        .symbol("AAPL")
        .txnDate(LocalDate.ofYearDay(2024, 30))
        .build();

    StockData stockData = StockData.builder()
        .symbol("AAPL")
        .open(BigDecimal.TEN)
        .close(BigDecimal.valueOf(20))
        .low(BigDecimal.ONE)
        .high(BigDecimal.valueOf(30))
        .build();

    Transaction response = new Transaction(recordTxnRequest, stockData);

    when(historicalDataService.recordTransaction(recordTxnRequest)).thenReturn(
        Mono.just(response));

    Mono<String> recordTransactionResponse = historicalDataController.recordTransaction(Mono.just(recordTxnRequest));

    StepVerifier.create(recordTransactionResponse)
        .expectNext("Transaction successfully recorded txnId="+uuid)
        .verifyComplete();

    verify(historicalDataService).recordTransaction(recordTxnRequest);
  }
}
