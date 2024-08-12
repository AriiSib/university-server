package com.khokhlov.universityserver.model.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StudentDTOTest {

    @Test
    void should_ValidateStudentDTO_When_ValidDataProvided() {
        StudentDTO studentDTO = StudentDTO.builder()
                .name("John")
                .surname("Doe")
                .birthdate(LocalDate.of(2000, 1, 1))
                .phoneNumber("+7 (123) 456-78-90")
                .build();

        assertDoesNotThrow(studentDTO::validate);
    }

    @Test
    void should_ThrowException_When_InvalidNameProvided() {
        StudentDTO studentDTO = StudentDTO.builder()
                .name("john!")
                .surname("Doe")
                .birthdate(LocalDate.of(2000, 1, 1))
                .phoneNumber("+7 (123) 456-78-90")
                .build();

        assertThrows(IllegalArgumentException.class, studentDTO::validate);
    }

    @Test
    void should_ThrowException_When_InvalidSurnameProvided() {
        StudentDTO studentDTO = StudentDTO.builder()
                .name("John")
                .surname("doe!")
                .birthdate(LocalDate.of(2000, 1, 1))
                .phoneNumber("+7 (123) 456-78-90")
                .build();

        assertThrows(IllegalArgumentException.class, studentDTO::validate);
    }

    @Test
    void should_ThrowException_When_InvalidPhoneNumberProvided() {
        StudentDTO studentDTO = StudentDTO.builder()
                .name("John")
                .surname("Doe")
                .birthdate(LocalDate.of(2000, 1, 1))
                .phoneNumber("1234567890")
                .build();

        assertThrows(IllegalArgumentException.class, studentDTO::validate);
    }

}