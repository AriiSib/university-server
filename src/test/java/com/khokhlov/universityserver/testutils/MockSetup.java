package com.khokhlov.universityserver.testutils;

import com.khokhlov.universityserver.service.*;
import com.khokhlov.universityserver.servlet.GroupServlet;
import com.khokhlov.universityserver.servlet.StudentServlet;
import com.khokhlov.universityserver.servlet.TeacherServlet;
import com.khokhlov.universityserver.servlet.TimetableServlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.khokhlov.universityserver.consts.Consts.*;
import static org.mockito.Mockito.when;

public class MockSetup {
    @Mock
    public GroupService groupService;

    @Mock
    public JsonService jsonService;

    @Mock
    public HttpServletRequest request;

    @Mock
    public HttpServletResponse response;

    @Mock
    public ServletConfig servletConfig;

    @Mock
    public ServletContext servletContext;

    @Mock
    public StudentService studentService;

    @Mock
    public TeacherService teacherService;

    @Mock
    public TimetableService timetableService;

    @InjectMocks
    public TimetableServlet timetableServlet;

    @InjectMocks
    public TeacherServlet teacherServlet;

    @InjectMocks
    public GroupServlet servletGroup;

    @InjectMocks
    public StudentServlet studentServlet;

    protected void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute(GROUP_SERVICE)).thenReturn(groupService);
        when(servletContext.getAttribute(JSON_SERVICE)).thenReturn(jsonService);
        when(servletContext.getAttribute(STUDENT_SERVICE)).thenReturn(studentService);
        when(servletContext.getAttribute(TEACHER_SERVICE)).thenReturn(teacherService);
        when(servletContext.getAttribute(TIMETABLE_SERVICE)).thenReturn(timetableService);
    }
}