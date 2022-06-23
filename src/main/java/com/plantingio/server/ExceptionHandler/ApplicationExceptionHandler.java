package com.plantingio.server.ExceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleException(IllegalArgumentException e) {
        Error error = new Error(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        return new ResponseEntity<>(error, error.getHttpStatus());
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Error> handleException(IllegalStateException e) {
        Error error = new Error(HttpStatus.NOT_FOUND, e.getLocalizedMessage());
        return new ResponseEntity<>(error, error.getHttpStatus());
    }
}
