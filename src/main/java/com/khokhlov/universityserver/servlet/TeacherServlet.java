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

import java.io.*;

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
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        var result = teacherService.getAllTeachers();

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.print(jsonService.toJson(result));
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var teacherToSaveAsString = getBody(req);
            var teacherDTO = jsonService.fromJson(teacherToSaveAsString, TeacherDTO.class);
            teacherService.addTeacher(teacherDTO);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (TeacherAlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(e.getMessage());
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
            resp.setStatus(HttpServletResponse.SC_OK); // HTTP 200 OK
        } catch (TeacherNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // HTTP 404 Not Found
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // HTTP 400 Bad Request
            resp.getWriter().write("Failed to add subject: " + e.getMessage());
        }
    }


    public static String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }
}
