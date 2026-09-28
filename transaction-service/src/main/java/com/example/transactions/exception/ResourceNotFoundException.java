package com.example.transactions.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " não encontrado(a): " + id);
    }
}
