package com.khokhlov.universityserver.servlet;

import com.khokhlov.universityserver.exception.StudentAlreadyExistsException;
import com.khokhlov.universityserver.exception.StudentNotFoundException;
import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.dto.StudentDTO;
import com.khokhlov.universityserver.service.JsonService;
import com.khokhlov.universityserver.service.StudentService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.khokhlov.universityserver.consts.Consts.JSON_SERVICE;
import static com.khokhlov.universityserver.consts.Consts.STUDENT_SERVICE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class StudentServletTest {

    @Mock
    private StudentService studentService;

    @Mock
    private JsonService jsonService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private ServletContext servletContext;

    @InjectMocks
    private StudentServlet servlet;

    private StringWriter responseWriter;

    private Student student;
    private StudentDTO studentDTO;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(STUDENT_SERVICE)).thenReturn(studentService);
        when(servletContext.getAttribute(JSON_SERVICE)).thenReturn(jsonService);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        servlet.init(servletConfig);

        student = new Student(1L, "John", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");
        studentDTO = new StudentDTO("John", "Doe", LocalDate.of(2000, 1, 1), "+7 (123) 456-78-90");
    }

    @Test
    void should_ReturnAllStudents_When_NoFilterIsApplied() throws Exception {
        List<Student> students = List.of(student);
        when(studentService.getAllStudents()).thenReturn(students);
        when(jsonService.toJson(students)).thenReturn("[{\"id\":1}]");

        servlet.doGet(request, response);

        verify(studentService).getAllStudents();
        assertEquals("[{\"id\":1}]", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnStudentById_When_ValidIdIsProvided() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");
        when(studentService.getStudentById(1L)).thenReturn(Optional.of(student));
        when(jsonService.toJson(Optional.of(student))).thenReturn("{\"id\":1}");

        servlet.doGet(request, response);

        verify(studentService).getStudentById(1L);
        assertEquals("{\"id\":1}", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnStudentByNameAndSurname_When_NameAndSurnameAreProvided() throws Exception {
        when(request.getParameter("name")).thenReturn("John");
        when(request.getParameter("surname")).thenReturn("Doe");
        when(studentService.getStudentsByNameAndSurname("John", "Doe")).thenReturn(List.of(student));
        when(jsonService.toJson(List.of(student))).thenReturn("[{\"id\":1}]");

        servlet.doGet(request, response);

        verify(studentService).getStudentsByNameAndSurname("John", "Doe");
        assertEquals("[{\"id\":1}]", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnStudentByName_When_NameIsProvided() throws Exception {
        when(request.getParameter("name")).thenReturn("John");
        when(studentService.getStudentsByName("John")).thenReturn(List.of(student));
        when(jsonService.toJson(List.of(student))).thenReturn("[{\"id\":1}]");

        servlet.doGet(request, response);

        verify(studentService).getStudentsByName("John");
        assertEquals("[{\"id\":1}]", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnStudentBySurname_When_SurnameIsProvided() throws Exception {
        when(request.getParameter("surname")).thenReturn("Doe");
        when(studentService.getStudentsBySurname("Doe")).thenReturn(List.of(student));
        when(jsonService.toJson(List.of(student))).thenReturn("[{\"id\":1}]");

        servlet.doGet(request, response);

        verify(studentService).getStudentsBySurname("Doe");
        assertEquals("[{\"id\":1}]", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnNotFound_When_StudentIdDoesNotExist() throws Exception {
        when(request.getPathInfo()).thenReturn("/999");
        when(studentService.getStudentById(999L)).thenThrow(new StudentNotFoundException("Student not found"));

        servlet.doGet(request, response);

        verify(studentService).getStudentById(999L);
        assertTrue(responseWriter.toString().contains("Student not found"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void should_ReturnConflict_When_AddingStudentFails() throws Exception {
        String studentJson = "{\"name\":\"John\", \"surname\":\"Doe\", \"birthdate\":\"2000-01-01\", \"phoneNumber\":\"1234567890\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(studentJson)));
        when(jsonService.fromJson(studentJson, StudentDTO.class)).thenReturn(studentDTO);
        doThrow(new StudentAlreadyExistsException("Student already exists")).when(studentService).addStudent(studentDTO);

        servlet.doPost(request, response);

        assertTrue(responseWriter.toString().contains("Student already exists"));
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
    }

    @Test
    void should_ReturnCreated_When_StudentIsAddedSuccessfully() throws Exception {
        String studentJson = "{\"name\":\"John\", \"surname\":\"Doe\", \"birthdate\":\"2000-01-01\", \"phoneNumber\":\"1234567890\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(studentJson)));
        when(jsonService.fromJson(studentJson, StudentDTO.class)).thenReturn(studentDTO);

        servlet.doPost(request, response);

        verify(studentService).addStudent(studentDTO);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void should_ReturnOk_When_StudentIsUpdatedSuccessfully() throws Exception {
        String studentJson = "{\"name\":\"John\", \"surname\":\"Doe\", \"birthdate\":\"2000-01-01\", \"phoneNumber\":\"1234567890\"}";
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(studentJson)));
        when(jsonService.fromJson(studentJson, StudentDTO.class)).thenReturn(studentDTO);

        servlet.doPut(request, response);

        verify(studentService).updateStudent(1L, studentDTO);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnNotFound_When_UpdatingNonexistentStudent() throws Exception {
        String studentJson = "{\"name\":\"John\", \"surname\":\"Doe\", \"birthdate\":\"2000-01-01\", \"phoneNumber\":\"1234567890\"}";
        when(request.getPathInfo()).thenReturn("/999");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(studentJson)));
        when(jsonService.fromJson(studentJson, StudentDTO.class)).thenReturn(studentDTO);
        doThrow(new StudentNotFoundException("Student not found")).when(studentService).updateStudent(999L, studentDTO);

        servlet.doPut(request, response);

        assertTrue(responseWriter.toString().contains("Student not found"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void should_ReturnOk_When_StudentIsDeletedSuccessfully() throws Exception {
        when(request.getPathInfo()).thenReturn("/1");

        servlet.doDelete(request, response);

        verify(studentService).deleteStudent(1L);
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnNotFound_When_DeletingNonexistentStudent() throws Exception {
        when(request.getPathInfo()).thenReturn("/999");
        doThrow(new StudentNotFoundException("Student not found")).when(studentService).deleteStudent(999L);

        servlet.doDelete(request, response);

        assertTrue(responseWriter.toString().contains("Student not found"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
