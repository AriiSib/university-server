package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.Teacher;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.StudentDTO;
import com.khokhlov.universityserver.model.dto.TeacherDTO;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

public class TeacherService {
    private final MemoryDB DB;
    private final MappingService mappingService;
    private final AtomicLong idGenerator;

    public TeacherService(MemoryDB DB, MappingService mappingService) {
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

    public Collection<Teacher> getAllTeachers() {
        return DB.getTeachers().values();
    }

    public void addTeacher(TeacherDTO teacherDTO) {
        long newId = idGenerator.incrementAndGet();
        DB.getTeachers().put(newId, mappingService.fromTeacherDTO(newId, teacherDTO));
    }

}
