package com.khokhlov.universityserver.model;

import lombok.Data;

import java.util.List;

@Data
public class Teacher {
    private long id;
    private String name;
    private String surname;
    private Long experience;
    private List<Subject> subjects;

    public Teacher(long id, String name, String surname, Long experience, List<Subject> subjects) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.experience = experience;
        this.subjects = subjects;
    }
}