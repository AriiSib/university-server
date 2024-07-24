package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.exception.TimetableAlreadyExistsException;
import com.khokhlov.universityserver.exception.TimetableNotFoundException;
import com.khokhlov.universityserver.model.Timetable;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.TimetableDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
public class TimetableService {
    private final MemoryDB DB;
    private final MappingService mappingService;
    private final PropertyService propertyService;
    private final AtomicLong idGenerator;

    public TimetableService(MemoryDB DB, MappingService mappingService, PropertyService propertyService) {
        this.DB = DB;
        this.mappingService = mappingService;
        this.propertyService = propertyService;
        this.idGenerator = new AtomicLong(initializeIdGenerator(DB));
        idGenerator.incrementAndGet();
    }

    private long initializeIdGenerator(MemoryDB DB) {
        return DB.getTeachers().keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
    }

    public Collection<Timetable> getAllTimetables() {
        Collection<Timetable> timetables = DB.getTimetables().values();
        log.info("Retrieved {} timetables", timetables.size());
        return timetables;
    }

    public Optional<Timetable> getTimetablesByGroupNumber(long groupNumber) {
        if (DB.getGroups().values().stream().noneMatch(group -> group.getNumber() == groupNumber)) {
            log.error("Group not found for the given timetable: group number {}", groupNumber);
            throw new TimetableNotFoundException("Group not found for the given timetable ");
        }

        Optional<Timetable> timetable = DB.getTimetables().values().stream()
                .filter(t -> DB.getGroups().containsKey(t.getGroupId()) &&
                        DB.getGroups().get(t.getGroupId()).getNumber() == groupNumber)
                .findFirst();

        if (timetable.isPresent()) {
            log.info("Found timetable for group number {}: {}", groupNumber, timetable.get());
        } else {
            log.warn("No timetable found for group number {}", groupNumber);
        }

        return timetable;
    }

    public List<Timetable> getTimetablesByStudentSurname(String studentSurname) {
        List<Timetable> timetables = DB.getTimetables().values().stream()
                .filter(timetable -> DB.getGroups().get(timetable.getGroupId())
                        .getStudents().stream()
                        .anyMatch(student -> student.getSurname().equalsIgnoreCase(studentSurname)))
                .collect(Collectors.toList());

        if (timetables.isEmpty()) {
            log.warn("No timetables found for student surname {}", studentSurname);
            throw new TimetableNotFoundException("No timetables found for the given student surname ");
        }

        log.info("Found {} timetables for student surname {}", timetables.size(), studentSurname);
        return timetables;
    }

    public List<Timetable> getTimetablesByTeacherSurname(String teacherSurname) {
        List<Timetable> timetables = DB.getTimetables().values().stream()
                .filter(timetable -> DB.getTeachers().get(timetable.getTeacherId())
                        .getSurname().equalsIgnoreCase(teacherSurname))
                .collect(Collectors.toList());

        if (timetables.isEmpty()) {
            log.warn("No timetables found for teacher surname {}", teacherSurname);
            throw new TimetableNotFoundException("No timetables found for the given teacher surname ");
        }

        log.info("Found {} timetables for teacher surname {}", timetables.size(), teacherSurname);
        return timetables;
    }

    public List<Timetable> getTimetablesByDate(LocalDate date) {
        List<Timetable> timetables = DB.getTimetables().values().stream()
                .filter(timetable -> timetable.getStartDateTime().toLocalDate().equals(date))
                .collect(Collectors.toList());

        if (timetables.isEmpty()) {
            log.warn("No timetables found for date {}", date);
            throw new TimetableNotFoundException("No timetables found for the given date ");
        }

        log.info("Found {} timetables for date {}", timetables.size(), date);
        return timetables;
    }

    public void addTimetable(TimetableDTO timetableDTO) {
        Timetable newTimetable = mappingService.fromTimetableDTO(idGenerator.get(), timetableDTO);
        if (!DB.getTimetables().containsValue(newTimetable)) {
            if (isScheduleValid(newTimetable, newTimetable.getTeacherId())) {
                DB.getTimetables().put(idGenerator.getAndIncrement(), newTimetable);
                log.info("Added new timetable: {}", newTimetable);
            } else {
                log.error("The total number of classes for the day exceeds the limit of {}", propertyService.getMaxClassesTime());
                throw new IllegalArgumentException("The total number of classes for the day exceeds the limit of " + propertyService.getMaxClassesTime());
            }
        } else {
            log.warn("Attempt to add existing timetable: {}", newTimetable);
            throw new TimetableAlreadyExistsException("Timetable already exists");
        }
    }

    public void updateTimetable(LocalDate date, TimetableDTO updatedTimetableDTO) {
        Timetable updatedTimetable = mappingService.fromTimetableDTO(updatedTimetableDTO);
        List<Timetable> timetables = getTimetablesByDate(date);

        if (timetables.isEmpty()) {
            log.error("Timetable not found for the given date: {}", date);
            throw new TimetableNotFoundException("Timetable not found for the given date");
        }

        Timetable timetable = timetables.get(0);

        timetable.setGroupId(updatedTimetable.getGroupId());
        timetable.setTeacherId(updatedTimetable.getTeacherId());
        timetable.setStartDateTime(updatedTimetable.getStartDateTime());
        timetable.setEndDateTime(updatedTimetable.getEndDateTime());

        if (!isScheduleValid(timetable, timetable.getTeacherId())) {
            log.error("The total number of classes for the day exceeds the limit of {}", propertyService.getMaxClassesTime());
            throw new IllegalArgumentException("The total number of classes for the day exceeds the limit of "
                    + propertyService.getMaxClassesTime());
        }

        log.info("Updated timetable: {}", timetable);
    }

    private boolean isScheduleValid(Timetable newTimetable, long teacherId) {
        LocalDate date = newTimetable.getStartDateTime().toLocalDate();
        long totalClasses = DB.getTimetables().values().stream()
                .filter(timetable -> timetable.getStartDateTime().toLocalDate().equals(date)
                        && timetable.getTeacherId() == teacherId)
                .count();

        totalClasses += 1;
        return totalClasses <= propertyService.getMaxClassesTime();
    }
}