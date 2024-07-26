package com.khokhlov.universityserver.servlet;

import com.khokhlov.universityserver.exception.StudentAlreadyExistsException;
import com.khokhlov.universityserver.exception.StudentNotFoundException;
import com.khokhlov.universityserver.model.dto.StudentDTO;
import com.khokhlov.universityserver.service.JsonService;
import com.khokhlov.universityserver.service.StudentService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

import static com.khokhlov.universityserver.consts.Consts.*;
import static com.khokhlov.universityserver.utils.HttpRequestUtils.getBody;

@Slf4j
@WebServlet(name = "studentService", value = "/students/*")
public class StudentServlet extends HttpServlet {

    private StudentService studentService;
    private JsonService jsonService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        this.studentService = (StudentService) context.getAttribute(STUDENT_SERVICE);
        this.jsonService = (JsonService) context.getAttribute(JSON_SERVICE);
        log.info("StudentServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Object result = getPathInfo(req, resp);

            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.print(jsonService.toJson(result));
            out.flush();

            log.info("GET request processed. Result: {}", result);
        } catch (StudentNotFoundException e) {
            log.info("Student not found: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Student not found");
        } catch (NumberFormatException e) {
            log.info("Number format exception: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Number format exception");
        } catch (Exception e) {
            log.error("Error processing GET request: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error processing request");
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var studentToSaveAsString = getBody(req);
            var studentDTO = jsonService.fromJson(studentToSaveAsString, StudentDTO.class);
            studentDTO.validate();
            studentService.addStudent(studentDTO);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            log.info("POST request processed. Student added: {}", studentDTO);
        } catch (StudentAlreadyExistsException e) {
            log.error("Student already exists: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(e.getMessage());
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
            long studentId = Long.parseLong(pathInfo.substring(1));

            var studentToUpdateAsString = getBody(req);
            var studentDTO = jsonService.fromJson(studentToUpdateAsString, StudentDTO.class);
            studentDTO.validate();

            studentService.updateStudent(studentId, studentDTO);
            resp.setStatus(HttpServletResponse.SC_OK);
            log.info("PUT request processed. Student updated: {}", studentDTO);
        } catch (StudentNotFoundException e) {
            log.error("Student not found: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to update student: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Failed to update student: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            long id = Long.parseLong(pathInfo.substring(1));
            studentService.deleteStudent(id);
            resp.setStatus(HttpServletResponse.SC_OK);
            log.info("DELETE request processed. Student with id {} deleted", id);
        } catch (StudentNotFoundException e) {
            log.error("Student not found: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(e.getMessage());
        }
    }

    private Object getPathInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String name = req.getParameter(NAME);
        String surname = req.getParameter(SURNAME);
        Object result = null;

        try {
            if (pathInfo != null && pathInfo.startsWith("/")) {
                String[] pathPart = pathInfo.substring(1).split("/");
                if (pathPart.length == 1 && pathPart[0].matches("\\d+")) {
                    long id = Integer.parseInt(pathPart[0]);
                    result = studentService.getStudentById(id);
                    log.debug("getPathInfo: Retrieved student by id {}", id);
                }
            } else if (name != null && surname != null) {
                result = studentService.getStudentsByNameAndSurname(name, surname);
                log.debug("getPathInfo: Retrieved students by name {} and surname {}", name, surname);
            } else if (name != null) {
                result = studentService.getStudentsByName(name);
                log.debug("getPathInfo: Retrieved students by name {}", name);
            } else if (surname != null) {
                result = studentService.getStudentsBySurname(surname);
                log.debug("getPathInfo: Retrieved students by surname {}", surname);
            } else {
                result = studentService.getAllStudents();
                log.debug("getPathInfo: Retrieved all students");
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (StudentNotFoundException e) {
            log.error("Student not found: {}", e.getMessage());
            throw new StudentNotFoundException("Student not found");
        } catch (NumberFormatException e) {
            log.error("Invalid student ID format: {}", e.getMessage());
            throw new NumberFormatException("Invalid student ID format");
        } catch (Exception e) {
            log.error("Error occurred while retrieving student: {}", e.getMessage());
            throw new IOException("Error occurred while retrieving student", e);
        }

        return result;
    }

}
