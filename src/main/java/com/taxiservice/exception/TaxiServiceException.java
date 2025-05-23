package com.taxiservice.exception;

public class TaxiServiceException extends RuntimeException {
    public TaxiServiceException(String message) {
        super(message);
    }

    public TaxiServiceException(String message, Throwable cause) {
        super(message, cause);
    }
} 