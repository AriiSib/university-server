package com.khokhlov.universityserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableDTO {
    private long groupId;
    private long teacherId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}