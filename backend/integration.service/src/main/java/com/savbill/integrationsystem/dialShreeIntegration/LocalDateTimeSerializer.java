package com.savbill.integrationsystem.dialShreeIntegration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeSerializer extends JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter FORMATTER_WITH_T = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter FORMATTER_WITHOUT_T = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String date = jsonParser.getText();
        try {
            return LocalDateTime.parse(date, FORMATTER_WITH_T); // First try ISO format
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(date, FORMATTER_WITHOUT_T); // Fallback to space-separated format
        }
    }
}
