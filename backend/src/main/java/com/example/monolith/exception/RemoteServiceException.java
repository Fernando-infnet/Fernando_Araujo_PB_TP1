package com.example.monolith.exception;

import org.springframework.http.HttpStatus;

public class RemoteServiceException extends RuntimeException {
    private final HttpStatus status;

    public RemoteServiceException(HttpStatus status, String message) {
        super(message);
        this.status = status == null ? HttpStatus.BAD_GATEWAY : status;
    }

    public HttpStatus getStatus() { return status; }
}
