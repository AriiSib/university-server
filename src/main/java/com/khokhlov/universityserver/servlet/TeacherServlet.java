package com.khokhlov.universityserver.servlet;

import com.khokhlov.universityserver.exception.TeacherAlreadyExistsException;
import com.khokhlov.universityserver.exception.TeacherNotFoundException;
import com.khokhlov.universityserver.model.dto.SubjectDTO;
import com.khokhlov.universityserver.model.dto.TeacherDTO;
import com.khokhlov.universityserver.service.JsonService;
import com.khokhlov.universityserver.service.TeacherService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

import static com.khokhlov.universityserver.utils.HttpRequestUtils.getBody;

@Slf4j
@WebServlet(name = "teacherServlet", value = "/teachers/*")
public class TeacherServlet extends HttpServlet {

    private TeacherService teacherService;
    private JsonService jsonService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext servletContext = config.getServletContext();
        this.teacherService = (TeacherService) servletContext.getAttribute("teacherService");
        this.jsonService = (JsonService) servletContext.getAttribute("jsonService");
        log.info("TeacherServlet initialized with TeacherService and JsonService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var result = teacherService.getAllTeachers();

            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.print(jsonService.toJson(result));
            out.flush();

            log.info("GET request processed. Result: {}", result);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("Error processing GET request: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error processing request");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var teacherToSaveAsString = getBody(req);
            var teacherDTO = jsonService.fromJson(teacherToSaveAsString, TeacherDTO.class);
            teacherDTO.validate();
            teacherService.addTeacher(teacherDTO);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            log.info("POST request processed. Teacher added: {}", teacherDTO);
        } catch (TeacherAlreadyExistsException e) {
            log.error("Teacher already exists: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write("Teacher already exists: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Invalid input: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("An unexpected error occurred: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            long teacherId = Long.parseLong(pathInfo.substring(1));

            var subjectToAddAsString = getBody(req);
            var subjectDTO = jsonService.fromJson(subjectToAddAsString, SubjectDTO.class);

            teacherService.addSubjectToTeacher(teacherId, subjectDTO);
            resp.setStatus(HttpServletResponse.SC_OK);
            log.info("PUT request processed. Subject added to teacher id {}: {}", teacherId, subjectDTO);
        } catch (TeacherNotFoundException e) {
            log.error("Teacher not found: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to add subject: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Failed to add subject: " + e.getMessage());
        }
    }

}
