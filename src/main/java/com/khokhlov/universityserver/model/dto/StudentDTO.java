package com.khokhlov.universityserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class StudentDTO {
   private String name;
   private String surname;
   private String phoneNumber;
}
