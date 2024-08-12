package com.khokhlov.universityserver.utils;


import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;

@Slf4j
@UtilityClass
public class HttpRequestUtils {
    public static String getBody(HttpServletRequest request) throws IOException {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = request.getReader()) {
            if (bufferedReader == null) {
                throw new IOException("BufferedReader is null");
            }
            char[] charBuffer = new char[128];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            log.error("Error reading request body: {}", e.getMessage(), e);
            throw e;
        }
        return stringBuilder.toString();
    }
}
