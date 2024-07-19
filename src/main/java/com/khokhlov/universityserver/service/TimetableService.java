package com.khokhlov.universityserver.service;

import com.khokhlov.universityserver.exception.TimetableAlreadyExistsException;
import com.khokhlov.universityserver.exception.TimetableNotFoundException;
import com.khokhlov.universityserver.model.Timetable;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.model.dto.TimetableDTO;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TimetableService {
    private final MemoryDB DB;
    private final MappingService mappingService;
    private final AtomicLong idGenerator;

    public TimetableService(MemoryDB DB, MappingService mappingService) {
        this.DB = DB;
        this.mappingService = mappingService;
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
        return DB.getTimetables().values();
    }

    public Optional<Timetable> getTimetablesByGroupNumber(long groupNumber) {
        if (DB.getGroups().values().stream().noneMatch(group -> group.getNumber() == groupNumber)) {
            throw new TimetableNotFoundException("Group not found for the given timetable ");
        }

        return DB.getTimetables().values().stream()
                .filter(timetable -> DB.getGroups().containsKey(timetable.getGroupId()) &&
                        DB.getGroups().get(timetable.getGroupId()).getNumber() == groupNumber)
                .findFirst();
    }

    public List<Timetable> getTimetablesByStudentSurname(String studentSurname) {
        List<Timetable> timetables = DB.getTimetables().values().stream()
                .filter(timetable -> DB.getGroups().get(timetable.getGroupId())
                        .getStudents().stream()
                        .anyMatch(student -> student.getSurname().equalsIgnoreCase(studentSurname)))
                .collect(Collectors.toList());

        if (timetables.isEmpty()) {
            throw new TimetableNotFoundException("No timetables found for the given student surname ");
        }

        return timetables;
    }

    public List<Timetable> getTimetablesByTeacherSurname(String teacherSurname) {
        List<Timetable> timetables = DB.getTimetables().values().stream()
                .filter(timetable -> DB.getTeachers().get(timetable.getTeacherId())
                        .getSurname().equalsIgnoreCase(teacherSurname))
                .collect(Collectors.toList());

        if (timetables.isEmpty()) {
            throw new TimetableNotFoundException("No timetables found for the given teacher surname ");
        }

        return timetables;
    }

    public List<Timetable> getTimetablesByDate(LocalDate date) {
        List<Timetable> timetables = DB.getTimetables().values().stream()
                .filter(timetable -> timetable.getStartDateTime().toLocalDate().equals(date))
                .collect(Collectors.toList());

        if (timetables.isEmpty()) {
            throw new TimetableNotFoundException("No timetables found for the given date ");
        }

        return timetables;
    }

    public void addTimetable(TimetableDTO timetableDTO) {
        Timetable newTimetable = mappingService.fromTimetableDTO(idGenerator.get(), timetableDTO);
        if (!DB.getTimetables().containsValue(newTimetable)) {
            DB.getTimetables().put(idGenerator.getAndIncrement(), newTimetable);
        } else {
            throw new TimetableAlreadyExistsException("Timetable already exists");
        }
    }


    public void updateTimetable(LocalDate date, TimetableDTO updatedTimetableDTO) {
        Timetable updatedTimetable = mappingService.fromTimetableDTO(updatedTimetableDTO);
        List<Timetable> timetables = getTimetablesByDate(date);

        if (timetables.isEmpty()) {
            throw new TimetableNotFoundException("Timetable not found for the given date");
        }

        Timetable timetable = timetables.getFirst();

        timetable.setGroupId(updatedTimetable.getGroupId());
        timetable.setTeacherId(updatedTimetable.getTeacherId());
        timetable.setStartDateTime(updatedTimetable.getStartDateTime());
        timetable.setEndDateTime(updatedTimetable.getEndDateTime());
    }
}
