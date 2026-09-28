package com.example.transactions.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.transactions.domain.ProcessedMessage;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, UUID> {}
