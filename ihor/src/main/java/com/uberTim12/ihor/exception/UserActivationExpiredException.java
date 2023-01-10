package com.uberTim12.ihor.exception;

public class UserActivationExpiredException extends RuntimeException {
    public UserActivationExpiredException(String message) {
        super(message);
    }
}
