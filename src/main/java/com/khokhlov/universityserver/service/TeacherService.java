package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.exception.TeacherAlreadyExistsException;
import com.khokhlov.universityserver.exception.TeacherNotFoundException;
import com.khokhlov.universityserver.model.Subject;
import com.khokhlov.universityserver.model.Teacher;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.SubjectDTO;
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
        idGenerator.incrementAndGet();
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
        Teacher teacher = mappingService.fromTeacherDTO(idGenerator.get(), teacherDTO);
        if (!DB.getTeachers().containsValue(teacher)) {
            DB.getTeachers().put(idGenerator.getAndIncrement(), teacher);
        } else {
            throw new TeacherAlreadyExistsException("Teacher already exists");
        }
    }

    public void addSubjectToTeacher(long teacherId, SubjectDTO subjectDTO) {
        if (DB.getTeachers().containsKey(teacherId)) {
            Teacher teacher = DB.getTeachers().get(teacherId);
            Subject newSubject = Subject.fromValue(subjectDTO.getSubject());
            if (!teacher.getSubjects().contains(newSubject)) {
                teacher.getSubjects().add(newSubject);
            } else {
                throw new IllegalArgumentException("Subject " + subjectDTO.getSubject() + " already exists for teacher");
            }
        } else {
            throw new TeacherNotFoundException("Teacher with id " + teacherId + " not found");
        }
    }

}
