package com.example.monolith.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
