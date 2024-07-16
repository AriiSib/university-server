package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.exception.StudentAlreadyExistsException;
import com.khokhlov.universityserver.exception.StudentNotFoundException;
import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.StudentDTO;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class StudentService {

    private final MemoryDB DB;
    private final MappingService mappingService;
    private final AtomicLong idGenerator;

    public StudentService(MemoryDB DB, MappingService mappingService) {
        this.DB = DB;
        this.mappingService = mappingService;
        this.idGenerator = new AtomicLong(initializeIdGenerator(DB));
        idGenerator.incrementAndGet(); //starting from 1
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
        } else {
            throw new StudentAlreadyExistsException("Student already exists");
        }
    }

    public void updateStudent(long id, StudentDTO studentDTO) {
        if (DB.getStudents().containsKey(id)) {
            Student updatedStudent = mappingService.fromStudentDTO(id, studentDTO);
            if (!DB.getStudents().containsValue(updatedStudent)) {
                DB.getStudents().put(id, updatedStudent);
            } else {
                throw new StudentAlreadyExistsException("Student already exists");
            }
        } else {
            throw new StudentNotFoundException("Student with id " + id + " not found");
        }
    }

    public void deleteStudent(long id) {
        if (DB.getStudents().containsKey(id)) {
            DB.getStudents().remove(id);
        } else {
            throw new StudentNotFoundException("Student with id " + id + " not found");
        }
    }
}
