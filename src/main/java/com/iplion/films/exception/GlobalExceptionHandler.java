package com.iplion.films.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(KinopoiskClientException.class)
    public ResponseEntity<ErrorResponse> handleKinopoiskClientException(KinopoiskClientException e) {
        return ResponseEntity
            .status(e.getHttpStatus())
            .body(new ErrorResponse(
                e.getHttpStatus().value(),
                e.getMessage()
            ));
    }

    public record ErrorResponse(
        int status,
        String message
    ) {
    }
}
