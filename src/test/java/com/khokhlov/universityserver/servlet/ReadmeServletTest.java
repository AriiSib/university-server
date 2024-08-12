package com.khokhlov.universityserver.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

class ReadmeServletTest {

    private ReadmeServlet readmeServlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private RequestDispatcher requestDispatcher;

    @BeforeEach
    void setUp() {
        readmeServlet = new ReadmeServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        requestDispatcher = mock(RequestDispatcher.class);
    }

    @Test
    void testDoGet_ForwardsToReadmeJsp() throws ServletException, IOException {
        when(request.getRequestDispatcher("/readme.jsp")).thenReturn(requestDispatcher);

        readmeServlet.doGet(request, response);

        verify(request).getRequestDispatcher("/readme.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGet_InvalidPathDoesNotForward() throws ServletException, IOException {
        when(request.getRequestDispatcher("/invalid.jsp")).thenReturn(null);

        verify(requestDispatcher, never()).forward(request, response);
    }
}