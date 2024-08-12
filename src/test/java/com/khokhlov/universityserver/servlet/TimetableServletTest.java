package com.khokhlov.universityserver.servlet;

import com.khokhlov.universityserver.exception.TimetableNotFoundException;
import com.khokhlov.universityserver.model.Timetable;
import com.khokhlov.universityserver.testutils.MockSetup;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TimetableServletTest extends MockSetup {

    private static final LocalDateTime FIXED_START_TIME = LocalDateTime.of(2024, 7, 26, 10, 0);
    private static final LocalDateTime FIXED_END_TIME = FIXED_START_TIME.plusHours(1).plusMinutes(30);

    private StringWriter responseWriter;

    private Timetable timetable;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        timetableServlet.init(servletConfig);

        timetable = new Timetable(1L, 1L, 1L, FIXED_START_TIME, FIXED_END_TIME);
    }

    @Test
    void should_ReturnAllTimetables_When_NoFilterIsApplied() throws Exception {
        List<Timetable> timetables = List.of(timetable);
        when(timetableService.getAllTimetables()).thenReturn(timetables);
        when(jsonService.toJson(timetables)).thenReturn("[{\"id\":1}]");

        timetableServlet.doGet(request, response);

        verify(timetableService).getAllTimetables();
        assertEquals("[{\"id\":1}]", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnTimetableByGroupNumber_When_GroupNumberIsProvided() throws Exception {
        when(request.getParameter("groupNumber")).thenReturn("1");
        when(timetableService.getTimetablesByGroupNumber(1L)).thenReturn(Optional.of(timetable));
        when(jsonService.toJson(Optional.of(timetable))).thenReturn("{\"id\":1}");

        timetableServlet.doGet(request, response);

        verify(timetableService).getTimetablesByGroupNumber(1L);
        assertEquals("{\"id\":1}", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnNotFound_When_GroupNumberDoesNotExist() throws Exception {
        when(request.getParameter("groupNumber")).thenReturn("999");
        when(timetableService.getTimetablesByGroupNumber(999L)).thenThrow(new TimetableNotFoundException("Timetable not found"));

        timetableServlet.doGet(request, response);

        verify(timetableService).getTimetablesByGroupNumber(999L);
        assertTrue(responseWriter.toString().contains("Timetable not found"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void should_ReturnBadRequest_When_InvalidGroupNumberFormat() throws Exception {
        when(request.getParameter("groupNumber")).thenReturn("invalid");

        timetableServlet.doGet(request, response);

        assertTrue(responseWriter.toString().contains("Invalid group number format"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void should_ReturnTimetablesByStudentSurname_When_StudentExists() throws Exception {
        when(request.getParameter("studentSurname")).thenReturn("Doe");
        when(timetableService.getTimetablesByStudentSurname("Doe")).thenReturn(List.of(timetable));
        when(jsonService.toJson(List.of(timetable))).thenReturn("[{\"id\":1}]");

        timetableServlet.doGet(request, response);

        verify(timetableService).getTimetablesByStudentSurname("Doe");
        assertEquals("[{\"id\":1}]", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnNotFound_When_StudentSurnameDoesNotExist() throws Exception {
        when(request.getParameter("studentSurname")).thenReturn("Nonexistent");
        when(timetableService.getTimetablesByStudentSurname("Nonexistent")).thenThrow(new TimetableNotFoundException("Timetable not found"));

        timetableServlet.doGet(request, response);

        verify(timetableService).getTimetablesByStudentSurname("Nonexistent");
        assertTrue(responseWriter.toString().contains("Timetable not found"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void should_ReturnTimetablesByTeacherSurname_When_TeacherExists() throws Exception {
        when(request.getParameter("teacherSurname")).thenReturn("Smith");
        when(timetableService.getTimetablesByTeacherSurname("Smith")).thenReturn(List.of(timetable));
        when(jsonService.toJson(List.of(timetable))).thenReturn("[{\"id\":1}]");

        timetableServlet.doGet(request, response);

        verify(timetableService).getTimetablesByTeacherSurname("Smith");
        assertEquals("[{\"id\":1}]", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnNotFound_When_TeacherSurnameDoesNotExist() throws Exception {
        when(request.getParameter("teacherSurname")).thenReturn("Nonexistent");
        when(timetableService.getTimetablesByTeacherSurname("Nonexistent")).thenThrow(new TimetableNotFoundException("Timetable not found"));

        timetableServlet.doGet(request, response);

        verify(timetableService).getTimetablesByTeacherSurname("Nonexistent");
        assertTrue(responseWriter.toString().contains("Timetable not found"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void should_ReturnTimetablesByDate_When_TimetableExists() throws Exception {
        when(request.getParameter("date")).thenReturn("26/07/2024");
        when(timetableService.getTimetablesByDate(LocalDate.parse("26/07/2024", DateTimeFormatter.ofPattern("dd/MM/yyyy")))).thenReturn(List.of(timetable));
        when(jsonService.toJson(List.of(timetable))).thenReturn("[{\"id\":1}]");

        timetableServlet.doGet(request, response);

        verify(timetableService).getTimetablesByDate(LocalDate.parse("26/07/2024", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        assertEquals("[{\"id\":1}]", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnNotFound_When_DateDoesNotExist() throws Exception {
        when(request.getParameter("date")).thenReturn("01/01/2025");
        when(timetableService.getTimetablesByDate(LocalDate.parse("01/01/2025", DateTimeFormatter.ofPattern("dd/MM/yyyy")))).thenThrow(new TimetableNotFoundException("Timetable not found"));

        timetableServlet.doGet(request, response);

        verify(timetableService).getTimetablesByDate(LocalDate.parse("01/01/2025", DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        assertTrue(responseWriter.toString().contains("Timetable not found"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}