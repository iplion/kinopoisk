package com.iplion.films.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KinopoiskClientException extends RuntimeException {

    private final HttpStatus httpStatus;

    public KinopoiskClientException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public KinopoiskClientException(String message, HttpStatus httpStatus) {
        this(message, httpStatus, null);
    }
}