package com.example.reactiveproject.integration;

import com.example.reactiveproject.controller.HistoricalDataController;
import com.example.reactiveproject.domain.RecordTxnRequest;
import com.example.reactiveproject.domain.StockData;
import com.example.reactiveproject.domain.Transaction;
import com.example.reactiveproject.repository.TxnRpsy;
import com.example.reactiveproject.service.HistoricalDataService;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = HistoricalDataController.class)
@Import(HistoricalDataService.class)
public class HistoricalDataIntegrationTest {
    @Autowired
    private WebTestClient webClient;
    @MockBean
    private HistoricalDataController historicalDataController;
    @MockBean
    private HistoricalDataService historicalDataService;
    @Mock
    private TxnRpsy txnRpsy;
    @Mock
    private WebClient iexClient;

    @Test
    public void testRecordTransactionValid() {
        UUID uuid = UUID.randomUUID();

        RecordTxnRequest recordTxnRequest = RecordTxnRequest.builder()
                .txnId(uuid)
                .firm("GS")
                .symbol("AAPL")
                .txnDate(LocalDate.ofYearDay(2024, 30))
                .build();

        StockData stockData = StockData.builder()
                .symbol("AAPL")
                .open(BigDecimal.TEN)
                .close(BigDecimal.valueOf(20))
                .low(BigDecimal.ONE)
                .high(BigDecimal.valueOf(30))
                .build();

        Transaction response = new Transaction(recordTxnRequest, stockData);

        webClient.post()
                        .uri("")
                                .contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(recordTxnRequest))
                                                .exchange()
                                                        .expectStatus().is2xxSuccessful();
    }
}
