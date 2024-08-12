package com.khokhlov.universityserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Group {
    private long id;
    @EqualsAndHashCode.Include
    private long number;
    private List<Student> students;
}