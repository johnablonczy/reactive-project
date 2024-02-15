package com.example.reactiveproject.controller;

import com.example.reactiveproject.domain.GetPricesRequest;
import com.example.reactiveproject.domain.RecordTxnRequest;
import com.example.reactiveproject.domain.StockData;
import com.example.reactiveproject.domain.Transaction;
import com.example.reactiveproject.service.HistoricalDataService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

  List<String> acceptedSymbols = new ArrayList<>(){
    {
      add("AAPL");
      add("AMZN");
      add("BAC");
      add("IBM");
      add("GOOG");
      add("MS");
      add("MSFT");
      add("TSLA");
    }
  };

  /**
   * Call the historicalDataService to return matching StockData objects.
   * @param symbol Accepted symbols are: AAPL, AMZN, IBM, GOOG, BAC, MS, MSFT, TSLA
   * @param range Accepted ranges are: ytd, nD, nW, nY where n is an integer. Defaults to return all data for symbol.
   * @return zero or more StockData objects matching the criteria
   */
  @GetMapping(path = "/prices")
  public Flux<StockData> getPricesFromSymbolAndRange(
      @RequestParam String symbol,
      @RequestParam String range
  ) {
    if(!acceptedSymbols.contains(symbol.toUpperCase())) {
      return Flux.just(StockData.builder()
              .symbol("Improper price request received: Symbol not accepted symbol={"+symbol+"}. Use an accepted symbol: "+acceptedSymbols)
          .build());
    }
    if(!(range.equalsIgnoreCase("YTD") || range.toUpperCase().matches("^\\d+[YWD]$"))){
      return Flux.just(StockData.builder()
          .symbol("Improper price request received: Range not accepted range={"+range+"}. Use an accepted range: ytd, nD, nW, nY where n is an integer")
          .build());
    }


    return historicalDataService.getHistoricalDataForSymbolAndRange(Mono.just(new GetPricesRequest(symbol.toUpperCase(), range.toUpperCase())));
  }

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
  public Flux<StockData> postPricesFromSymbolAndRange(@RequestBody GetPricesRequest getPricesRequest) {
    Mono<GetPricesRequest> getPricesRequestMono = Mono.just(getPricesRequest);

    return getPricesRequestMono.flatMapMany(req -> {
      if(!acceptedSymbols.contains(req.getSymbol().toUpperCase())) {
        return Flux.just(StockData.builder()
                .symbol("Improper price request received: Symbol not accepted symbol={"+req.getSymbol()+"}. Use an accepted symbol: "+acceptedSymbols)
                .build());
      } else if(!(req.getRange().equalsIgnoreCase("YTD") || req.getRange().toUpperCase().matches("^\\d+[YWD]$"))){
        return Flux.just(StockData.builder()
                .symbol("Improper price request received: Range not accepted range={"+req.getRange()+"}. Use an accepted range: ytd, nD, nW, nY where n is an integer")
                .build());
      } else {
        return historicalDataService.getHistoricalDataForSymbolAndRange(Mono.just(new GetPricesRequest(req.getSymbol().toUpperCase(), req.getRange().toUpperCase())));
      }
    });
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
    Mono<RecordTxnRequest> recordTxnRequestMono = Mono.just(recordTxnRequest);

    return recordTxnRequestMono.flatMap(req -> {
      if(!acceptedSymbols.contains(req.getSymbol().toUpperCase())) {
        return Mono.just("Improper transaction request received: Symbol not accepted symbol={"+req.getSymbol()+"}. Use an accepted symbol: "+acceptedSymbols);
      }
      if(req.getTxnDate().isAfter(LocalDate.of(2024, 2, 1))) {
        return Mono.just("Improper transaction request received: Date past 2024-02-01 date={"+req.getTxnDate()+"}. Use a date on or before 2024-02-01");
      }
      Mono<Transaction> txn = historicalDataService.recordTransaction(req);
      return txn.map(t -> "Transaction successfully recorded txnId="+t.getTxnId());
    });
  }
}
