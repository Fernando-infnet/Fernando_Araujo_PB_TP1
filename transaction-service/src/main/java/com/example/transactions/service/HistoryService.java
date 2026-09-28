package com.example.transactions.service;

import static com.example.transactions.dto.TransactionDtos.HistoryView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.transactions.domain.Transaction;

import jakarta.persistence.EntityManager;

@Service
public class HistoryService {
    private final EntityManager entityManager;
    private final TransactionService transactions;

    public HistoryService(EntityManager entityManager, TransactionService transactions) {
        this.entityManager = entityManager;
        this.transactions = transactions;
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<HistoryView> transactionHistory(Long id) {
        List<Object[]> rows = AuditReaderFactory.get(entityManager).createQuery()
                .forRevisionsOfEntity(Transaction.class, false, true)
                .add(AuditEntity.id().eq(id))
                .addOrder(AuditEntity.revisionNumber().asc())
                .getResultList();
        return rows.stream().map(row -> historyView((Transaction) row[0],
                (DefaultRevisionEntity) row[1], (RevisionType) row[2])).toList();
    }

    private HistoryView historyView(Transaction transaction, DefaultRevisionEntity revision, RevisionType type) {
        LocalDateTime at = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(revision.getTimestamp()), ZoneId.systemDefault());
        Object data = type == RevisionType.DEL ? null : transactions.view(transaction);
        return new HistoryView(revision.getId(), type.name(), at, data);
    }
}
