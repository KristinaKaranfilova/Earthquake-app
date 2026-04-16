package com.example.earthquake.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EarthquakeApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(EarthquakeApiException ex) {
        log.error("USGS API error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(errorBody(ex.getMessage(), 502));
    }

    @ExceptionHandler(EarthquakeDatabaseException.class)
    public ResponseEntity<Map<String, Object>> handleDbException(EarthquakeDatabaseException ex) {
        log.error("Database error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody(ex.getMessage(), 500));
    }

    @ExceptionHandler(EarthquakeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EarthquakeNotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorBody(ex.getMessage(), 404));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorBody("An unexpected error occurred", 500));
    }

    private Map<String, Object> errorBody(String message, int status) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "status", status,
                "error", message
        );
    }
}
