package com.example.transactions.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.transactions.domain.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId, Pageable pageable);
}
