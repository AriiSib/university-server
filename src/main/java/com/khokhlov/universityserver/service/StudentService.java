package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.StudentDTO;

import java.util.Collection;
import java.util.List;
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

    public void addStudent(StudentDTO student) {
        long newId = idGenerator.incrementAndGet();
        DB.getStudents().put(newId, mappingService.fromStudentDTO(newId, student));
    }
}
