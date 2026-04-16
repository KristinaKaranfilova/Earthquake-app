package com.example.earthquake.exception;

public class EarthquakeApiException extends RuntimeException {

    public EarthquakeApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
