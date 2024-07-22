package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.exception.GroupAlreadyExistsException;
import com.khokhlov.universityserver.exception.GroupNotFoundException;
import com.khokhlov.universityserver.exception.StudentAlreadyExistsException;
import com.khokhlov.universityserver.exception.StudentNotFoundException;
import com.khokhlov.universityserver.model.Group;
import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.GroupDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GroupServiceTest {

    private MemoryDB memoryDB;
    private MappingService mappingService;
    private StudentService studentService;
    private PropertyService propertyService;
    private GroupService groupService;

    private Student student_1;
    private Student student_2;
    private GroupDTO groupDTO;


    @BeforeEach
    void setUp() {
        memoryDB = new MemoryDB();
        mappingService = new MappingService();
        studentService = new StudentService(memoryDB, mappingService);
        propertyService = new PropertyService();
        groupService = new GroupService(memoryDB, mappingService, studentService, propertyService);

        student_1 = new Student(1L, "John", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");
        student_2 = new Student(2L, "Jane", "Doe", LocalDate.of(1999, 2, 3), "+375 (29) 123-45-67");
        groupDTO = new GroupDTO(101L, new int[]{1, 2, 3}, Arrays.asList());
    }

    @Test
    void should_AddGroup_When_GroupDoesNotExist() {
        groupService.addGroup(groupDTO);

        assertNotNull(memoryDB.getGroups().get(1L));
    }

    @Test
    void should_ThrowException_When_AddingExistingGroup() {
        groupService.addGroup(groupDTO);

        assertThrows(GroupAlreadyExistsException.class, () -> groupService.addGroup(groupDTO));
    }

    @Test
    void should_GetStudentsById_When_ValidDataProvided() {
        memoryDB.getStudents().put(1L, student_1);
        memoryDB.getStudents().put(2L, student_2);

        groupDTO = new GroupDTO(101L, new int[]{1, 2}, Arrays.asList(student_1, student_2));

        List<Student> students = groupService.getStudentsById(groupDTO);

        assertEquals(2, students.size());
        assertTrue(students.contains(student_1));
        assertTrue(students.contains(student_2));
    }

    @Test
    void should_ThrowException_When_StudentNotFound() {
        groupDTO = new GroupDTO(101L, new int[]{999}, Arrays.asList());

        assertThrows(StudentNotFoundException.class, () -> groupService.getStudentsById(groupDTO));
    }

    @Test
    void should_GetGroupByNumberAndSurname_When_ValidDataProvided() {
        Group group = new Group(1L, 101L, Arrays.asList(student_1));
        memoryDB.getGroups().put(1L, group);

        Optional<Group> result = groupService.getGroupByNumberAndSurname("101", "Doe");

        assertTrue(result.isPresent());
        assertEquals(group, result.get());
    }

    @Test
    void should_ThrowException_When_GroupNotFoundByNumber() {
        Optional<Group> result = groupService.getGroupByNumber("999");

        assertTrue(result.isEmpty());
    }

    @Test
    void should_ThrowException_When_GroupNotFoundBySurname() {
        Optional<Group> result = groupService.getGroupBySurname("Nonexistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void should_ThrowException_When_GroupNotFoundByNumberInAddStudents() {
        assertThrows(GroupNotFoundException.class, () -> groupService.addStudentsToGroup(999L, Arrays.asList(student_1)));
    }

    @Test
    void should_AddStudentsToGroup_When_ValidDataProvided() {
        Group group = new Group(1L, 101L, new ArrayList<>());
        memoryDB.getGroups().put(1L, group);

        boolean result = groupService.addStudentsToGroup(101L, Arrays.asList(student_1));

        assertTrue(result);
        assertTrue(memoryDB.getGroups().get(1L).getStudents().contains(student_1));
    }

    @Test
    void should_ThrowException_When_AddingTooManyStudents() {
        int maxStudents = propertyService.getMaxStudents();

        List<Student> initialStudents = new ArrayList<>();
        for (long i = 1; i <= maxStudents; i++) {
            initialStudents.add(new Student(i, "Student" + i, "Surname" + i, LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90"));
        }
        Group group = new Group(1L, 101L, new ArrayList<>(initialStudents));
        memoryDB.getGroups().put(1L, group);

        List<Student> studentsToAdd = Arrays.asList(new Student(maxStudents + 1, "New", "Student", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90"));

        assertThrows(IllegalArgumentException.class, () -> groupService.addStudentsToGroup(101L, studentsToAdd));
    }

    @Test
    void should_ThrowException_When_NotEnoughStudentsInGroup() {
        int minStudents = propertyService.getMinStudents();
        int[] studentIds = new int[minStudents - 1]; // меньше минимального числа студентов
        for (int i = 0; i < studentIds.length; i++) {
            studentIds[i] = i + 1;
        }
        groupDTO = new GroupDTO(101L, studentIds, Arrays.asList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> groupService.getStudentsById(groupDTO));
        assertEquals("Group must have at least " + minStudents + " students.", exception.getMessage());
    }

    @Test
    void should_ThrowException_When_TooManyStudentsInGroup() {
        int maxStudents = propertyService.getMaxStudents();
        int[] studentIds = new int[maxStudents + 1]; // больше максимального числа студентов
        for (int i = 0; i < studentIds.length; i++) {
            studentIds[i] = i + 1;
        }
        groupDTO = new GroupDTO(101L, studentIds, Arrays.asList());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> groupService.getStudentsById(groupDTO));
        assertEquals("Group cannot have more than " + maxStudents + " students.", exception.getMessage());
    }


    @Test
    void should_ThrowException_When_StudentAlreadyExists() {
        Group group = new Group(1L, 101L, Arrays.asList(student_1));
        memoryDB.getGroups().put(1L, group);

        assertThrows(StudentAlreadyExistsException.class, () -> groupService.addStudentsToGroup(101L, Arrays.asList(student_1)));
    }
}