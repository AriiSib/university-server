package com.khokhlov.universityserver.validator;

import com.khokhlov.universityserver.service.PropertyService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @Test
    void should_ThrowIllegalArgumentException_When_NameIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateName("invalid_name!"));
        assertDoesNotThrow(() -> Validator.validateName("Valid Name"));
    }

    @Test
    void should_ThrowIllegalArgumentException_When_SurnameIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateSurname("invalid_surname!"));
        assertDoesNotThrow(() -> Validator.validateSurname("Valid Surname"));
    }

    @Test
    void should_ThrowIllegalArgumentException_When_PhoneNumberIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validatePhone("12345"));
        assertDoesNotThrow(() -> Validator.validatePhone("+7 (123) 456-78-90"));
    }

    @Test
    void should_ThrowIllegalArgumentException_When_EndDateTimeIsBeforeStartDateTime() {
        LocalDateTime startDateTime = LocalDateTime.of(2024, 7, 21, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 7, 21, 9, 0);

        assertThrows(IllegalArgumentException.class, () ->
                Validator.validateTimetable(startDateTime, endDateTime, Mockito.mock(PropertyService.class))
        );
    }

    @Test
    void should_ThrowIllegalArgumentException_When_DurationExceedsMaxAllowed() {
        PropertyService propertyService = Mockito.mock(PropertyService.class);
        Mockito.when(propertyService.getPropertyAsInt("max.classes", 90)).thenReturn(60);
        Mockito.when(propertyService.getPropertyAsInt("min.classes", 90)).thenReturn(30);

        LocalDateTime startDateTime = LocalDateTime.of(2024, 7, 21, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 7, 21, 11, 1);

        assertThrows(IllegalArgumentException.class, () ->
                Validator.validateTimetable(startDateTime, endDateTime, propertyService)
        );
    }

    @Test
    void should_ThrowIllegalArgumentException_When_DurationIsLessThanMinAllowed() {
        PropertyService propertyService = Mockito.mock(PropertyService.class);
        Mockito.when(propertyService.getPropertyAsInt("max.classes", 90)).thenReturn(120);
        Mockito.when(propertyService.getPropertyAsInt("min.classes", 90)).thenReturn(60);

        LocalDateTime startDateTime = LocalDateTime.of(2024, 7, 21, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 7, 21, 10, 59);

        assertThrows(IllegalArgumentException.class, () ->
                Validator.validateTimetable(startDateTime, endDateTime, propertyService)
        );
    }

    @Test
    void should_NotThrowException_When_TimetableDurationIsValid() {
        PropertyService propertyService = Mockito.mock(PropertyService.class);
        Mockito.when(propertyService.getPropertyAsInt("max.classes", 90)).thenReturn(120);
        Mockito.when(propertyService.getPropertyAsInt("min.classes", 90)).thenReturn(60);

        LocalDateTime startDateTime = LocalDateTime.of(2024, 7, 21, 10, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 7, 21, 11, 0);

        assertDoesNotThrow(() ->
                Validator.validateTimetable(startDateTime, endDateTime, propertyService)
        );
    }
}