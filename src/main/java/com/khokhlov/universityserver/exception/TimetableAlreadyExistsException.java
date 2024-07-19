package com.khokhlov.universityserver.exception;

public class TimetableAlreadyExistsException extends RuntimeException {
    public TimetableAlreadyExistsException(String message) {
        super(message);
    }
}
