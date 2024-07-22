package com.khokhlov.universityserver.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    private Student createStudent_1() {
        return new Student(1L, "John", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");
    }

    private Student createStudent_2() {
        return new Student(2L, "John", "Doe", LocalDate.of(1999, 2, 3), "+375 (29) 123-45-67");
    }

    @Test
    void should_CreateStudent_When_ValidArgumentsProvided() {
        Student student = createStudent_1();

        assertEquals(1L, student.getId());
        assertEquals("John", student.getName());
        assertEquals("Doe", student.getSurname());
        assertEquals(LocalDate.of(2000, 1, 1), student.getBirthDate());
        assertEquals("+7 (123) 456-78-90", student.getPhoneNumber());
    }

    @Test
    void should_ReturnTrue_When_StudentsAreEqual() {
        Student student_1 = createStudent_1();
        Student student_2 = createStudent_1();

        assertEquals(student_1, student_2);
        assertEquals(student_1.hashCode(), student_2.hashCode());
    }

    @Test
    void should_ReturnFalse_When_StudentsHaveDifferentAttributes() {
        Student student_1 = createStudent_1();
        Student student_2 = createStudent_2();

        assertNotEquals(student_1, student_2);
        assertNotEquals(student_1.hashCode(), student_2.hashCode());
    }

    @Test
    void should_ReturnFalse_When_ObjectIsNotStudent() {
        Student student = createStudent_1();
        Object notStudent = new Object();

        assertNotEquals(student, notStudent);
    }
}