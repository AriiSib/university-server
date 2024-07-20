package com.khokhlov.universityserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khokhlov.universityserver.exception.InvalidJsonException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;


@RequiredArgsConstructor
public class JsonService {

    private final ObjectMapper objectMapper;


    @SneakyThrows
    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    @SneakyThrows
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException("Invalid JSON format");
        }
    }
}
