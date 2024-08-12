package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.exception.TimetableAlreadyExistsException;
import com.khokhlov.universityserver.exception.TimetableNotFoundException;
import com.khokhlov.universityserver.model.Timetable;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.TimetableDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        if (!isDurationValid(newTimetable.getStartDateTime(), newTimetable.getEndDateTime())) {
            log.error("The duration of the timetable must be 90 minutes.");
            throw new IllegalArgumentException("The duration of the timetable must be 90 minutes.");
        }

        if (!DB.getTimetables().containsValue(newTimetable)) {
            if (!isTimeLimitExceeded(newTimetable.getGroupId(), newTimetable.getStartDateTime(), newTimetable.getEndDateTime(), true) ||
                    !isTimeLimitExceeded(newTimetable.getTeacherId(), newTimetable.getStartDateTime(), newTimetable.getEndDateTime(), false)) {
                DB.getTimetables().put(idGenerator.getAndIncrement(), newTimetable);
                log.info("Added new timetable: {}", newTimetable);
            } else {
                log.error("The total duration for the group or teacher exceeds the limit.");
                throw new IllegalArgumentException("The total duration for the group or teacher exceeds the limit.");
            }
        } else {
            log.warn("Attempt to add existing timetable: {}", newTimetable);
            throw new TimetableAlreadyExistsException("Timetable already exists");
        }
    }


    public void updateTimetable(LocalDate date, TimetableDTO timetableDTO) {
        Optional<Timetable> existingTimetableOpt = DB.getTimetables().values().stream()
                .filter(t -> t.getGroupId() == timetableDTO.getGroupId() &&
                        t.getTeacherId() == timetableDTO.getTeacherId() &&
                        t.getStartDateTime().toLocalDate().equals(date))
                .findFirst();

        if (existingTimetableOpt.isPresent()) {
            Timetable existingTimetable = existingTimetableOpt.get();

            if (!isDurationValid(timetableDTO.getStartDateTime(), timetableDTO.getEndDateTime())) {
                log.error("The duration of the timetable must be 90 minutes.");
                throw new IllegalArgumentException("The duration of the timetable must be 90 minutes.");
            }

            if (isTimeLimitExceeded(timetableDTO.getGroupId(), timetableDTO.getStartDateTime(), timetableDTO.getEndDateTime(), true)) {
                throw new IllegalArgumentException("Exceeds group time limit");
            }

            if (isTimeLimitExceeded(timetableDTO.getTeacherId(), timetableDTO.getStartDateTime(), timetableDTO.getEndDateTime(), false)) {
                throw new IllegalArgumentException("Exceeds teacher time limit");
            }

            existingTimetable.setStartDateTime(timetableDTO.getStartDateTime());
            existingTimetable.setEndDateTime(timetableDTO.getEndDateTime());
        } else {
            throw new TimetableNotFoundException("Timetable not found for the given date, group, and teacher");
        }
    }


    private boolean isTimeLimitExceeded(Long entityId, LocalDateTime newStartDateTime, LocalDateTime newEndDateTime, boolean isGroup) {
        LocalDate date = newStartDateTime.toLocalDate();
        long totalMinutes = DB.getTimetables().values().stream()
                .filter(t -> (isGroup ? t.getGroupId() : t.getTeacherId()) == entityId &&
                        t.getStartDateTime().toLocalDate().equals(date))
                .mapToLong(t -> Duration.between(t.getStartDateTime(), t.getEndDateTime()).toMinutes())
                .sum();

        totalMinutes += Duration.between(newStartDateTime, newEndDateTime).toMinutes();

        return totalMinutes > propertyService.getPropertyAsInt("max.classes", 90);
    }

    private boolean isDurationValid(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        long durationInMinutes = Duration.between(startDateTime, endDateTime).toMinutes();
        return durationInMinutes == 90;
    }

}