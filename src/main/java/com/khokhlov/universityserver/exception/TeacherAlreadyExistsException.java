package com.khokhlov.universityserver.exception;

public class TeacherAlreadyExistsException extends RuntimeException {
    public TeacherAlreadyExistsException(String message) {
        super(message);
    }
}
