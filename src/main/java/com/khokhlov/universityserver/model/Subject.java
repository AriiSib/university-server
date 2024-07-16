package com.khokhlov.universityserver.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Subject {
    MATH("Math"),
    ECONOMICS("Economics"),
    PROGRAMMING("Programming");

    private final String value;


    @JsonValue
    @Override
    public String toString() {
        return this.value;
    }

    @JsonCreator
    public static Subject fromValue(String value) {
        for (Subject subject : Subject.values()) {
            if (subject.value.equalsIgnoreCase(value)) {
                return subject;
            }
        }
        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
