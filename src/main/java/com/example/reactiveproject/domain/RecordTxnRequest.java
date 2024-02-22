package com.example.reactiveproject.domain;

import java.time.LocalDate;
import java.util.UUID;

import com.example.reactiveproject.validation.DateBeforeConstraint;
import com.example.reactiveproject.validation.SymbolMatchConstraint;
import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecordTxnRequest {
  @PrimaryKey
  private UUID txnId = UUID.randomUUID();
  @NonNull
  @SymbolMatchConstraint
  private String symbol;
  @NonNull
  private String firm;
  @NonNull
  @DateBeforeConstraint("2024-02-01")
  private LocalDate txnDate;
}
