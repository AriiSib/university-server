package com.khokhlov.universityserver.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertyServiceTest {

    private final PropertyService propertyService = new PropertyService();

    @Test
    void should_LoadProperties_When_FileIsPresent() {
        assertNotNull(propertyService.getProperties());
    }

    @Test
    void should_ReturnCorrectMinClassesTime_When_PropertyIsDefined() {
        int minClassesTime = propertyService.getPropertyAsInt("min.classes", 90);
        assertEquals(90, minClassesTime, "The minimum class time should be 90 minutes (1 * 90)");
    }

    @Test
    void should_ReturnCorrectMaxClassesTime_When_PropertyIsDefined() {
        int maxClassesTime = propertyService.getPropertyAsInt("max.classes", 90);
        assertEquals(450, maxClassesTime, "The maximum class time should be 450 minutes (5 * 90)");
    }

    @Test
    void should_ReturnCorrectMinStudents_When_PropertyIsDefined() {
        int minStudents = propertyService.getPropertyAsInt("min.students");
        assertEquals(1, minStudents, "The minimum number of students should be 1");
    }

    @Test
    void should_ReturnCorrectMaxStudents_When_PropertyIsDefined() {
        int maxStudents = propertyService.getPropertyAsInt("max.students");
        assertEquals(3, maxStudents, "The maximum number of students should be 3");
    }
}