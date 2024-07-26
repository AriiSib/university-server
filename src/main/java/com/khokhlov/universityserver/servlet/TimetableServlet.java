package com.khokhlov.universityserver.servlet;

import com.khokhlov.universityserver.exception.TimetableNotFoundException;
import com.khokhlov.universityserver.model.dto.TimetableDTO;
import com.khokhlov.universityserver.service.JsonService;
import com.khokhlov.universityserver.service.PropertyService;
import com.khokhlov.universityserver.service.TimetableService;
import com.khokhlov.universityserver.validator.Validator;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.khokhlov.universityserver.consts.Consts.*;
import static com.khokhlov.universityserver.utils.HttpRequestUtils.getBody;

@Slf4j
@WebServlet(name = "TimetableServlet", value = "/timetable/*")
public class TimetableServlet extends HttpServlet {
    private TimetableService timetableService;
    private PropertyService propertyService;
    private JsonService jsonService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        this.timetableService = (TimetableService) context.getAttribute(TIMETABLE_SERVICE);
        this.propertyService = (PropertyService) context.getAttribute(PROPERTY_SERVICE);
        this.jsonService = (JsonService) context.getAttribute(JSON_SERVICE);
        log.info("TimetableServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var result = getPathInfo(req, resp);

            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.print(jsonService.toJson(result));
            out.flush();

            log.info("GET request processed. Result: {}", result);
        } catch (Exception e) {
            log.error("Error processing GET request: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error processing request");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String timetableToSaveAsString = getBody(req);
            TimetableDTO timetableDTO = jsonService.fromJson(timetableToSaveAsString, TimetableDTO.class);
            Validator.validateTimetable(timetableDTO.getStartDateTime(), timetableDTO.getEndDateTime(), propertyService);
            timetableService.addTimetable(timetableDTO);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            log.info("Timetable added successfully.");
        } catch (Exception e) {
            log.error("Error adding timetable: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String dateStr = req.getParameter("date");
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            String body = getBody(req);
            TimetableDTO timetableDTO = jsonService.fromJson(body, TimetableDTO.class);
            Validator.validateTimetable(timetableDTO.getStartDateTime(), timetableDTO.getEndDateTime(), propertyService);
            timetableService.updateTimetable(date, timetableDTO);
            resp.setStatus(HttpServletResponse.SC_OK);
            log.info("Timetable updated successfully.");
        } catch (Exception e) {
            log.error("Error updating timetable: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(e.getMessage());
        }
    }

    private Object getPathInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String groupNumber = req.getParameter("groupNumber");
        String studentSurname = req.getParameter("studentSurname");
        String teacherSurname = req.getParameter("teacherSurname");
        String date = req.getParameter("date");

        Object result = null;

        try {
            if (groupNumber != null) {
                result = timetableService.getTimetablesByGroupNumber(Long.parseLong(groupNumber));
            } else if (studentSurname != null) {
                result = timetableService.getTimetablesByStudentSurname(studentSurname);
            } else if (teacherSurname != null) {
                result = timetableService.getTimetablesByTeacherSurname(teacherSurname);
            } else if (date != null) {
                LocalDate dateFormat = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                result = timetableService.getTimetablesByDate(dateFormat);
            } else {
                result = timetableService.getAllTimetables();
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            log.info("Timetables retrieved successfully.");
        } catch (NumberFormatException e) {
            log.error("Invalid group number format: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid group number format: " + e.getMessage());
        } catch (TimetableNotFoundException e) {
            log.error("Timetable not found: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Timetable not found: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error retrieving timetable: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("An error occurred while retrieving the timetable: " + e.getMessage());
        }

        return result;
    }

}
