package com.example.earthquake.exception;

public class EarthquakeDatabaseException extends RuntimeException {

    public EarthquakeDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
