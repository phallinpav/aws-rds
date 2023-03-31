package com.mangobyte.example.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AwsErrorExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { AwsErrorException.class })
    protected ResponseEntity<Object> handleAwsError(AwsErrorException e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(),
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = { CustomErrorException.class })
    protected ResponseEntity<Object> handleCustomError(CustomErrorException e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(),
                new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    protected ResponseEntity<Object> handleArgumentException(IllegalArgumentException e, WebRequest request) {
        return handleExceptionInternal(e, e.getMessage(),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
