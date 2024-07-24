package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.exception.GroupAlreadyExistsException;
import com.khokhlov.universityserver.exception.GroupNotFoundException;
import com.khokhlov.universityserver.exception.StudentAlreadyExistsException;
import com.khokhlov.universityserver.exception.StudentNotFoundException;
import com.khokhlov.universityserver.model.Group;
import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.GroupDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class GroupService {
    private final MemoryDB DB;
    private final MappingService mappingService;
    private final StudentService studentService;
    private final PropertyService propertyService;
    private final AtomicLong idGenerator;

    public GroupService(MemoryDB DB, MappingService mappingService, StudentService studentService, PropertyService propertyService) {
        this.DB = DB;
        this.mappingService = mappingService;
        this.idGenerator = new AtomicLong(initializeIdGenerator(DB));
        this.studentService = studentService;
        this.propertyService = propertyService;
        idGenerator.incrementAndGet();
        log.info("GroupService initialized with ID generator starting from {}", idGenerator.get());
    }

    private long initializeIdGenerator(MemoryDB DB) {
        return DB.getGroups().keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
    }

    public Collection<Group> getAllGroups() {
        return DB.getGroups().values();
    }

    public void addGroup(GroupDTO groupDTO) {
        Group group = mappingService.fromGroupDTO(idGenerator.get(), groupDTO);
        if (!DB.getGroups().containsValue(group)) {
            DB.getGroups().put(idGenerator.getAndIncrement(), group);
            log.info("Added new group with ID {}: {}", idGenerator.get() - 1, group);
        } else {
            log.warn("Attempted to add an existing group: {}", group);
            throw new GroupAlreadyExistsException("Group already exists");
        }
    }

    public List<Student> getStudentsById(GroupDTO groupDTO) {
        List<Student> students = new ArrayList<>();

        int minStudents = propertyService.getMinStudents();
        int maxStudents = propertyService.getMaxStudents();
        int studentCount = groupDTO.getStudents().length;

        if (studentCount < minStudents) {
            log.error("Group must have at least {} students, but {} were provided.", minStudents, studentCount);
            throw new IllegalArgumentException("Group must have at least " + minStudents + " students.");
        } else if (studentCount > maxStudents) {
            log.error("Group cannot have more than {} students (see config.properties), but {} were provided.", maxStudents, studentCount);
            throw new IllegalArgumentException("Group cannot have more than " + maxStudents + " students.");
        }

        for (int studentId : groupDTO.getStudents()) {
            studentService.getStudentById(studentId)
                    .ifPresentOrElse(
                            students::add,
                            () -> {
                                log.error("Student with id {} not found", studentId);
                                throw new StudentNotFoundException("Student with id " + studentId + " not found");
                            }
                    );
        }

        return students;
    }

    public Optional<Group> getGroupByNumberAndSurname(String groupNumber, String surname) {
        long number = Long.parseLong(groupNumber);
        return getAllGroups().stream()
                .filter(group -> group.getNumber() == number &&
                        group.getStudents().stream()
                                .anyMatch(student -> student.getSurname().equalsIgnoreCase(surname)))
                .findFirst();
    }

    public Optional<Group> getGroupByNumber(String groupNumber) {
        long number = Long.parseLong(groupNumber);
        return getAllGroups().stream()
                .filter(group -> group.getNumber() == number)
                .findFirst();
    }

    public Optional<Group> getGroupBySurname(String surname) {
        return getAllGroups().stream()
                .filter(group -> group.getStudents().stream()
                        .anyMatch(student -> student.getSurname().equalsIgnoreCase(surname)))
                .findFirst();
    }

    public boolean addStudentsToGroup(long groupNumber, List<Student> studentsToAdd) {
        Group group = getGroupByNumber(String.valueOf(groupNumber))
                .orElseThrow(() -> {
                    log.error("Group not found with number {}", groupNumber);
                    return new GroupNotFoundException("Group not found with number " + groupNumber);
                });

        List<Student> currentStudents = group.getStudents();
        int maxStudents = propertyService.getMaxStudents();

        if (currentStudents.size() + studentsToAdd.size() > maxStudents) {
            log.error("Adding students will exceed the maximum number of {} students.", maxStudents);
            throw new IllegalArgumentException("Adding students will exceed the maximum number of " + maxStudents + " students.");
        }

        for (Student student : studentsToAdd) {
            if (!currentStudents.contains(student)) {
                currentStudents.add(student);
                log.info("Added student with ID {} to group {}", student.getId(), groupNumber);
            } else {
                log.warn("Student with ID {} already exists in the group {}", student.getId(), groupNumber);
                throw new StudentAlreadyExistsException("Student with id " + student.getId() + " already exists");
            }
        }
        return true;
    }
}