package com.khokhlov.universityserver.servlet;

import com.khokhlov.universityserver.exception.GroupNotFoundException;
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
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;

import static com.khokhlov.universityserver.consts.Consts.*;
import static com.khokhlov.universityserver.utils.HttpRequestUtils.getBody;

@Slf4j
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
        log.info("GroupServlet initialized");
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
        } catch (GroupNotFoundException e) {
            log.error("Error processing GET request: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Group not found");
        } catch (Exception e) {
            log.error("Error processing GET request: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Error processing request");
        }
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
            log.info("POST request processed. Group added: {}", groupDTO);
        } catch (Exception e) {
            log.error("Error processing POST request: {}", e.getMessage(), e);
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
                log.info("PUT request processed. Students added to group number {}: {}", groupNumber, studentsToAdd);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                log.warn("Group number {} not found while adding students", groupNumber);
            }
        } catch (NumberFormatException e) {
            log.error("Invalid group number format: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid group number format");
        } catch (Exception e) {
            log.error("Error processing PUT request: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(e.getMessage());
        }
    }

    private Object getPathInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String groupNumber = req.getParameter("number");
        String surname = req.getParameter(SURNAME);

        Object result = null;

        try {
            if (groupNumber != null && surname != null) {
                result = groupService.getGroupByNumberAndSurname(groupNumber, surname);
                log.debug("getPathInfo: Retrieved group by number {} and surname {}", groupNumber, surname);
            } else if (groupNumber != null) {
                result = groupService.getGroupByNumber(groupNumber);
                log.debug("getPathInfo: Retrieved group by number {}", groupNumber);
            } else if (surname != null) {
                result = groupService.getGroupBySurname(surname);
                log.debug("getPathInfo: Retrieved group by surname {}", surname);
            } else {
                result = groupService.getAllGroups();
                log.debug("getPathInfo: Retrieved all groups");
            }
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (NumberFormatException e) {
            log.error("Invalid number format: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid number format", e);
        } catch (GroupNotFoundException e) {
            log.error("Group not found: {}", e.getMessage(), e);
            throw new GroupNotFoundException("Group not found");
        } catch (Exception e) {
            log.error("Error retrieving group information: {}", e.getMessage(), e);
            throw new RuntimeException("Error retrieving group information", e);
        }

        return result;
    }

}