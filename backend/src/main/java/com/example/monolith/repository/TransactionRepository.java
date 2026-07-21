package com.example.monolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.monolith.domain.Transaction;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId, Pageable pageable);

    @Query("""
            select coalesce(sum(case when t.type = com.example.monolith.domain.TransactionType.CREDIT
                then t.amount else -t.amount end), 0)
            from Transaction t where t.wallet.id = :walletId
            """)
    BigDecimal calculateBalance(@Param("walletId") Long walletId);
}
