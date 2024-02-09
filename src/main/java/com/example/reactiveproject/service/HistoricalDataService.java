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
    this.iexClient = webClientBuilder.baseUrl("http://localhost:8090").build();
  }

  /**
   * Calls LocalIex REST endpoint to return StockData objects based on criteria
   * @param symbol Accepted symbols are: AAPL, AMZN, IBM, GOOG, BAC, MS, MSFT, TSLA
   * @param range Accepted ranges are: ytd, nD, nW, nY where n is an integer. Defaults to return all data for symbol.
   * @return zero or more StockData objects matching criteria
   */
  public Flux<StockData> getHistoricalDataForSymbolAndRange(String symbol, String range) {
    return this.iexClient.get()
        .uri("/stock/{symbol}/chart/{range}", symbol, range)
        .retrieve().bodyToFlux(StockData.class);
  }

  /**
   * Calls LocalIex REST endpoint to get a single StockData for a given symbol and date.
   * Then, create a transaction object and save to DB.
   * @param recordTxnRequest RecordTxnRequest object which holds request info (symbol, firm, date)
   * @return Transaction saved in DB
   */
  public Mono<Transaction> recordTransaction(RecordTxnRequest recordTxnRequest) {

    Mono<StockData> stockData = this.iexClient.get()
        .uri("/stock/{symbol}/single/{date}", recordTxnRequest.getSymbol(), recordTxnRequest.getTxnDate())
        .retrieve().bodyToMono(StockData.class);

    Mono<Transaction> txn = stockData.map(s -> new Transaction(recordTxnRequest, s));

    return txn.flatMap(t -> txnRpsy.save(t));
  }
}
