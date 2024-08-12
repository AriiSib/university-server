package com.khokhlov.universityserver.model.dto;

import com.khokhlov.universityserver.model.Subject;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TeacherDTOTest {

    @Test
    void should_ValidateTeacherDTO_When_ValidDataProvided() {
        TeacherDTO teacherDTO = TeacherDTO.builder()
                .name("John")
                .surname("Doe")
                .experience(5L)
                .subjects(Arrays.asList(Subject.MATH, Subject.PHYSICS))
                .build();

        assertDoesNotThrow(teacherDTO::validate);
    }

    @Test
    void should_ThrowException_When_InvalidNameProvided() {
        TeacherDTO teacherDTO = TeacherDTO.builder()
                .name("john!")
                .surname("Doe")
                .experience(5L)
                .subjects(Arrays.asList(Subject.MATH, Subject.PHYSICS))
                .build();

        assertThrows(IllegalArgumentException.class, teacherDTO::validate);
    }

    @Test
    void should_ThrowException_When_InvalidSurnameProvided() {
        TeacherDTO teacherDTO = TeacherDTO.builder()
                .name("John")
                .surname("doe!")
                .experience(5L)
                .subjects(Arrays.asList(Subject.MATH, Subject.PHYSICS))
                .build();

        assertThrows(IllegalArgumentException.class, teacherDTO::validate);
    }

}