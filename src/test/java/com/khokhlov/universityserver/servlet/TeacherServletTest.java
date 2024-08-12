package com.khokhlov.universityserver.servlet;

import com.khokhlov.universityserver.exception.TeacherAlreadyExistsException;
import com.khokhlov.universityserver.exception.TeacherNotFoundException;
import com.khokhlov.universityserver.model.Teacher;
import com.khokhlov.universityserver.model.Subject;
import com.khokhlov.universityserver.model.dto.SubjectDTO;
import com.khokhlov.universityserver.model.dto.TeacherDTO;
import com.khokhlov.universityserver.testutils.MockSetup;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static com.khokhlov.universityserver.model.Subject.MATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TeacherServletTest extends MockSetup {

    private StringWriter responseWriter;

    private TeacherDTO teacherDTO;
    private Teacher teacher;
    private SubjectDTO subjectDTO;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        teacherServlet.init(servletConfig);

        teacherDTO = new TeacherDTO("John", "Doe", 1L, List.of(Subject.MATH));
        teacher = teacher = new Teacher(1L, "John", "Doe", 1L, List.of(MATH));
        subjectDTO = new SubjectDTO("Math");
    }

    @Test
    void should_ReturnAllTeachers_When_NoFilterIsApplied() throws Exception {
        List<Teacher> teachers = List.of(teacher);
        when(teacherService.getAllTeachers()).thenReturn(teachers);
        when(jsonService.toJson(teachers)).thenReturn("[{\"id\":1}]");

        teacherServlet.doGet(request, response);

        verify(teacherService).getAllTeachers();
        assertEquals("[{\"id\":1}]", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }


    @Test
    void should_ReturnConflict_When_AddingTeacherFails() throws Exception {
        String teacherJson = "{\"name\":\"John\", \"surname\":\"Doe\", \"experience\":10, \"subjects\":[{\"name\":\"Math\"}]}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(teacherJson)));
        when(jsonService.fromJson(teacherJson, TeacherDTO.class)).thenReturn(teacherDTO);
        doThrow(new TeacherAlreadyExistsException("Teacher already exists")).when(teacherService).addTeacher(teacherDTO);

        teacherServlet.doPost(request, response);

        assertTrue(responseWriter.toString().contains("Teacher already exists"));
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
    }

    @Test
    void should_ReturnCreated_When_TeacherIsAddedSuccessfully() throws Exception {
        String teacherJson = "{\"name\":\"John\", \"surname\":\"Doe\", \"experience\":10, \"subjects\":[{\"name\":\"Math\"}]}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(teacherJson)));
        when(jsonService.fromJson(teacherJson, TeacherDTO.class)).thenReturn(teacherDTO);

        teacherServlet.doPost(request, response);

        verify(teacherService).addTeacher(teacherDTO);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void should_AddSubjectToTeacher_When_ValidRequest() throws Exception {
        String subjectJson = "{\"subject\":\"Science\"}";
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(subjectJson)));
        when(jsonService.fromJson(subjectJson, SubjectDTO.class)).thenReturn(subjectDTO);

        teacherServlet.doPut(request, response);

        verify(teacherService).addSubjectToTeacher(1L, subjectDTO);
        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().isEmpty());
    }

    @Test
    void should_ReturnNotFound_When_TeacherNotFoundDuringSubjectAddition() throws Exception {
        String subjectJson = "{\"subject\":\"Math\"}";
        when(request.getPathInfo()).thenReturn("/999");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(subjectJson)));
        when(jsonService.fromJson(subjectJson, SubjectDTO.class)).thenReturn(subjectDTO);
        doThrow(new TeacherNotFoundException("Teacher with id 999 not found")).when(teacherService).addSubjectToTeacher(999L, subjectDTO);

        teacherServlet.doPut(request, response);

        assertTrue(responseWriter.toString().contains("Teacher with id 999 not found"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void should_ReturnBadRequest_When_FailedToAddSubject() throws Exception {
        String subjectJson = "{\"subject\":\"Math\"}";
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(subjectJson)));
        when(jsonService.fromJson(subjectJson, SubjectDTO.class)).thenReturn(subjectDTO);
        doThrow(new RuntimeException("Failed to add subject")).when(teacherService).addSubjectToTeacher(1L, subjectDTO);

        teacherServlet.doPut(request, response);

        assertTrue(responseWriter.toString().contains("Failed to add subject"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
