package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.dto.StudentDTO;

import java.time.LocalDate;

public class MappingService {

    Student fromStudentDTO(long id, StudentDTO studentDTO) {
        return new Student(id,
                studentDTO.getName(),
                studentDTO.getFirstName(),
                studentDTO.getLastName(),
                LocalDate.now(),
                studentDTO.getPhoneNumber());
    }
}
