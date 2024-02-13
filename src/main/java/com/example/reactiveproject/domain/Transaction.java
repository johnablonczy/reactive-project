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
}
