package com.khokhlov.universityserver.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeacherTest {

    private Teacher createTeacher_1() {
        return new Teacher(1L, "John", "Doe", 5L, Arrays.asList(Subject.MATH, Subject.PHYSICS));
    }

    private Teacher createTeacher_2() {
        return new Teacher(2L, "Jane", "Doo", 1L, Arrays.asList(Subject.ECONOMICS, Subject.CHEMISTRY));
    }

    @Test
    void should_CreateTeacher_When_ValidArgumentsProvided() {
        List<Subject> subjects = Arrays.asList(Subject.MATH, Subject.PHYSICS);
        Teacher teacher = createTeacher_1();

        assertEquals(1l, teacher.getId());
        assertEquals("John", teacher.getName());
        assertEquals("Doe", teacher.getSurname());
        assertEquals(5L, teacher.getExperience());
        assertEquals(subjects, teacher.getSubjects());
    }

    @Test
    void should_ReturnTrue_When_TeachersAreEqual() {
        Teacher teacher_1 = createTeacher_1();
        Teacher teacher_2 = createTeacher_1();

        assertEquals(teacher_1, teacher_2);
        assertEquals(teacher_1.hashCode(), teacher_2.hashCode());
    }

    @Test
    void should_ReturnFalse_When_TeachersHaveDifferentAttributes() {
        Teacher teacher_1 = createTeacher_1();
        Teacher teacher_2 = createTeacher_2();

        assertNotEquals(teacher_1, teacher_2);
        assertNotEquals(teacher_1.hashCode(), teacher_2.hashCode());
    }

    @Test
    void should_ReturnFalse_When_ObjectIsNotTeacher() {
        Teacher teacher_1 = createTeacher_1();
        Object notTeacher = new Object();

        assertNotEquals(teacher_1, notTeacher);
    }

}