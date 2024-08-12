package com.khokhlov.universityserver.model.dto;

import com.khokhlov.universityserver.model.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupDTOTest {

    private final int[] arrayOfStudentsId = new int[]{1, 2, 3};
    private final GroupDTO groupDTO = new GroupDTO(1L, arrayOfStudentsId, new ArrayList<>());
    private final Student student_1 = new Student(1L, "John", "Doe", null, "+7 (123) 456-78-90");
    private final Student student_2 = new Student(2L, "Jane", "Doe", null, "+375 (29) 123-45-67");


    @Test
    void should_ReturnDefaultValues_When_InitializedWithDefaultConstructor() {
        assertNotNull(groupDTO);
        assertEquals(1, groupDTO.getNumber());
        assertArrayEquals(arrayOfStudentsId, groupDTO.getStudents());
        assertEquals(Collections.emptyList(), groupDTO.getStudentsList());
    }

    @Test
    void should_InitializeCorrectly_When_UsingParameterizedConstructor() {

        List<Student> studentsList = Arrays.asList(student_1, student_2);
        int[] studentsArray = {1, 2};

        GroupDTO groupDTO = new GroupDTO(1L, studentsArray, studentsList);

        assertEquals(1L, groupDTO.getNumber());
        assertArrayEquals(studentsArray, groupDTO.getStudents());
        assertEquals(studentsList, groupDTO.getStudentsList());
    }

    @Test
    void should_ReturnCorrectValues_When_UsingGettersAndSetters() {
        groupDTO.setNumber(2L);
        groupDTO.setStudents(new int[]{3, 4});

        List<Student> studentsList = Arrays.asList(student_1, student_2);
        groupDTO.setStudentsList(studentsList);

        assertEquals(2L, groupDTO.getNumber());
        assertArrayEquals(new int[]{3, 4}, groupDTO.getStudents());
        assertEquals(studentsList, groupDTO.getStudentsList());
    }

    @Test
    void should_BeEqual_When_ObjectsHaveSameValues() {
        List<Student> studentsList1 = Collections.singletonList(student_1);
        int[] studentsArray1 = {1};

        GroupDTO groupDTO1 = new GroupDTO(1L, studentsArray1, studentsList1);
        GroupDTO groupDTO2 = new GroupDTO(1L, studentsArray1, studentsList1);

        assertEquals(groupDTO1, groupDTO2);
        assertEquals(groupDTO1.hashCode(), groupDTO2.hashCode());
    }

    @Test
    void should_NotBeEqual_When_ObjectsHaveDifferentValues() {
        List<Student> studentsList1 = Collections.singletonList(student_1);
        int[] studentsArray1 = {1};

        GroupDTO groupDTO1 = new GroupDTO(1L, studentsArray1, studentsList1);
        GroupDTO groupDTO2 = new GroupDTO(2L, new int[]{2}, Collections.emptyList());

        assertNotEquals(groupDTO1, groupDTO2);
        assertNotEquals(groupDTO1.hashCode(), groupDTO2.hashCode());
    }


}