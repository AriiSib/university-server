package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.exception.GroupAlreadyExistsException;
import com.khokhlov.universityserver.exception.StudentAlreadyExistsException;
import com.khokhlov.universityserver.exception.StudentNotFoundException;
import com.khokhlov.universityserver.model.Group;
import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.GroupDTO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

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
        } else {
            throw new GroupAlreadyExistsException("Group already exists");
        }
    }

    public List<Student> getStudentsById(GroupDTO groupDTO) {
        List<Student> students = new ArrayList<>();
        if (groupDTO.getStudents().length >= Integer.parseInt(propertyService.getProperties().getProperty("min.students"))) {
            for (int studentId : groupDTO.getStudents()) {
                studentService.getStudentById(studentId).ifPresentOrElse(students::add,
                        () -> {
                            throw new StudentNotFoundException("Student with id " + studentId + " not found");
                        });
            }
        }
        return students;
    }

    public Optional<Group> getGroupByNumberAndSurname(String groupNumber, String surname) {
        //REGEX
        long number = Long.parseLong(groupNumber);
        return getAllGroups().stream()
                .filter(group -> group.getNumber() == number &&
                        group.getStudents().stream()
                                .anyMatch(student -> student.getSurname().equalsIgnoreCase(surname)))
                .findFirst();
    }

    public Optional<Group> getGroupByNumber(String groupNumber) {
        //REGEX
        long number = Long.parseLong(groupNumber);
        return getAllGroups().stream()
                .filter(group -> group.getNumber() == number)
                .findFirst();
    }

    public Optional<Group> getGroupBySurname(String surname) {
        //REGEX
        return getAllGroups().stream()
                .filter(group -> group.getStudents().stream()
                        .anyMatch(student -> student.getSurname().equalsIgnoreCase(surname)))
                .findFirst();
    }

    public boolean addStudentsToGroup(long groupNumber, List<Student> studentsToAdd) {
        Optional<Group> groupOptional = getAllGroups().stream()
                .filter(group -> group.getNumber() == groupNumber)
                .findFirst();

        if (groupOptional.isPresent()) {
            Group group = groupOptional.get();
            List<Student> currentStudents = group.getStudents();

            for (Student student : studentsToAdd) {
                if (!currentStudents.contains(student)) {
                    currentStudents.add(student);
                } else {
                    throw new StudentAlreadyExistsException("Student with id " + student.getId() + " already exists");
                }
            }
            return true;
        } else {
            return false;
        }
    }


}

