package com.uberTim12.ihor.exception;

public class UserAlreadyBlockedException extends RuntimeException {
    public UserAlreadyBlockedException(String message) {
        super(message);
    }
}
