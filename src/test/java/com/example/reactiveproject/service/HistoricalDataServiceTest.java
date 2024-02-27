package com.example.reactiveproject.service;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.reactiveproject.domain.GetPricesRequest;
import com.example.reactiveproject.domain.RecordTxnRequest;
import com.example.reactiveproject.domain.StockData;
import com.example.reactiveproject.domain.Transaction;
import com.example.reactiveproject.repository.TxnRpsy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(MockitoJUnitRunner.class)
public class HistoricalDataServiceTest {
  @InjectMocks
  private HistoricalDataService historicalDataService;
  @Mock
  private WebClient iexClient;
  @Mock
  private TxnRpsy txnRpsy;
  @Mock
  @SuppressWarnings("rawtypes")
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
  @Mock
  @SuppressWarnings("rawtypes")
  private WebClient.RequestHeadersSpec requestHeadersSpecMock;
  @Mock
  @SuppressWarnings(("rawtypes"))
  private WebClient.ResponseSpec responseSpecMock;
  @Test
  @SuppressWarnings("unchecked")
  public void testGetPrices() {

    GetPricesRequest getPricesRequest = GetPricesRequest.builder()
            .symbol("aapl")
            .range("ytd")
            .build();

    Flux<StockData> stockData = Flux.just(
            generateTestStockData(0),
            generateTestStockData(1),
            generateTestStockData(2),
            generateTestStockData(3),
            generateTestStockData(4)
    );

    when(iexClient.get())
            .thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri(anyString()))
            .thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve())
            .thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToFlux(StockData.class))
            .thenReturn(stockData);

    Flux<StockData> stockDataFlux = historicalDataService.getHistoricalDataForSymbolAndRange(getPricesRequest);

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
  @SuppressWarnings("unchecked")
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

    when(iexClient.get())
        .thenReturn(requestHeadersUriSpecMock);
    when(requestHeadersUriSpecMock.uri("/stock/{symbol}/single/{date}", recordTxnRequest.getSymbol().toUpperCase(), recordTxnRequest.getTxnDate()))
        .thenReturn(requestHeadersSpecMock);
    when(requestHeadersSpecMock.retrieve())
        .thenReturn(responseSpecMock);
    when(responseSpecMock.bodyToMono(StockData.class))
        .thenReturn(Mono.just(stockData));

    when(txnRpsy.save(any(Transaction.class)))
        .thenReturn(Mono.just(transaction));

    Mono<Transaction> transactionMono = historicalDataService.recordTransaction(recordTxnRequest);

    StepVerifier.create(transactionMono)
        .expectNext(transaction)
        .verifyComplete();

    verify(responseSpecMock).bodyToMono(StockData.class);

    verify(txnRpsy).save(any());
  }
}
