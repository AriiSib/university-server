package com.khokhlov.universityserver.servlet;

import com.khokhlov.universityserver.exception.TimetableNotFoundException;
import com.khokhlov.universityserver.model.dto.TimetableDTO;
import com.khokhlov.universityserver.service.JsonService;
import com.khokhlov.universityserver.service.TimetableService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.khokhlov.universityserver.consts.Consts.JSON_SERVICE;
import static com.khokhlov.universityserver.consts.Consts.TIMETABLE_SERVICE;

@WebServlet(name = "TimetableServlet", value = "/timetable/*")
public class TimetableServlet extends HttpServlet {
    private TimetableService timetableService;
    private JsonService jsonService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        this.timetableService = (TimetableService) context.getAttribute(TIMETABLE_SERVICE);
        this.jsonService = (JsonService) context.getAttribute(JSON_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var result = getPathInfo(req, resp);

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.print(jsonService.toJson(result));
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String timetableToSaveAsString = getBody(req);
            TimetableDTO timetable = jsonService.fromJson(timetableToSaveAsString, TimetableDTO.class);
            timetableService.addTimetable(timetable);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
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
            timetableService.updateTimetable(date, timetableDTO);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
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
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid group number format: " + e.getMessage());
        } catch (TimetableNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Timetable not found: " + e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("An error occurred while retrieving the timetable: " + e.getMessage());
        }

        return result;
    }

    private static String getBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            char[] charBuffer = new char[128];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        }
        return stringBuilder.toString();
    }
}
