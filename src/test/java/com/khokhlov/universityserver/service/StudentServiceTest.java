package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.exception.StudentAlreadyExistsException;
import com.khokhlov.universityserver.exception.StudentNotFoundException;
import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.StudentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StudentServiceTest {

    private MemoryDB memoryDB;
    private MappingService mappingService;
    private StudentService studentService;

    private Student student;
    private StudentDTO studentDTO;

    @BeforeEach
    void setUp() {
        memoryDB = new MemoryDB();
        mappingService = mock(MappingService.class);
        studentService = new StudentService(memoryDB, mappingService);

        student = new Student(1L, "John", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");
        studentDTO = new StudentDTO("John", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");

        when(mappingService.fromStudentDTO(anyLong(), eq(studentDTO))).thenReturn(student);
    }

    @Test
    void should_AddStudent_When_StudentDoesNotExist() {
        studentService.addStudent(studentDTO);

        assertEquals(1, memoryDB.getStudents().size());
        assertTrue(memoryDB.getStudents().containsValue(student));
    }

    @Test
    void should_ThrowException_When_AddingExistingStudent() {
        memoryDB.getStudents().put(1L, student);

        assertThrows(StudentAlreadyExistsException.class, () -> studentService.addStudent(studentDTO));
    }

    @Test
    void should_GetAllStudents() {
        memoryDB.getStudents().put(1L, student);

        Collection<Student> students = studentService.getAllStudents();

        assertEquals(1, students.size());
        assertTrue(students.contains(student));
    }

    @Test
    void should_GetStudentById_When_StudentExists() {
        memoryDB.getStudents().put(1L, student);

        Optional<Student> result = studentService.getStudentById(1L);

        assertTrue(result.isPresent());
        assertEquals(student, result.get());
    }

    @Test
    void should_ReturnEmptyOptional_When_StudentDoesNotExist() {
        Optional<Student> result = studentService.getStudentById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void should_GetStudentsByNameAndSurname() {
        memoryDB.getStudents().put(1L, student);

        Collection<Student> result = studentService.getStudentsByNameAndSurname("John", "Doe");

        assertEquals(1, result.size());
        assertTrue(result.contains(student));
    }

    @Test
    void should_GetStudentsByName() {
        memoryDB.getStudents().put(1L, student);

        Collection<Student> result = studentService.getStudentsByName("John");

        assertEquals(1, result.size());
        assertTrue(result.contains(student));
    }

    @Test
    void should_GetStudentsBySurname() {
        memoryDB.getStudents().put(1L, student);

        Collection<Student> result = studentService.getStudentsBySurname("Doe");

        assertEquals(1, result.size());
        assertTrue(result.contains(student));
    }

    @Test
    void should_UpdateStudent_When_StudentExists() {
        memoryDB.getStudents().put(1L, student);
        Student updatedStudent = new Student(1L, "Jane", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");
        StudentDTO updatedStudentDTO = new StudentDTO("Jane", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");

        when(mappingService.fromStudentDTO(1L, updatedStudentDTO)).thenReturn(updatedStudent);

        studentService.updateStudent(1L, updatedStudentDTO);

        assertEquals(updatedStudent, memoryDB.getStudents().get(1L));
    }

    @Test
    void should_ThrowException_When_UpdatingNonexistentStudent() {
        StudentDTO updatedStudentDTO = new StudentDTO("Jane", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");

        assertThrows(StudentNotFoundException.class, () -> studentService.updateStudent(1L, updatedStudentDTO));
    }

    @Test
    void should_DeleteStudent_When_StudentExists() {
        memoryDB.getStudents().put(1L, student);

        studentService.deleteStudent(1L);

        assertFalse(memoryDB.getStudents().containsKey(1L));
    }

    @Test
    void should_ThrowException_When_DeletingNonexistentStudent() {
        assertThrows(StudentNotFoundException.class, () -> studentService.deleteStudent(1L));
    }

}