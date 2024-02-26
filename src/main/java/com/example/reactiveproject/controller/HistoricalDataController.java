package com.example.reactiveproject.controller;

import com.example.reactiveproject.domain.GetPricesRequest;
import com.example.reactiveproject.domain.RecordTxnRequest;
import com.example.reactiveproject.domain.StockData;
import com.example.reactiveproject.domain.Transaction;
import com.example.reactiveproject.service.HistoricalDataService;
import com.example.reactiveproject.validation.SymbolMatchConstraint;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class HistoricalDataController {

  @Autowired
  HistoricalDataService historicalDataService;

  /**
   * POST request for prices from symbol and range
   * @param getPricesRequest Request object which holds symbol and range.
   *                         Accepted symbols are: AAPL, AMZN, IBM, GOOG, BAC, MS, MSFT, TSLA
   *                         Accepted ranges are: ytd, nD, nW, nY where n is an integer. Defaults to return all data for symbol.
   * @return zero or more StockData objects matching the criteria
   */
  @PostMapping(path = "/prices",
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<StockData> postPricesFromSymbolAndRange(@Valid @RequestBody Mono<GetPricesRequest> getPricesRequest) {
    return getPricesRequest.flatMapMany(req -> historicalDataService.getHistoricalDataForSymbolAndRange(req));
  }

  /**
   * Call the historicalDataService to record a transaction based on request info
   * @param recordTxnRequest RecordTxnRequest object which holds request info (symbol, firm, date)
   * @return Confirmation of successful record keeping.
   */
  @PostMapping(path = "/transaction",
  consumes = MediaType.APPLICATION_JSON_VALUE,
  produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<String> recordTransaction(@Valid @RequestBody Mono<RecordTxnRequest> recordTxnRequest) {
    return recordTxnRequest
            .flatMap(req -> historicalDataService.recordTransaction(req))
            .map(txn -> "Transaction successfully recorded txnId="+txn.getTxnId());
  }
}
