package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.model.Group;
import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.Teacher;
import com.khokhlov.universityserver.model.Timetable;
import com.khokhlov.universityserver.model.dto.GroupDTO;
import com.khokhlov.universityserver.model.dto.StudentDTO;
import com.khokhlov.universityserver.model.dto.TeacherDTO;
import com.khokhlov.universityserver.model.dto.TimetableDTO;


public class MappingService {

    Student fromStudentDTO(long id, StudentDTO studentDTO) {
        return new Student(id,
                studentDTO.getName(),
                studentDTO.getSurname(),
                studentDTO.getBirthdate(),
                studentDTO.getPhoneNumber());
    }

    Teacher fromTeacherDTO(long id, TeacherDTO teacherDTO) {
        return new Teacher(id,
                teacherDTO.getName(),
                teacherDTO.getSurname(),
                teacherDTO.getExperience(),
                teacherDTO.getSubjects());
    }

    Group fromGroupDTO(long id, GroupDTO groupDTO) {
        return new Group(id,
                groupDTO.getNumber(),
                groupDTO.getStudentsList());
    }


    Timetable fromTimetableDTO(long id, TimetableDTO timetableDTO) {
        return new Timetable(id,
                timetableDTO.getGroupId(),
                timetableDTO.getTeacherId(),
                timetableDTO.getStartDateTime(),
                timetableDTO.getEndDateTime());
    }

    public Timetable fromTimetableDTO(TimetableDTO timetableDTO) {
        return new Timetable(0,
                timetableDTO.getGroupId(),
                timetableDTO.getTeacherId(),
                timetableDTO.getStartDateTime(),
                timetableDTO.getEndDateTime());
    }
}