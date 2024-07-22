package com.khokhlov.universityserver.listener;

import com.khokhlov.universityserver.service.*;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import static com.khokhlov.universityserver.consts.Consts.*;

class AppContextListenerTest {

    private ServletContextEvent servletContextEvent;
    private ServletContext servletContext;

    @BeforeEach
    void setUp() {
        servletContextEvent = Mockito.mock(ServletContextEvent.class);
        servletContext = Mockito.mock(ServletContext.class);
        Mockito.when(servletContextEvent.getServletContext()).thenReturn(servletContext);
    }

    @Test
    void contextInitialized_shouldInitializeContextAttributes() {
        AppContextListener listener = new AppContextListener();

        listener.contextInitialized(servletContextEvent);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        verify(servletContext).setAttribute(eq(PROPERTY_SERVICE), captor.capture());
        assertNotNull(captor.getValue());

        verify(servletContext).setAttribute(eq(STUDENT_SERVICE), captor.capture());
        assertNotNull(captor.getValue());

        verify(servletContext).setAttribute(eq(TEACHER_SERVICE), captor.capture());
        assertNotNull(captor.getValue());

        verify(servletContext).setAttribute(eq(GROUP_SERVICE), captor.capture());
        assertNotNull(captor.getValue());

        verify(servletContext).setAttribute(eq(TIMETABLE_SERVICE), captor.capture());
        assertNotNull(captor.getValue());

        verify(servletContext).setAttribute(eq(JSON_SERVICE), captor.capture());
        assertNotNull(captor.getValue());
    }
}