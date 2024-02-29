package com.example.reactiveproject.integration;

import com.example.reactiveproject.controller.HistoricalDataController;
import com.example.reactiveproject.domain.RecordTxnRequest;
import com.example.reactiveproject.domain.StockData;
import com.example.reactiveproject.domain.Transaction;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.CassandraContainer;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@WebFluxTest(HistoricalDataController.class)
@AutoConfigureWebFlux
public class HistoricalDataIntegrationTest {
    @Autowired
    private WebTestClient webClient;

    static CassandraContainer<?> cassandraContainer = new CassandraContainer<>("cassandra:latest");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", cassandraContainer::getContactPoint);
        registry.add("spring.cassandra.local-datacenter", cassandraContainer::getLocalDatacenter);
    }

    @BeforeAll
    static void beforeAll() {
        cassandraContainer.start();
    }

    @AfterAll
    static void afterAll() {
        cassandraContainer.stop();
    }

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

        Transaction transaction = new Transaction(recordTxnRequest, stockData);


        webClient.post().uri("/transaction")
                .body(Mono.just(recordTxnRequest), RecordTxnRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Transaction successfully recorded txnId="+uuid);
    }
}
