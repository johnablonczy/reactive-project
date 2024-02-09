package com.example.reactiveproject.controller;

import com.example.reactiveproject.domain.RecordTxnRequest;
import com.example.reactiveproject.domain.StockData;
import com.example.reactiveproject.domain.Transaction;
import com.example.reactiveproject.service.HistoricalDataService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class HistoricalDataController {

  @Autowired
  HistoricalDataService historicalDataService;

  /**
   * Call the historicalDataService to return matching StockData objects.
   * @param symbol Accepted symbols are: AAPL, AMZN, IBM, GOOG, BAC, MS, MSFT, TSLA
   * @param range Accepted ranges are: ytd, nD, nW, nY where n is an integer. Defaults to return all data for symbol.
   * @return zero or more StockData objects matching the criteria
   */
  @GetMapping(path = "/prices")
  public Flux<StockData> getPricesWithSymbolAndRange(
      @RequestParam String symbol,
      @RequestParam String range
  ) {
    return historicalDataService.getHistoricalDataForSymbolAndRange(symbol, range);
  }

  /**
   * Call the historicalDataService to record a transaction based on request info
   * @param recordTxnRequest RecordTxnRequest object which holds request info (symbol, firm, date)
   * @return Confirmation of successful record keeping.
   */
  @PostMapping(path = "/transaction",
  consumes = MediaType.APPLICATION_JSON_VALUE,
  produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<String> recordTransaction(@RequestBody RecordTxnRequest recordTxnRequest) {
    Mono<Transaction> txn = historicalDataService.recordTransaction(recordTxnRequest);
    return txn.map(t -> "Transaction successfully recorded txnId="+t.getTxnId());
  }
}
