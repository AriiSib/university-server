package com.khokhlov.universityserver.servlet;

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
import lombok.RequiredArgsConstructor;

import java.io.*;

import static com.khokhlov.universityserver.consts.Consts.*;


@WebServlet(name = "studentService", value = "/students/*")
@RequiredArgsConstructor
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

        Object result = getPathInfo(req, resp);

        System.out.println(req.getPathInfo());
        System.out.println(req.getAttribute("name"));
        System.out.println(req.getParameter("name"));
        System.out.println(req.getRequestURI());

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.print(jsonService.toJson(result));
        out.flush();
    }

    private Object getPathInfo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // /{id} или /name/{name} или /surname/{surname}
        String name = req.getParameter("name");
        String surname = req.getParameter("surname");
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var studentToSaveAsString = getBody(req);
        var studentDTO = jsonService.fromJson(studentToSaveAsString, StudentDTO.class);
        studentService.addStudent(studentDTO);
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
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
