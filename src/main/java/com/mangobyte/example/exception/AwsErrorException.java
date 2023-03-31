package com.mangobyte.example.exception;

public class AwsErrorException extends RuntimeException {
    public AwsErrorException(String message) {
        super(message);
    }
}
