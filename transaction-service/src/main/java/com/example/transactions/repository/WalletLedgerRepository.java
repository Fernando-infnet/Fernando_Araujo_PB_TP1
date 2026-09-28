package com.example.transactions.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.transactions.domain.WalletLedger;

import jakarta.persistence.LockModeType;

@Repository
public interface WalletLedgerRepository extends JpaRepository<WalletLedger, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ledger from WalletLedger ledger where ledger.walletId = :walletId")
    Optional<WalletLedger> findByWalletIdForUpdate(@Param("walletId") Long walletId);
}
