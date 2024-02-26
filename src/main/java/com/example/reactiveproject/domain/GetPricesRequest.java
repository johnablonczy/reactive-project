package com.example.reactiveproject.domain;

import com.example.reactiveproject.validation.SymbolMatchConstraint;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Pattern.Flag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetPricesRequest {
  @SymbolMatchConstraint
  private String symbol;
  @Pattern(regexp = "^(ytd|\\d+D|\\d+W|\\d+Y)$", flags = Flag.CASE_INSENSITIVE, message = "Range not accepted. Use an accepted range: ytd, nD, nW, nY where n is an integer")
  private String range;
}
