package com.khokhlov.universityserver.servlet;

import com.khokhlov.universityserver.exception.GroupAlreadyExistsException;
import com.khokhlov.universityserver.exception.GroupNotFoundException;
import com.khokhlov.universityserver.model.Group;
import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.dto.GroupDTO;
import com.khokhlov.universityserver.service.GroupService;
import com.khokhlov.universityserver.service.JsonService;
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
import java.util.List;
import java.util.Optional;

import static com.khokhlov.universityserver.consts.Consts.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GroupServletTest {

    @Mock
    private GroupService groupService;

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
    private GroupServlet servlet;

    private StringWriter responseWriter;

    private Group group;
    private Student student;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(GROUP_SERVICE)).thenReturn(groupService);
        when(servletContext.getAttribute(JSON_SERVICE)).thenReturn(jsonService);

        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        servlet.init(servletConfig);

        student = new Student(1L, "John", "Doe", null, null);
        group = new Group(1L, 1, List.of(student));
    }

    @Test
    void should_ReturnAllGroups_When_NoFilterIsApplied() throws Exception {
        List<Group> groups = List.of(group);
        when(groupService.getAllGroups()).thenReturn(groups);
        when(jsonService.toJson(groups)).thenReturn("[{\"id\":1}]");

        servlet.doGet(request, response);

        verify(groupService).getAllGroups();
        assertEquals("[{\"id\":1}]", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnGroupByNumber_When_GroupNumberIsProvided() throws Exception {
        when(request.getParameter("number")).thenReturn("1");
        when(groupService.getGroupByNumber("1")).thenReturn(Optional.of(group));
        when(jsonService.toJson(Optional.of(group))).thenReturn("{\"id\":1}");

        servlet.doGet(request, response);

        verify(groupService).getGroupByNumber("1");
        assertEquals("{\"id\":1}", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnGroupBySurname_When_SurnameIsProvided() throws Exception {
        when(request.getParameter(SURNAME)).thenReturn("Doe");
        when(groupService.getGroupBySurname("Doe")).thenReturn(Optional.of(group));
        when(jsonService.toJson(Optional.of(group))).thenReturn("{\"id\":1}");

        servlet.doGet(request, response);

        verify(groupService).getGroupBySurname("Doe");
        assertEquals("{\"id\":1}", responseWriter.toString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnNotFound_When_GroupNumberDoesNotExist() throws Exception {
        when(request.getParameter("number")).thenReturn("999");
        when(groupService.getGroupByNumber("999")).thenThrow(new GroupNotFoundException("Group not found"));

        servlet.doGet(request, response);

        verify(groupService).getGroupByNumber("999");
        assertTrue(responseWriter.toString().contains("Group not found"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void should_ReturnNotFound_When_SurnameDoesNotExist() throws Exception {
        when(request.getParameter(SURNAME)).thenReturn("Nonexistent");
        when(groupService.getGroupBySurname("Nonexistent")).thenThrow(new GroupNotFoundException("Group not found"));

        servlet.doGet(request, response);

        verify(groupService).getGroupBySurname("Nonexistent");
        assertTrue(responseWriter.toString().contains("Group not found"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void should_ReturnBadRequest_When_InvalidGroupNumberFormat() throws Exception {
        when(request.getPathInfo()).thenReturn("/invalid");

        servlet.doPut(request, response);

        assertTrue(responseWriter.toString().contains("Invalid group number format"));
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void should_ReturnConflict_When_AddingGroupFails() throws Exception {
        String groupJson = "{\"number\":1, \"students\":[1]}";
        GroupDTO groupDTO = new GroupDTO(1L, new int[]{1}, List.of(student));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(groupJson)));
        when(jsonService.fromJson(groupJson, GroupDTO.class)).thenReturn(groupDTO);
        when(groupService.getStudentsById(groupDTO)).thenReturn(List.of(student));
        doThrow(new GroupAlreadyExistsException("Group already exists")).when(groupService).addGroup(groupDTO);

        servlet.doPost(request, response);

        assertTrue(responseWriter.toString().contains("Group already exists"));
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
    }

    @Test
    void should_ReturnCreated_When_GroupIsAddedSuccessfully() throws Exception {
        String groupJson = "{\"number\":1, \"students\":[1]}";
        GroupDTO groupDTO = new GroupDTO(1L, new int[]{1}, List.of(student));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(groupJson)));
        when(jsonService.fromJson(groupJson, GroupDTO.class)).thenReturn(groupDTO);
        when(groupService.getStudentsById(groupDTO)).thenReturn(List.of(student));

        servlet.doPost(request, response);

        verify(groupService).addGroup(groupDTO);
        verify(response).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void should_ReturnOk_When_StudentsAreAddedSuccessfully() throws Exception {
        String groupNumber = "1";
        String studentsJson = "[1, 2, 3]";
        GroupDTO groupDTO = new GroupDTO();
        when(request.getPathInfo()).thenReturn("/" + groupNumber);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(studentsJson)));
        when(jsonService.fromJson(studentsJson, GroupDTO.class)).thenReturn(groupDTO);

        Student student1 = new Student(1L, "John", "Doe", null, null);
        Student student2 = new Student(2L, "Jane", "Doe", null, null);
        Student student3 = new Student(3L, "Jack", "Doe", null, null);
        when(groupService.getStudentsById(groupDTO)).thenReturn(List.of(student1, student2, student3));

        boolean success = true;
        when(groupService.addStudentsToGroup(Long.parseLong(groupNumber), List.of(student1, student2, student3)))
                .thenReturn(success);

        servlet.doPut(request, response);

        verify(groupService).addStudentsToGroup(Long.parseLong(groupNumber), List.of(student1, student2, student3));
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void should_ReturnConflict_When_StudentsCannotBeAdded() throws Exception {
        String groupNumber = "1";
        String studentsJson = "[1, 2, 3]";
        GroupDTO groupDTO = new GroupDTO();
        when(request.getPathInfo()).thenReturn("/" + groupNumber);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(studentsJson)));
        when(jsonService.fromJson(studentsJson, GroupDTO.class)).thenReturn(groupDTO);

        Student student1 = new Student(1L, "John", "Doe", null, null);
        Student student2 = new Student(2L, "Jane", "Doe", null, null);
        Student student3 = new Student(3L, "Jack", "Doe", null, null);
        when(groupService.getStudentsById(groupDTO)).thenReturn(List.of(student1, student2, student3));

        when(groupService.addStudentsToGroup(Long.parseLong(groupNumber), List.of(student1, student2, student3)))
                .thenThrow(new GroupNotFoundException("Group not found"));

        servlet.doPut(request, response);

        assertTrue(responseWriter.toString().contains("Group not found"));
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
    }

}
