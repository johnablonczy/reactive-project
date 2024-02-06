package com.example.reactiveproject.domain;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecordTxnRequest {
  @PrimaryKey
  private UUID txnId = UUID.randomUUID();
  private String symbol;
  private String firm;
  private LocalDate txnDate;
}
