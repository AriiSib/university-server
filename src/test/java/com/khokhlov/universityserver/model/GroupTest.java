package com.khokhlov.universityserver.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupTest {

    private List<Student> createStudentsList() {
        Student student1 = new Student(1L, "John", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");
        Student student2 = new Student(2L, "Jane", "Doe", LocalDate.of(2001, 2, 2), "+375 (29) 123-45-67");
        return Arrays.asList(student1, student2);
    }

    @Test
    void should_CreateGroup_When_ValidArgumentsProvided() {
        List<Student> students = createStudentsList();

        Group group = new Group(1L, 101L, students);

        assertEquals(1L, group.getId());
        assertEquals(101L, group.getNumber());
        assertEquals(students, group.getStudents());
    }

    @Test
    void testEqualsAndHashCode() {
        List<Student> students = createStudentsList();

        Group group1 = new Group(1L, 101L, students);
        Group group2 = new Group(2L, 101L, students);
        Group group3 = new Group(3L, 102L, students);

        assertEquals(group1, group2);
        assertNotEquals(group1, group3);

        assertEquals(group1.hashCode(), group2.hashCode());
        assertNotEquals(group1.hashCode(), group3.hashCode());
    }
}