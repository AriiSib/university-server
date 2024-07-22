package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.exception.StudentAlreadyExistsException;
import com.khokhlov.universityserver.exception.StudentNotFoundException;
import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.StudentDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
public class StudentService {

    private final MemoryDB DB;
    private final MappingService mappingService;
    private final AtomicLong idGenerator;

    public StudentService(MemoryDB DB, MappingService mappingService) {
        this.DB = DB;
        this.mappingService = mappingService;
        this.idGenerator = new AtomicLong(initializeIdGenerator(DB));
        idGenerator.incrementAndGet();
        log.info("StudentService initialized with ID generator starting from {}", idGenerator.get());
    }

    private long initializeIdGenerator(MemoryDB DB) {
        return DB.getStudents().keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
    }

    public Collection<Student> getAllStudents() {
        return DB.getStudents().values();
    }

    public Optional<Student> getStudentById(long studentId) {
        return Optional.ofNullable(DB.getStudents().get(studentId));
    }

    public Collection<Student> getStudentsByNameAndSurname(String name, String surname) {
        return getAllStudents().stream()
                .filter(student -> name.equalsIgnoreCase(student.getName()) && surname.equalsIgnoreCase(student.getSurname()))
                .collect(Collectors.toList());
    }

    public Collection<Student> getStudentsByName(String name) {
        return getAllStudents().stream()
                .filter(student -> student.getName()
                        .equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    public Collection<Student> getStudentsBySurname(String surname) {
        return getAllStudents().stream()
                .filter(student -> student.getSurname()
                        .equalsIgnoreCase(surname))
                .collect(Collectors.toList());
    }

    public void addStudent(StudentDTO studentDTO) {
        Student student = mappingService.fromStudentDTO(idGenerator.get(), studentDTO);
        if (!DB.getStudents().containsValue(student)) {
            DB.getStudents().put(idGenerator.getAndIncrement(), student);
            log.info("Added student with ID {}: {}", idGenerator.get() - 1, student);
        } else {
            log.warn("Attempted to add an existing student: {}", student);
            throw new StudentAlreadyExistsException("Student already exists");
        }
    }

    public void updateStudent(long studentId, StudentDTO studentDTO) {
        if (DB.getStudents().containsKey(studentId)) {
            Student updatedStudent = mappingService.fromStudentDTO(studentId, studentDTO);
            if (!DB.getStudents().containsValue(updatedStudent)) {
                DB.getStudents().put(studentId, updatedStudent);
                log.info("Updated student with ID {}: {}", studentId, updatedStudent);
            } else {
                log.warn("Attempted to update with an existing student: {}", updatedStudent);
                throw new StudentAlreadyExistsException("Student already exists");
            }
        } else {
            log.error("Attempted to update non-existing student with ID {}", studentId);
            throw new StudentNotFoundException("Student with id " + studentId + " not found");
        }
    }

    public void deleteStudent(long id) {
        if (DB.getStudents().containsKey(id)) {
            DB.getStudents().remove(id);
            log.info("Deleted student with ID {}", id);
        } else {
            log.error("Attempted to delete non-existing student with ID {}", id);
            throw new StudentNotFoundException("Student with id " + id + " not found");
        }
    }
}
