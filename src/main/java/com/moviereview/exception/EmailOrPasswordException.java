package com.moviereview.exception;

public class EmailOrPasswordException extends RuntimeException {
    public EmailOrPasswordException(String message) {
        super(message);
    }
}

