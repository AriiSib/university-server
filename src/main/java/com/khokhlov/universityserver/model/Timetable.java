package com.khokhlov.universityserver.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;


@Data
public class Timetable {
    private long id;
    private long groupId;
    private long teacherId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public Timetable(long id, long groupId, long teacherId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.id = id;
        this.groupId = groupId;
        this.teacherId = teacherId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timetable timetable = (Timetable) o;
        return groupId == timetable.groupId && teacherId == timetable.teacherId
                && Objects.equals(startDateTime, timetable.startDateTime)
                && Objects.equals(endDateTime, timetable.endDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, teacherId, startDateTime, endDateTime);
    }
}