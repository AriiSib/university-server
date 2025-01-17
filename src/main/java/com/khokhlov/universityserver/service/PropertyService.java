package com.khokhlov.universityserver.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
@Slf4j
public class PropertyService {

    private static final String PATH_TO_PROPERTIES = "/config.properties";

    private Properties properties;

    public PropertyService() {
        loadProperties();
    }


    private void loadProperties() {
        try (InputStream inputStream = PropertyService.class.getResourceAsStream(PATH_TO_PROPERTIES)) {
            if (inputStream != null) {
                properties = new Properties();
                properties.load(inputStream);
                log.info("Properties file loaded successfully from {}", PATH_TO_PROPERTIES);
            } else {
                throw new RuntimeException("Properties file not found: " + PATH_TO_PROPERTIES);
            }
        } catch (IOException e) {
            log.error("Error loading properties file from {}", PATH_TO_PROPERTIES, e);
            throw new RuntimeException("Error loading properties file from " + PATH_TO_PROPERTIES, e);
        }
    }

    public int getPropertyAsInt(String propertyName) {
        return Integer.parseInt(properties.getProperty(propertyName));
    }

    public int getPropertyAsInt(String propertyName, int classPeriodInMinutes) {
        return getPropertyAsInt(propertyName) * classPeriodInMinutes;
    }
}