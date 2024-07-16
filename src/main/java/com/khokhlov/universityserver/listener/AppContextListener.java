package com.khokhlov.universityserver.listener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.service.JsonService;
import com.khokhlov.universityserver.service.MappingService;
import com.khokhlov.universityserver.service.PropertyService;
import com.khokhlov.universityserver.service.StudentService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import static com.khokhlov.universityserver.consts.Consts.*;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        MemoryDB memoryDB = new MemoryDB();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());

        MappingService mappingService = new MappingService();

        PropertyService propertyService = new PropertyService();
        StudentService studentService = new StudentService(memoryDB, mappingService);
        JsonService jsonService = new JsonService(objectMapper);


        ctx.setAttribute(PROPERTY_SERVICE, propertyService);
        ctx.setAttribute(STUDENT_SERVICE, studentService);
        ctx.setAttribute(JSON_SERVICE, jsonService);

        ServletContextListener.super.contextInitialized(sce);
    }
}