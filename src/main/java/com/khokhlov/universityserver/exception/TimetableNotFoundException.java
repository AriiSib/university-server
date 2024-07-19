package com.khokhlov.universityserver.exception;

public class TimetableNotFoundException extends RuntimeException {
    public TimetableNotFoundException(String message) {
        super(message);
    }
}
