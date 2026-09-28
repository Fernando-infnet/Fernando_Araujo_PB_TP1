package com.example.monolith.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.monolith.domain.WalletEventOutbox;

public interface WalletEventOutboxRepository extends JpaRepository<WalletEventOutbox, UUID> {
    List<WalletEventOutbox> findTop50ByPublishedAtIsNullOrderByOccurredAtAsc();
}
