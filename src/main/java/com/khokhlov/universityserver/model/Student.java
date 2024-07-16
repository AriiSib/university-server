package com.khokhlov.universityserver.model;

import lombok.Data;

import java.time.LocalDate;

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
}