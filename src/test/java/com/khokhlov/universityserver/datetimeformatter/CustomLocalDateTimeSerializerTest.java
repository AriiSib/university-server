package com.khokhlov.universityserver.datetimeformatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class CustomLocalDateTimeSerializerTest {

    @Test
    void should_SerializeLocalDateTimeCorrectly() throws IOException {
        CustomLocalDateTimeSerializer serializer = new CustomLocalDateTimeSerializer();
        LocalDateTime dateTime = LocalDateTime.of(2024, 7, 23, 14, 30);
        JsonGenerator jsonGenerator = mock(JsonGenerator.class);
        SerializerProvider serializerProvider = mock(SerializerProvider.class);

        serializer.serialize(dateTime, jsonGenerator, serializerProvider);

        verify(jsonGenerator, times(1)).writeString("2024-07-23 14:30");
    }

}