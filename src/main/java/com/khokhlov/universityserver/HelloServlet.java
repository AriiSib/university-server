package com.khokhlov.universityserver;

import java.io.*;

import com.khokhlov.universityserver.service.PropertyService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import static com.khokhlov.universityserver.consts.Consts.PROPERTY_SERVICE;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {

    private PropertyService propertyService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext context = config.getServletContext();
        this.propertyService = (PropertyService) context.getAttribute(PROPERTY_SERVICE);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + propertyService.getProperties().getProperty("max.students") + "</h1>");
        out.println("</body></html>");
    }

    public void destroy() {
    }
}