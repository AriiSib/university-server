package com.khokhlov.universityserver.validator;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class Validator {

    private static final String NAME_REGEX = "^[A-ZА-Я][a-zа-яё]*([ '-][A-ZА-Я][a-zа-яё]*)*$";
    private static final String PHONE_REGEX = "^(?:\\+7|8)\\s\\(\\d{3}\\)\\s\\d{3}-\\d{2}-\\d{2}$|^(?:\\+375|8)\\s\\(\\d{2}\\)\\s\\d{3}-\\d{2}-\\d{2}$";
    private static final String DATE_REGEX = "^(19|20)\\d\\d-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";

    public static void validateName(String name) {
        if (!Pattern.matches(NAME_REGEX, name)) {
            throw new IllegalArgumentException("Invalid name");
        }
    }

    public static void validateSurname(String surname) {
        if (!Pattern.matches(NAME_REGEX, surname)) {
            throw new IllegalArgumentException("Invalid surname");
        }
    }

    public static void validatePhone(String phone) {
        if (!Pattern.matches(PHONE_REGEX, phone)) {
            throw new IllegalArgumentException("Invalid phone number");
        }
    }

    public static void validateTimetable(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        long durationMinutes = java.time.Duration.between(startDateTime, endDateTime).toMinutes();
        if (durationMinutes > 450) {
            throw new IllegalArgumentException("Total class duration exceeds maximum allowed");
        }
    }
}