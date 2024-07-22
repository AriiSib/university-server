package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.exception.TeacherAlreadyExistsException;
import com.khokhlov.universityserver.exception.TeacherNotFoundException;
import com.khokhlov.universityserver.model.Subject;
import com.khokhlov.universityserver.model.Teacher;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.SubjectDTO;
import com.khokhlov.universityserver.model.dto.TeacherDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TeacherServiceTest {

    private MemoryDB memoryDB;
    private MappingService mappingService;
    private TeacherService teacherService;

    private Teacher teacher;
    private TeacherDTO teacherDTO;

    @BeforeEach
    void setUp() {
        memoryDB = new MemoryDB();
        mappingService = mock(MappingService.class);
        teacherService = new TeacherService(memoryDB, mappingService);

        teacher = new Teacher(1L, "John", "Doe", 5L, new ArrayList<>());
        teacherDTO = new TeacherDTO("John", "Doe", 5L, new ArrayList<>());

        when(mappingService.fromTeacherDTO(anyLong(), eq(teacherDTO))).thenReturn(teacher);
    }

    @Test
    void should_AddTeacher_When_TeacherDoesNotExist() {
        teacherService.addTeacher(teacherDTO);

        assertEquals(1, memoryDB.getTeachers().size());
        assertTrue(memoryDB.getTeachers().containsValue(teacher));
    }

    @Test
    void should_ThrowException_When_AddingExistingTeacher() {
        memoryDB.getTeachers().put(1L, teacher);

        assertThrows(TeacherAlreadyExistsException.class, () -> teacherService.addTeacher(teacherDTO));
    }

    @Test
    void should_GetAllTeachers() {
        memoryDB.getTeachers().put(1L, teacher);

        Collection<Teacher> teachers = teacherService.getAllTeachers();

        assertEquals(1, teachers.size());
        assertTrue(teachers.contains(teacher));
    }

    @Test
    void should_AddSubjectToTeacher_When_TeacherExists() {
        memoryDB.getTeachers().put(1L, teacher);
        SubjectDTO subjectDTO = new SubjectDTO("Math");

        teacherService.addSubjectToTeacher(1L, subjectDTO);

        assertTrue(teacher.getSubjects().contains(Subject.fromValue("Math")));
    }

    @Test
    void should_ThrowException_When_AddingSubjectToNonexistentTeacher() {
        SubjectDTO subjectDTO = new SubjectDTO("Math");

        assertThrows(TeacherNotFoundException.class, () -> teacherService.addSubjectToTeacher(1L, subjectDTO));
    }

    @Test
    void should_ThrowException_When_AddingExistingSubjectToTeacher() {
        memoryDB.getTeachers().put(1L, teacher);
        teacher.getSubjects().add(Subject.fromValue("Math"));
        SubjectDTO subjectDTO = new SubjectDTO("Math");

        assertThrows(IllegalArgumentException.class, () -> teacherService.addSubjectToTeacher(1L, subjectDTO));
    }
}