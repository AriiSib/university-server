package com.khokhlov.universityserver.model;

import lombok.Data;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(name, teacher.name) && Objects.equals(surname, teacher.surname) && Objects.equals(experience, teacher.experience) && Objects.equals(subjects, teacher.subjects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, experience, subjects);
    }
}