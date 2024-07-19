package com.khokhlov.universityserver.service;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class PropertyService {

    private Properties properties;

    public PropertyService() {
        loadProperties();
    }

    private static final String PATH_TO_PROPERTIES = "/config.properties";

    private void loadProperties() {
        try (InputStream inputStream = PropertyService.class.getResourceAsStream(PATH_TO_PROPERTIES)) {
            if (inputStream != null) {
                properties = new Properties();
                properties.load(inputStream);
            } else {
                throw new RuntimeException("Properties file not found: " + PATH_TO_PROPERTIES);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading properties file from " + PATH_TO_PROPERTIES, e);
        }
    }
}
