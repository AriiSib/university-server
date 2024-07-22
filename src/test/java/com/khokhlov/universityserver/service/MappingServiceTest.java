package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.model.*;
import com.khokhlov.universityserver.model.dto.GroupDTO;
import com.khokhlov.universityserver.model.dto.StudentDTO;
import com.khokhlov.universityserver.model.dto.TeacherDTO;
import com.khokhlov.universityserver.model.dto.TimetableDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MappingServiceTest {

    private final MappingService mappingService = new MappingService();

    @Test
    void should_MapStudentDTOToStudent_When_ValidDTOProvided() {
        StudentDTO studentDTO = new StudentDTO("John", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");
        Student expectedStudent = new Student(1L, "John", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");

        Student student = mappingService.fromStudentDTO(1L, studentDTO);

        assertEquals(expectedStudent, student);
    }

    @Test
    void should_MapTeacherDTOToTeacher_When_ValidDTOProvided() {
        List<Subject> subjects = Arrays.asList(Subject.MATH, Subject.PHYSICS);
        TeacherDTO teacherDTO = new TeacherDTO("Jane", "Doe", 5L, subjects);
        Teacher expectedTeacher = new Teacher(1L, "Jane", "Doe", 5L, subjects);

        Teacher teacher = mappingService.fromTeacherDTO(1L, teacherDTO);

        assertEquals(expectedTeacher, teacher);
    }

    @Test
    void should_MapGroupDTOToGroup_When_ValidDTOProvided() {
        List<Student> students = Arrays.asList(
                new Student(1L, "John", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90"),
                new Student(2L, "Jane", "Doe", LocalDate.of(2001, 2, 2), "+7 (123) 456-78-91")
        );
        GroupDTO groupDTO = new GroupDTO(101L, new int[]{1, 2}, students);
        Group expectedGroup = new Group(1L, 101L, students);

        Group group = mappingService.fromGroupDTO(1L, groupDTO);

        assertEquals(expectedGroup, group);
    }

    @Test
    void should_MapTimetableDTOToTimetable_When_ValidDTOProvided() {
        TimetableDTO timetableDTO = new TimetableDTO(1L, 2L, LocalDateTime.of(2024, 7, 21, 10, 0), LocalDateTime.of(2024, 7, 21, 12, 0));
        Timetable expectedTimetable = new Timetable(1L, 1L, 2L, LocalDateTime.of(2024, 7, 21, 10, 0), LocalDateTime.of(2024, 7, 21, 12, 0));

        Timetable timetable = mappingService.fromTimetableDTO(timetableDTO);

        assertEquals(expectedTimetable, timetable);
    }

    @Test
    void should_MapTimetableDTOToTimetable_WithDefaultId_When_NoIdProvided() {
        TimetableDTO timetableDTO = new TimetableDTO(1L, 2L, LocalDateTime.of(2024, 7, 21, 10, 0), LocalDateTime.of(2024, 7, 21, 12, 0));
        Timetable expectedTimetable = new Timetable(0, 1L, 2L, LocalDateTime.of(2024, 7, 21, 10, 0), LocalDateTime.of(2024, 7, 21, 12, 0));

        Timetable timetable = mappingService.fromTimetableDTO(timetableDTO);

        assertEquals(expectedTimetable, timetable);
    }
}