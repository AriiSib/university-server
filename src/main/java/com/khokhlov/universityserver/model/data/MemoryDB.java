package com.khokhlov.universityserver.model.data;

import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.Teacher;
import lombok.Data;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class MemoryDB {

    private Map<Long, Student> students = new ConcurrentHashMap<>();
    private Map<Long, Teacher> teachers = new ConcurrentHashMap<>();
}
