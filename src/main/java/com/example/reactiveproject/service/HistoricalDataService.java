package com.example.reactiveproject.service;

import com.example.reactiveproject.domain.RecordTxnRequest;
import com.example.reactiveproject.domain.StockData;
import com.example.reactiveproject.domain.Transaction;
import com.example.reactiveproject.repository.TxnRpsy;
import java.time.LocalDate;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Log
public class HistoricalDataService {

  private final WebClient iexClient;

  @Autowired
  private TxnRpsy txnRpsy;

  public HistoricalDataService(WebClient.Builder webClientBuilder) {
    this.iexClient = webClientBuilder.baseUrl("http://localhost:8090").filter(logRequest()).build();
  }

  public Flux<StockData> getHistoricalDataForSymbolAndRange(String symbol, String range) {
    return this.iexClient.get()
        .uri("/stock/{symbol}/chart/{range}", symbol, range)
        .retrieve().bodyToFlux(StockData.class);
  }

  public Mono<Transaction> recordTransaction(RecordTxnRequest recordTxnRequest) {

    Mono<StockData> stockData = this.iexClient.get()
        .uri("/stock/{symbol}/single/{date}", recordTxnRequest.getSymbol(), recordTxnRequest.getTxnDate())
        .retrieve().bodyToMono(StockData.class);

    Mono<Transaction> txn = stockData.map(s -> new Transaction(recordTxnRequest, s));

    return txn.flatMap(t -> txnRpsy.save(t));
  }

  private ExchangeFilterFunction logRequest() {
    return (clientRequest, next) -> {
      log.info("Request: {"+clientRequest.method()+"} {"+clientRequest.url()+"}");
      clientRequest.headers()
          .forEach((name, values) -> values.forEach(value -> log.info("{"+name+"}={"+value+"}")));
      return next.exchange(clientRequest);
    };
  }
}
