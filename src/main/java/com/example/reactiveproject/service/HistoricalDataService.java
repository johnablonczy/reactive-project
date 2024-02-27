package com.example.reactiveproject.service;

import com.example.reactiveproject.domain.GetPricesRequest;
import com.example.reactiveproject.domain.RecordTxnRequest;
import com.example.reactiveproject.domain.StockData;
import com.example.reactiveproject.domain.Transaction;
import com.example.reactiveproject.repository.TxnRpsy;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Log
public class HistoricalDataService {

  private WebClient iexClient;

  private TxnRpsy txnRpsy;

  /**
   * Calls LocalIex REST endpoint to return StockData objects based on criteria
   * @param getPricesRequest Request object which holds symbol and range.
   *                             Accepted symbols are: AAPL, AMZN, IBM, GOOG, BAC, MS, MSFT, TSLA
   *                             Accepted ranges are: ytd, nD, nW, nY where n is an integer. Defaults to return all data for symbol.
   * @return zero or more StockData objects matching criteria
   */
  public Flux<StockData> getHistoricalDataForSymbolAndRange(GetPricesRequest getPricesRequest) {
    return iexClient.get()
        .uri("/stock/{symbol}/chart/{range}", getPricesRequest.getSymbol(),
            getPricesRequest.getRange())
        .retrieve().bodyToFlux(StockData.class)
            .doOnNext(req -> log.info("HistoricalDataService successfully received StockData={"+req.toString()+"}"));
  }

  /**
   * Calls LocalIex REST endpoint to get a single StockData for a given symbol and date.
   * Then, create a transaction object and save to DB.
   * @param recordTxnRequest RecordTxnRequest object which holds request info (symbol, firm, date)
   * @return Transaction saved in DB
   */
  public Mono<Transaction> recordTransaction(RecordTxnRequest recordTxnRequest) {
    return iexClient.get()
            .uri("/stock/{symbol}/single/{date}", recordTxnRequest.getSymbol().toUpperCase(), recordTxnRequest.getTxnDate())
        .retrieve()
        .bodyToMono(StockData.class)
            .map(stockData -> new Transaction(recordTxnRequest, stockData))
            .flatMap(txn -> txnRpsy.save(txn))
            .doOnNext(req -> log.info("HistoricalDataService successfully recorded Transaction={"+req.toString()+"}"));
  }
}
