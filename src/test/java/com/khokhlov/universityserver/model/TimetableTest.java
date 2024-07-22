package com.khokhlov.universityserver.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimetableTest {

    private Timetable createTimetable_1() {
        return new Timetable(
                1L,
                100L,
                200L,
                LocalDateTime.of(2024, 7, 21, 10, 0),
                LocalDateTime.of(2024, 7, 21, 12, 0)
        );
    }

    private Timetable createTimetable_2() {
        return new Timetable(
                2L,
                101L,
                201L,
                LocalDateTime.of(2024, 7, 22, 14, 0),
                LocalDateTime.of(2024, 7, 22, 16, 0)
        );
    }

    @Test
    void should_CreateTimetable_When_ValidArgumentsProvided() {
        Timetable timetable = createTimetable_1();

        assertEquals(1L, timetable.getId());
        assertEquals(100L, timetable.getGroupId());
        assertEquals(200L, timetable.getTeacherId());
        assertEquals(LocalDateTime.of(2024, 7, 21, 10, 0), timetable.getStartDateTime());
        assertEquals(LocalDateTime.of(2024, 7, 21, 12, 0), timetable.getEndDateTime());
    }

    @Test
    void should_ReturnTrue_When_TimetablesAreEqual() {
        Timetable timetable1 = createTimetable_1();
        Timetable timetable2 = createTimetable_1();

        assertEquals(timetable1, timetable2);
        assertEquals(timetable1.hashCode(), timetable2.hashCode());
    }

    @Test
    void should_ReturnFalse_When_TimetablesHaveDifferentAttributes() {
        Timetable timetable1 = createTimetable_1();
        Timetable timetable2 = createTimetable_2();

        assertNotEquals(timetable1, timetable2);
        assertNotEquals(timetable1.hashCode(), timetable2.hashCode());
    }

    @Test
    void should_ReturnFalse_When_ObjectIsNotTimetable() {
        Timetable timetable = createTimetable_1();
        Object notTimetable = new Object();

        assertNotEquals(timetable, notTimetable);
        assertNotEquals(timetable, null);
    }

}