package com.example.reactiveproject.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("stock_data")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockData {
  private BigDecimal close;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal open;
  @PrimaryKey
  private String symbol;
  private int volume;
  private LocalDate date;
}
