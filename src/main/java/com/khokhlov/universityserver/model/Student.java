package com.khokhlov.universityserver.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Student {
    private long id;
    private String name;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String phoneNumber;

    public Student(long id, String name, String firstName, String lastName, LocalDate birthDate, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }
}