package com.khokhlov.universityserver.model;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class Group {
    private long id;
    private long number;
    private List<Student> students;

    public Group(long id, long number, List<Student> students) {
        this.id = id;
        this.number = number;
        this.students = students;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return number == group.number;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(number);
    }
}