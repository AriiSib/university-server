package com.khokhlov.universityserver.listener;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.khokhlov.universityserver.datetimeformatter.CustomLocalDateTimeSerializer;
import com.khokhlov.universityserver.model.data.MemoryDB;
import com.khokhlov.universityserver.service.*;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.khokhlov.universityserver.consts.Consts.*;

@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        MemoryDB memoryDB = new MemoryDB();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        objectMapper.registerModule(new Jdk8Module());
        JavaTimeModule module = new JavaTimeModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME)));
        module.addSerializer(LocalDateTime.class, new CustomLocalDateTimeSerializer());
        objectMapper.registerModule(module);

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingService mappingService = new MappingService();

        PropertyService propertyService = new PropertyService();
        StudentService studentService = new StudentService(memoryDB, mappingService);
        TeacherService teacherService = new TeacherService(memoryDB, mappingService);
        GroupService groupService = new GroupService(memoryDB, mappingService, studentService, propertyService);
        TimetableService timetableService = new TimetableService(memoryDB, mappingService, propertyService);
        JsonService jsonService = new JsonService(objectMapper);

        ctx.setAttribute(PROPERTY_SERVICE, propertyService);
        ctx.setAttribute(STUDENT_SERVICE, studentService);
        ctx.setAttribute(TEACHER_SERVICE, teacherService);
        ctx.setAttribute(GROUP_SERVICE, groupService);
        ctx.setAttribute(TIMETABLE_SERVICE, timetableService);
        ctx.setAttribute(JSON_SERVICE, jsonService);

        ServletContextListener.super.contextInitialized(sce);
    }
}