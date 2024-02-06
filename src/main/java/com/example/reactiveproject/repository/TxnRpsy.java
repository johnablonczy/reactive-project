package com.example.reactiveproject.repository;

import com.example.reactiveproject.domain.Transaction;
import java.util.UUID;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TxnRpsy extends ReactiveCrudRepository<Transaction, UUID> {
}
