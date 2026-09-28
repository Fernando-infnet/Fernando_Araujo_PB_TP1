package com.example.transactions.exception;

public class WalletServiceUnavailableException extends RuntimeException {
    public WalletServiceUnavailableException() {
        super("Serviço de carteiras indisponível");
    }
}
