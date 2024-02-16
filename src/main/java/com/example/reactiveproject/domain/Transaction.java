package com.example.reactiveproject.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import reactor.core.publisher.Mono;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("transactions")
public class Transaction {
  @PrimaryKey
  @Builder.Default
  private UUID txnId = UUID.randomUUID();
  private String symbol;
  private String firm;
  private LocalDate txnDate;
  private BigDecimal close;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal open;


  public Transaction(RecordTxnRequest recordTxnRequest, StockData stockData) {
    this.symbol = recordTxnRequest.getSymbol();
    this.firm = recordTxnRequest.getFirm();
    this.txnDate = recordTxnRequest.getTxnDate();
    this.txnId = recordTxnRequest.getTxnId();

    this.close = stockData.getClose();
    this.high = stockData.getHigh();
    this.low = stockData.getLow();
    this.open = stockData.getOpen();
  }

  public static Transaction fromTxnRequest(RecordTxnRequest recordTxnRequest) {
    Transaction fromTxnRequest = new Transaction();
    fromTxnRequest.symbol = recordTxnRequest.getSymbol();
    fromTxnRequest.firm = recordTxnRequest.getFirm();
    fromTxnRequest.txnDate = recordTxnRequest.getTxnDate();
    fromTxnRequest.txnId = recordTxnRequest.getTxnId();
    return fromTxnRequest;
  }

  public static Mono<Transaction> fromStockDataMono(Mono<StockData> stockData) {
    return stockData.map(s -> Transaction.builder().close(s.getClose()).high(s.getHigh()).low(s.getLow()).open(s.getOpen()).build());
  }

  public Transaction stitchStockData(StockData stockData) {
    this.close = stockData.getClose();
    this.high = stockData.getHigh();
    this.low = stockData.getLow();
    this.open = stockData.getOpen();
  }

  public Transaction stitchTxnReqData(RecordTxnRequest recordTxnRequest) {
    this.symbol = recordTxnRequest.getSymbol();
    this.firm = recordTxnRequest.getFirm();
    this.txnDate = recordTxnRequest.getTxnDate();
    this.txnId = recordTxnRequest.getTxnId();
    return this;
  }
}
