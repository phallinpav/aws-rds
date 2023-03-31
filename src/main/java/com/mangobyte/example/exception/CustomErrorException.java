package com.mangobyte.example.exception;

public class CustomErrorException extends RuntimeException {
    public CustomErrorException(String message) {
        super(message);
    }

    public CustomErrorException(Exception e) {
        super(e);
    }
}
