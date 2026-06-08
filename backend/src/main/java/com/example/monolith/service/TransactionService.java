package com.example.monolith.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.monolith.domain.Transaction;
import com.example.monolith.exception.TransactionNotFoundException;
import com.example.monolith.repository.TransactionRepository;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> listAll() { return repository.findAll(); }

    public Transaction findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
    }

    public Transaction create(Transaction t) {
        t.setId(null);
        return repository.save(t);
    }

    public void delete(Long id) { repository.delete(findById(id)); }
}
