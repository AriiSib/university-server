package com.khokhlov.universityserver.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
public class Student {
    private long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String phoneNumber;

    public Student(long id, String name, String surname, LocalDate birthDate, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(name, student.name) && Objects.equals(surname, student.surname) && Objects.equals(birthDate, student.birthDate) && Objects.equals(phoneNumber, student.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, birthDate, phoneNumber);
    }
}