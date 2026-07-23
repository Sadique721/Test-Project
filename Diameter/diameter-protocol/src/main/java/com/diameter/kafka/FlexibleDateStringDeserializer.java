package com.diameter.kafka;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class FlexibleDateStringDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return blankToNull(node.asText());
        }
        if (node.isNumber() || node.isBoolean()) {
            return node.asText();
        }
        if (node.isArray()) {
            return deserializeArray(node);
        }
        if (node.isObject()) {
            return deserializeObject(node);
        }
        return blankToNull(node.asText());
    }

    private String deserializeArray(JsonNode node) {
        if (node.size() < 3) {
            return null;
        }
        int year = node.path(0).asInt();
        int month = node.path(1).asInt();
        int day = node.path(2).asInt();
        if (node.size() == 3) {
            return String.format("%04d-%02d-%02d", year, month, day);
        }
        int hour = node.path(3).asInt();
        int minute = node.path(4).asInt();
        int second = node.path(5).asInt();
        if (node.size() > 6) {
            long nanos = node.path(6).asLong();
            if (nanos == 0) {
                return String.format("%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hour, minute, second);
            }
            String fraction = String.format("%09d", nanos);
            return String.format("%04d-%02d-%02dT%02d:%02d:%02d.%s", year, month, day, hour, minute, second, fraction);
        }
        return String.format("%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hour, minute, second);
    }

    private String deserializeObject(JsonNode node) {
        if (node.has("year") && (node.has("month") || node.has("monthValue")) && (node.has("day") || node.has("dayOfMonth"))) {
            int year = node.path("year").asInt();
            int month = node.has("month") ? node.path("month").asInt() : node.path("monthValue").asInt();
            int day = node.has("day") ? node.path("day").asInt() : node.path("dayOfMonth").asInt();
            if (node.has("hour") || node.has("minute") || node.has("second")) {
                String value = String.format("%04d-%02d-%02dT%02d:%02d:%02d", year, month, day,
                        node.path("hour").asInt(), node.path("minute").asInt(), node.path("second").asInt());
                long nanos = node.path("nano").asLong();
                return nanos == 0 ? value : value + "." + String.format("%09d", nanos);
            }
            return String.format("%04d-%02d-%02d", year, month, day);
        }
        return blankToNull(node.toString());
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
