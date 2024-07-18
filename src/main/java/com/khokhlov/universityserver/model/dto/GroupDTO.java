package com.khokhlov.universityserver.model.dto;

import com.khokhlov.universityserver.model.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {
    private long number;
    private int[] students;
    private List<Student> studentsList;
}




