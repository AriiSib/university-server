package com.khokhlov.universityserver.model.data;

import com.khokhlov.universityserver.model.Student;
import lombok.Data;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class MemoryDB {

    private Map<Long, Student> students = new ConcurrentHashMap<>();
}
