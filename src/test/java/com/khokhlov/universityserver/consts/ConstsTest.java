package com.khokhlov.universityserver.consts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstsTest {

    @Test
    public void testConsts() {
        assertEquals("propertyService", Consts.PROPERTY_SERVICE);
        assertEquals("studentService", Consts.STUDENT_SERVICE);
        assertEquals("teacherService", Consts.TEACHER_SERVICE);
        assertEquals("groupService", Consts.GROUP_SERVICE);
        assertEquals("timetableService", Consts.TIMETABLE_SERVICE);
        assertEquals("jsonService", Consts.JSON_SERVICE);

        assertEquals("name", Consts.NAME);
        assertEquals("surname", Consts.SURNAME);
    }
}