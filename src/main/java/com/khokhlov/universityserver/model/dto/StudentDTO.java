package com.khokhlov.universityserver.model.dto;

import com.khokhlov.universityserver.validator.Validator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentDTO {
    private String name;
    private String surname;
    private LocalDate birthdate;
    private String phoneNumber;


    public void validate() {
        Validator.validateName(name);
        Validator.validateSurname(surname);
        Validator.validatePhone(phoneNumber);
    }
}
