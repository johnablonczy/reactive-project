package com.example.reactiveproject.controller;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.example.reactiveproject.domain.RecordTxnRequest;
import com.example.reactiveproject.domain.StockData;
import com.example.reactiveproject.domain.Transaction;
import com.example.reactiveproject.service.HistoricalDataService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@WebFluxTest(HistoricalDataController.class)
@RunWith(SpringRunner.class)
public class HistoricalDataControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @MockBean
  private HistoricalDataService historicalDataService;

  @Test
  public void testGetHistoricalData() {

    Flux<StockData> response = Flux.just(
        generateTestStockData(0),
        generateTestStockData(1),
        generateTestStockData(2),
        generateTestStockData(3),
        generateTestStockData(4)
    );

    when(historicalDataService.getHistoricalDataForSymbolAndRange("aapl", "1w"))
        .thenReturn(response);

    Flux<StockData> stockDataFlux = webTestClient.get().uri("/prices?symbol=aapl&range=1w").header(ACCEPT, APPLICATION_JSON_VALUE)
        .exchange()
        .expectStatus().isOk()
        .returnResult(StockData.class)
        .getResponseBody();

    StepVerifier.create(stockDataFlux)
        .expectNext(generateTestStockData(0))
        .expectNext(generateTestStockData(1))
        .expectNext(generateTestStockData(2))
        .expectNext(generateTestStockData(3))
        .expectNext(generateTestStockData(4))
        .verifyComplete();
  }

  @Test
  public void testGetHistoricalDataBadSymbol() {
    Flux<StockData> stockDataFlux = webTestClient.get().uri("/prices?symbol=asdf&range=1w").header(ACCEPT, APPLICATION_JSON_VALUE)
        .exchange()
        .expectStatus().isOk()
        .returnResult(StockData.class)
        .getResponseBody();

    StepVerifier.create(stockDataFlux)
        .expectNext(StockData.builder()
            .symbol("Improper price request received: Symbol not accepted symbol={asdf}. Use an accepted symbol: [AAPL, AMZN, BAC, IBM, GOOG, MS, MSFT, TSLA]")
            .build())
        .verifyComplete();
  }

  @Test
  public void testGetHistoricalDataBadRange() {
    Flux<StockData> stockDataFlux = webTestClient.get().uri("/prices?symbol=aapl&range=1h").header(ACCEPT, APPLICATION_JSON_VALUE)
        .exchange()
        .expectStatus().isOk()
        .returnResult(StockData.class)
        .getResponseBody();

    StepVerifier.create(stockDataFlux)
        .expectNext(StockData.builder()
            .symbol("Improper price request received: Range not accepted range={1h}. Use an accepted range: ytd, nD, nW, nY where n is an integer")
            .build())
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

    Transaction transaction = new Transaction(recordTxnRequest, stockData);

    when(historicalDataService.recordTransaction(recordTxnRequest)).thenReturn(
        Mono.just(transaction));

    Flux<String> recordTransactionResponse = webTestClient.post().uri("/transaction").header(ACCEPT, APPLICATION_JSON_VALUE)
        .bodyValue(recordTxnRequest)
        .exchange()
        .expectStatus().isOk()
        .returnResult(String.class)
        .getResponseBody();

    StepVerifier.create(recordTransactionResponse)
        .expectNext("Transaction successfully recorded txnId="+uuid)
        .verifyComplete();
  }

  @Test
  public void testRecordTransactionBadSymbol() {
    UUID uuid = UUID.randomUUID();

    RecordTxnRequest recordTxnRequest = RecordTxnRequest.builder()
        .txnId(uuid)
        .firm("GS")
        .symbol("ASDF")
        .txnDate(LocalDate.ofYearDay(2024, 30))
        .build();

    StockData stockData = StockData.builder()
        .symbol("AAPL")
        .open(BigDecimal.TEN)
        .close(BigDecimal.valueOf(20))
        .low(BigDecimal.ONE)
        .high(BigDecimal.valueOf(30))
        .build();

    Transaction transaction = new Transaction(recordTxnRequest, stockData);

    when(historicalDataService.recordTransaction(recordTxnRequest)).thenReturn(
        Mono.just(transaction));

    Flux<String> recordTransactionResponse = webTestClient.post().uri("/transaction").header(ACCEPT, APPLICATION_JSON_VALUE)
        .bodyValue(recordTxnRequest)
        .exchange()
        .expectStatus().isOk()
        .returnResult(String.class)
        .getResponseBody();

    StepVerifier.create(recordTransactionResponse)
        .expectNext("Improper transaction request received: Symbol not accepted symbol={ASDF}. Use an accepted symbol: [AAPL, AMZN, BAC, IBM, GOOG, MS, MSFT, TSLA]")
        .verifyComplete();
  }

  @Test
  public void testRecordTransactionBadDate() {
    UUID uuid = UUID.randomUUID();

    RecordTxnRequest recordTxnRequest = RecordTxnRequest.builder()
        .txnId(uuid)
        .firm("GS")
        .symbol("AAPL")
        .txnDate(LocalDate.of(2024, 2, 2))
        .build();

    StockData stockData = StockData.builder()
        .symbol("AAPL")
        .open(BigDecimal.TEN)
        .close(BigDecimal.valueOf(20))
        .low(BigDecimal.ONE)
        .high(BigDecimal.valueOf(30))
        .build();

    Transaction transaction = new Transaction(recordTxnRequest, stockData);

    when(historicalDataService.recordTransaction(recordTxnRequest)).thenReturn(
        Mono.just(transaction));

    Flux<String> recordTransactionResponse = webTestClient.post().uri("/transaction").header(ACCEPT, APPLICATION_JSON_VALUE)
        .bodyValue(recordTxnRequest)
        .exchange()
        .expectStatus().isOk()
        .returnResult(String.class)
        .getResponseBody();

    StepVerifier.create(recordTransactionResponse)
        .expectNext("Improper transaction request received: Date past 2024-02-01 date={2024-02-02}. Use a date on or before 2024-02-01")
        .verifyComplete();
  }
  
}