package com.khokhlov.universityserver.model.dto;

import com.khokhlov.universityserver.model.Subject;
import com.khokhlov.universityserver.validator.Validator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TeacherDTO {
    private String name;
    private String surname;
    private Long experience;
    private List<Subject> subjects;

    public void validate() {
        Validator.validateName(name);
        Validator.validateSurname(surname);
    }
}
