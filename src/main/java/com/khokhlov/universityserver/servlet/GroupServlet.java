package com.khokhlov.universityserver.servlet;

import com.khokhlov.universityserver.model.Student;
import com.khokhlov.universityserver.model.dto.GroupDTO;
import com.khokhlov.universityserver.service.GroupService;
import com.khokhlov.universityserver.service.JsonService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.List;

import static com.khokhlov.universityserver.consts.Consts.*;

@WebServlet(name = "GroupServlet", value = "/groups/*")
public class GroupServlet extends HttpServlet {
    private GroupService groupService;
    private JsonService jsonService;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        this.groupService = (GroupService) context.getAttribute(GROUP_SERVICE);
        this.jsonService = (JsonService) context.getAttribute(JSON_SERVICE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var result = getPathInfo(req);

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.print(jsonService.toJson(result));
        out.flush();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var groupToSaveAsString = getBody(req);
            var groupDTO = jsonService.fromJson(groupToSaveAsString, GroupDTO.class);

            var students = groupService.getStudentsById(groupDTO);
            groupDTO.setStudentsList(students);
            groupService.addGroup(groupDTO);

            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(e.getMessage());
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            long groupNumber = Long.parseLong(pathInfo.substring(1));
            var studentToAddAsString = getBody(req);
            var groupDTO = jsonService.fromJson(studentToAddAsString, GroupDTO.class);
            List<Student> studentsToAdd = groupService.getStudentsById(groupDTO);

            boolean success = groupService.addStudentsToGroup(groupNumber, studentsToAdd);
            if (success) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid group number format");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(e.getMessage());
        }
    }

    private Object getPathInfo(HttpServletRequest req) throws IOException {
        String groupNumber = req.getParameter("number");
        String surname = req.getParameter(SURNAME);

        Object result = null;

        if (groupNumber != null && surname != null) {
            result = groupService.getGroupByNumberAndSurname(groupNumber, surname);
        } else if (groupNumber != null) {
            result = groupService.getGroupByNumber(groupNumber);
        } else if (surname != null) {
            result = groupService.getGroupBySurname(surname);
        } else {
            result = groupService.getAllGroups();
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