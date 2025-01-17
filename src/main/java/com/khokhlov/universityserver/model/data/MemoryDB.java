package com.khokhlov.universityserver.model.data;

import com.khokhlov.universityserver.model.Group;
import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.Teacher;
import com.khokhlov.universityserver.model.Timetable;
import lombok.Data;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class MemoryDB {

    private Map<Long, Student> students = new ConcurrentHashMap<>();
    private Map<Long, Teacher> teachers = new ConcurrentHashMap<>();
    private Map<Long, Group> groups = new ConcurrentHashMap<>();
    private Map<Long, Timetable> timetables = new ConcurrentHashMap<>();
}
