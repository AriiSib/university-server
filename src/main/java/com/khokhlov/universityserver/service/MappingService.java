package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.Teacher;
import com.khokhlov.universityserver.model.dto.StudentDTO;
import com.khokhlov.universityserver.model.dto.TeacherDTO;

import java.time.LocalDate;

public class MappingService {

    Student fromStudentDTO(long id, StudentDTO studentDTO) {
        return new Student(id,
                studentDTO.getName(),
                studentDTO.getSurname(),
                LocalDate.now(),
                studentDTO.getPhoneNumber());
    }

    Teacher fromTeacherDTO(long id, TeacherDTO teacherDTO) {


        return new Teacher(id,
                teacherDTO.getName(),
                teacherDTO.getSurname(),
                teacherDTO.getExperience(),
                teacherDTO.getSubjects());
    }

}
