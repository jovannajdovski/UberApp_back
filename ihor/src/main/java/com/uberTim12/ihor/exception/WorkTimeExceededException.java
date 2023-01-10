package com.uberTim12.ihor.exception;

public class WorkTimeExceededException extends RuntimeException {
    public WorkTimeExceededException(String message) {
        super(message);
    }
}
