package com.khokhlov.universityserver.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubjectTest {

    @Test
    void should_ReturnCorrectSubject_When_ValidValueProvided() {
        assertEquals(Subject.MATH, Subject.fromValue("Math"));
        assertEquals(Subject.PROGRAMMING, Subject.fromValue("Programming"));
    }

    @Test
    void should_ReturnCorrectStringRepresentation() {
        assertEquals("Math", Subject.MATH.toString());
        assertEquals("Economics", Subject.ECONOMICS.toString());
    }

    @Test
    void should_ThrowIllegalArgumentException_When_InvalidValueProvided() {
        assertThrows(IllegalArgumentException.class, () -> Subject.fromValue("NonExistSubject"));
    }
}