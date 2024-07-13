package com.khokhlov.universityserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class JsonService {

    private final ObjectMapper objectMapper;


    @SneakyThrows
    public String toJson(Object obj) {
        return objectMapper.writeValueAsString(obj);
    }

    @SneakyThrows
    public <T> T fromJson(String json, Class<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }
}
