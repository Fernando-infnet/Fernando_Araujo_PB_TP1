package com.example.monolith.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.monolith.domain.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
