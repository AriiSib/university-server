package com.khokhlov.universityserver.model.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimetableDTOTest {

    @Test
    void should_ReturnDefaultValues_When_InitializedWithDefaultConstructor() {
        TimetableDTO timetableDTO = new TimetableDTO();
        assertNotNull(timetableDTO);
        assertEquals(0L, timetableDTO.getGroupId());
        assertEquals(0L, timetableDTO.getTeacherId());
        assertNull(timetableDTO.getStartDateTime());
        assertNull(timetableDTO.getEndDateTime());
    }

    @Test
    void should_InitializeCorrectly_When_UsingParameterizedConstructor() {
        long groupId = 1L;
        long teacherId = 2L;
        LocalDateTime startDateTime = LocalDateTime.of(2024, 7, 22, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 7, 22, 12, 0);

        TimetableDTO timetableDTO = new TimetableDTO(groupId, teacherId, startDateTime, endDateTime);

        assertEquals(groupId, timetableDTO.getGroupId());
        assertEquals(teacherId, timetableDTO.getTeacherId());
        assertEquals(startDateTime, timetableDTO.getStartDateTime());
        assertEquals(endDateTime, timetableDTO.getEndDateTime());
    }

    @Test
    void should_ReturnCorrectValues_When_UsingGettersAndSetters() {
        TimetableDTO timetableDTO = new TimetableDTO();
        long groupId = 3L;
        long teacherId = 4L;
        LocalDateTime startDateTime = LocalDateTime.of(2024, 8, 1, 9, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 8, 1, 11, 0);

        timetableDTO.setGroupId(groupId);
        timetableDTO.setTeacherId(teacherId);
        timetableDTO.setStartDateTime(startDateTime);
        timetableDTO.setEndDateTime(endDateTime);

        assertEquals(groupId, timetableDTO.getGroupId());
        assertEquals(teacherId, timetableDTO.getTeacherId());
        assertEquals(startDateTime, timetableDTO.getStartDateTime());
        assertEquals(endDateTime, timetableDTO.getEndDateTime());
    }

    @Test
    void should_BeEqual_When_ObjectsHaveSameValues() {
        long groupId = 5L;
        long teacherId = 6L;
        LocalDateTime startDateTime = LocalDateTime.of(2024, 7, 22, 14, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 7, 22, 16, 0);

        TimetableDTO timetableDTO1 = new TimetableDTO(groupId, teacherId, startDateTime, endDateTime);
        TimetableDTO timetableDTO2 = new TimetableDTO(groupId, teacherId, startDateTime, endDateTime);

        assertEquals(timetableDTO1, timetableDTO2);
        assertEquals(timetableDTO1.hashCode(), timetableDTO2.hashCode());
    }

    @Test
    void should_NotBeEqual_When_ObjectsHaveDifferentValues() {
        TimetableDTO timetableDTO1 = new TimetableDTO(7L, 8L, LocalDateTime.of(2024, 7, 22, 17, 0), LocalDateTime.of(2024, 7, 22, 19, 0));
        TimetableDTO timetableDTO2 = new TimetableDTO(9L, 10L, LocalDateTime.of(2024, 7, 23, 17, 0), LocalDateTime.of(2024, 7, 23, 19, 0));

        assertNotEquals(timetableDTO1, timetableDTO2);
        assertNotEquals(timetableDTO1.hashCode(), timetableDTO2.hashCode());
    }

}