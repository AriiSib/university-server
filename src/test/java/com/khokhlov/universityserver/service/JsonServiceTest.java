package com.khokhlov.universityserver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.khokhlov.universityserver.exception.InvalidJsonException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class JsonServiceTest {

    private ObjectMapper objectMapper;
    private JsonService jsonService;

    @BeforeEach
    void setUp() {
        objectMapper = Mockito.mock(ObjectMapper.class);
        jsonService = new JsonService(objectMapper);
    }

    @Test
    void should_SerializeObjectToJson_When_ObjectIsValid() throws Exception {
        TestClass testObject = new TestClass("value");
        String expectedJson = "{\"value\":\"value\"}";

        Mockito.when(objectMapper.writeValueAsString(testObject)).thenReturn(expectedJson);

        String json = jsonService.toJson(testObject);

        assertEquals(expectedJson, json);
    }

    @Test
    void should_ThrowRuntimeException_When_SerializationFails() throws Exception {
        TestClass testObject = new TestClass("value");
        Mockito.when(objectMapper.writeValueAsString(testObject)).thenThrow(JsonProcessingException.class);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> jsonService.toJson(testObject));

        assertEquals("Failed to serialize object to JSON", thrown.getMessage());
    }

    @Test
    void should_DeserializeJsonToObject_When_JsonIsValid() throws Exception {
        String json = "{\"value\":\"value\"}";
        TestClass expectedObject = new TestClass("value");

        Mockito.when(objectMapper.readValue(json, TestClass.class)).thenReturn(expectedObject);

        TestClass obj = jsonService.fromJson(json, TestClass.class);

        assertEquals(expectedObject, obj);
    }

    @Test
    void should_ThrowInvalidJsonException_When_DeserializationFails() throws Exception {
        String json = "{\"value\":\"value\"}";
        Mockito.when(objectMapper.readValue(json, TestClass.class)).thenThrow(JsonProcessingException.class);

        InvalidJsonException thrown = assertThrows(InvalidJsonException.class, () -> jsonService.fromJson(json, TestClass.class));
        assertEquals("Invalid JSON format", thrown.getMessage());
    }

    static class TestClass {
        private String value;

        public TestClass(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestClass testClass = (TestClass) o;
            return value.equals(testClass.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
}