package com.khokhlov.universityserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khokhlov.universityserver.exception.InvalidJsonException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
public class JsonService {

    private final ObjectMapper objectMapper;


    @SneakyThrows
    public String toJson(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            log.info("Successfully serialized object to JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    @SneakyThrows
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            T obj = objectMapper.readValue(json, clazz);
            log.info("Successfully deserialized JSON to object of type {}: {}", clazz.getSimpleName(), obj);
            return obj;
        } catch (JsonProcessingException e) {
            log.error("Invalid JSON format: {}", e.getMessage(), e);
            throw new InvalidJsonException("Invalid JSON format");
        }
    }
}