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

import java.io.*;

import static com.khokhlov.universityserver.consts.Consts.*;


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
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Object result = getPathInfo(req);

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.print(jsonService.toJson(result));
        out.flush();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var studentToSaveAsString = getBody(req);
            var studentDTO = jsonService.fromJson(studentToSaveAsString, StudentDTO.class);
            studentService.addStudent(studentDTO);
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (StudentAlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(e.getMessage());
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            long studentId = Long.parseLong(pathInfo.substring(1));

            var studentToUpdateAsString = getBody(req);
            var studentDTO = jsonService.fromJson(studentToUpdateAsString, StudentDTO.class);

            studentService.updateStudent(studentId, studentDTO);
            resp.setStatus(HttpServletResponse.SC_OK); // HTTP 200 OK
        } catch (StudentNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // HTTP 404 Not Found
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // HTTP 400 Bad Request
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
        } catch (StudentNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(e.getMessage());
        }
    }

    private Object getPathInfo(HttpServletRequest req) throws IOException {
        String pathInfo = req.getPathInfo();
        String name = req.getParameter(NAME);
        String surname = req.getParameter(SURNAME);
        Object result = null;

        if (pathInfo != null && pathInfo.startsWith("/")) {
            String[] pathPart = pathInfo.substring(1).split("/");
            if (pathPart.length == 1 && pathPart[0].matches("\\d+")) {
                long id = Integer.parseInt(pathPart[0]);
                result = studentService.getStudentById(id);
            }
        } else if (name != null && surname != null) {
            result = studentService.getStudentsByNameAndSurname(name, surname);
        } else if (name != null) {
            result = studentService.getStudentsByName(name);
        } else if (surname != null) {
            result = studentService.getStudentsBySurname(surname);
        } else {
            result = studentService.getAllStudents();
        }
        return result;
    }

    public static String getBody(HttpServletRequest request) throws IOException {

        String body = "";
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
