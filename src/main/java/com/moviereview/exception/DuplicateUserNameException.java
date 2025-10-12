package com.moviereview.exception;

public class DuplicateUserNameException extends RuntimeException {
    
    public DuplicateUserNameException(String message) {
        super(message);
    }

}
