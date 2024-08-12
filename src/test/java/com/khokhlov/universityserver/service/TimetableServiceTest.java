package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.exception.TimetableAlreadyExistsException;
import com.khokhlov.universityserver.exception.TimetableNotFoundException;
import com.khokhlov.universityserver.model.Group;
import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.Teacher;
import com.khokhlov.universityserver.model.Timetable;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.TimetableDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TimetableServiceTest {

    private static final LocalDateTime FIXED_START_TIME = LocalDateTime.of(2024, 7, 25, 10, 0);
    private static final LocalDateTime FIXED_END_TIME = FIXED_START_TIME.plusHours(1).plusMinutes(30);


    private MemoryDB memoryDB;
    private MappingService mappingService;
    private PropertyService propertyService;
    private TimetableService timetableService;

    private Timetable timetable;
    private TimetableDTO timetableDTO;
    private Teacher teacher;
    private Student student;
    private Group group;

    @BeforeEach
    void setUp() {
        memoryDB = new MemoryDB();
        mappingService = mock(MappingService.class);
        propertyService = mock(PropertyService.class);
        timetableService = new TimetableService(memoryDB, mappingService, propertyService);

        teacher = new Teacher(1L, "Jane", "Smith", 5L, new ArrayList<>());
        student = new Student(1L, "John", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");
        group = new Group(1L, 101L, Arrays.asList(student));
        timetable = new Timetable(1L, 1L, 1L, FIXED_START_TIME, FIXED_END_TIME);
        timetableDTO = new TimetableDTO(1L, 1L, FIXED_START_TIME, FIXED_END_TIME);

        memoryDB.getTeachers().put(1L, teacher);
        memoryDB.getGroups().put(1L, group);

        when(mappingService.fromTimetableDTO(anyLong(), eq(timetableDTO))).thenReturn(timetable);
        when(mappingService.fromTimetableDTO(timetableDTO)).thenReturn(timetable);
        when(propertyService.getPropertyAsInt("max.classes", 90)).thenReturn(450);
    }

    @Test
    void should_AddTimetable_When_TimetableDoesNotExist() {
        timetableService.addTimetable(timetableDTO);

        assertEquals(1, memoryDB.getTimetables().size());
        assertTrue(memoryDB.getTimetables().containsValue(timetable));
    }

    @Test
    void should_ThrowException_When_AddingExistingTimetable() {
        memoryDB.getTimetables().put(1L, timetable);

        assertThrows(TimetableAlreadyExistsException.class, () -> timetableService.addTimetable(timetableDTO));
    }

    @Test
    void should_ThrowException_When_AddingTimetableWithInvalidDuration() {
        TimetableDTO invalidTimetableDTO = new TimetableDTO(1L, 1L, FIXED_START_TIME, FIXED_END_TIME);
        timetable.setEndDateTime(FIXED_END_TIME.plusMinutes(89));
        when(mappingService.fromTimetableDTO(anyLong(), eq(timetableDTO))).thenReturn(timetable);
        IllegalArgumentException thrownException = assertThrows(IllegalArgumentException.class, () -> timetableService.addTimetable(invalidTimetableDTO));
        assertEquals("The duration of the timetable must be 90 minutes.", thrownException.getMessage());
    }


    @Test
    void should_GetAllTimetables() {
        memoryDB.getTimetables().put(1L, timetable);

        Collection<Timetable> timetables = timetableService.getAllTimetables();

        assertEquals(1, timetables.size());
        assertTrue(timetables.contains(timetable));
    }

    @Test
    void should_GetTimetablesByGroupNumber_When_GroupExists() {
        memoryDB.getTimetables().put(1L, timetable);

        Optional<Timetable> retrievedTimetable = timetableService.getTimetablesByGroupNumber(101L);

        assertTrue(retrievedTimetable.isPresent());
        assertEquals(timetable, retrievedTimetable.get());
    }

    @Test
    void should_ThrowException_When_GettingTimetablesByNonexistentGroupNumber() {
        assertThrows(TimetableNotFoundException.class, () -> timetableService.getTimetablesByGroupNumber(102L));
    }

    @Test
    void should_GetTimetablesByStudentSurname_When_StudentExists() {
        memoryDB.getTimetables().put(1L, timetable);

        List<Timetable> timetables = timetableService.getTimetablesByStudentSurname("Doe");

        assertEquals(1, timetables.size());
        assertTrue(timetables.contains(timetable));
    }

    @Test
    void should_ThrowException_When_GettingTimetablesByNonexistentStudentSurname() {
        assertThrows(TimetableNotFoundException.class, () -> timetableService.getTimetablesByStudentSurname("Nonexistent"));
    }

    @Test
    void should_GetTimetablesByTeacherSurname_When_TeacherExists() {
        memoryDB.getTimetables().put(1L, timetable);

        List<Timetable> timetables = timetableService.getTimetablesByTeacherSurname("Smith");

        assertEquals(1, timetables.size());
        assertTrue(timetables.contains(timetable));
    }

    @Test
    void should_ThrowException_When_GettingTimetablesByNonexistentTeacherSurname() {
        assertThrows(TimetableNotFoundException.class, () -> timetableService.getTimetablesByTeacherSurname("Nonexistent"));
    }

    @Test
    void should_GetTimetablesByDate_When_TimetableExists() {
        memoryDB.getTimetables().put(1L, timetable);

        List<Timetable> timetables = timetableService.getTimetablesByDate(FIXED_START_TIME.toLocalDate());

        assertEquals(1, timetables.size());
        assertTrue(timetables.contains(timetable));
    }

    @Test
    void should_ThrowException_When_GettingTimetablesByNonexistentDate() {
        assertThrows(TimetableNotFoundException.class, () -> timetableService.getTimetablesByDate(LocalDate.now().plusDays(1)));
    }

    @Test
    void should_UpdateTimetable_When_TimetableExists() {
        memoryDB.getTimetables().put(1L, timetable);
        timetableDTO.setStartDateTime(FIXED_START_TIME.plusMinutes(60));
        timetableDTO.setEndDateTime(FIXED_END_TIME.plusMinutes(60));
        timetableService.updateTimetable(FIXED_START_TIME.toLocalDate(), timetableDTO);

        Timetable result = memoryDB.getTimetables().get(1L);
        assertEquals(timetableDTO.getStartDateTime(), result.getStartDateTime());
        assertEquals(timetableDTO.getEndDateTime(), result.getEndDateTime());
    }

    @Test
    void should_ThrowException_When_UpdatingNonexistentTimetable() {
        TimetableDTO updatedTimetableDTO = new TimetableDTO(1L, 1L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        assertThrows(TimetableNotFoundException.class, () -> timetableService.updateTimetable(LocalDate.now(), updatedTimetableDTO));
    }

    @Test
    void should_ThrowException_When_UpdatingTimetableWithExceedingClasses() {
        memoryDB.getTimetables().put(1L, timetable);
        TimetableDTO updatedTimetableDTO = new TimetableDTO(1L, 1L, FIXED_START_TIME.plusHours(1), FIXED_END_TIME.plusHours(12));

        assertThrows(IllegalArgumentException.class, () -> timetableService.updateTimetable(timetableDTO.getStartDateTime().toLocalDate(), updatedTimetableDTO));
    }

}