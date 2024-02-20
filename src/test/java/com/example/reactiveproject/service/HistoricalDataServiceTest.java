package com.example.reactiveproject.service;



import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@WebFluxTest(HistoricalDataService.class)
@RunWith(MockitoJUnitRunner.class)
public class HistoricalDataServiceTest {
  @Mock
  WebClient iexClient;
  @Mock
  TxnRpsy txnRpsy;
  @InjectMocks
  HistoricalDataService historicalDataService;
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

    Mono<Transaction> transactionMono = historicalDataService.recordTransaction(Mono.just(recordTxnRequest));

    verify(responseSpecMock).bodyToMono(StockData.class);

    verify(txnRpsy, times(1)).save(any());

    StepVerifier.create(transactionMono)
        .expectNext(transaction)
        .verifyComplete();
  }
}
