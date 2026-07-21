package com.example.monolith.service;

import static com.example.monolith.dto.ApiDtos.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.monolith.domain.Transaction;

import jakarta.persistence.EntityManager;

@Service
public class HistoryService {
    private final EntityManager entityManager;
    private final PersistenceService persistenceService;
    public HistoryService(EntityManager entityManager, PersistenceService persistenceService) {
        this.entityManager = entityManager; this.persistenceService = persistenceService;
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<HistoryView> transactionHistory(Long id) {
        List<Object[]> rows = AuditReaderFactory.get(entityManager).createQuery()
                .forRevisionsOfEntity(Transaction.class, false, true)
                .add(AuditEntity.id().eq(id))
                .addOrder(AuditEntity.revisionNumber().asc())
                .getResultList();
        return rows.stream().map(row -> {
            Transaction transaction = (Transaction) row[0];
            org.hibernate.envers.DefaultRevisionEntity revision = (org.hibernate.envers.DefaultRevisionEntity) row[1];
            RevisionType type = (RevisionType) row[2];
            LocalDateTime at = LocalDateTime.ofInstant(Instant.ofEpochMilli(revision.getTimestamp()), ZoneId.systemDefault());
            Object data = type == RevisionType.DEL ? null : persistenceService.transactionView(transaction);
            return new HistoryView(revision.getId(), type.name(), at, data);
        }).toList();
    }
}
