package com.diameter.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class BooleanToShortDeserializer extends JsonDeserializer<Short> {

    @Override
    public Short deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {

        JsonToken token = parser.getCurrentToken();

        if (token == JsonToken.VALUE_TRUE) {
            return 1;
        }

        if (token == JsonToken.VALUE_FALSE) {
            return 0;
        }

        if (token == JsonToken.VALUE_NUMBER_INT) {
            return parser.getShortValue();
        }

        if (token == JsonToken.VALUE_STRING) {
            return Short.parseShort(parser.getText());
        }

        return null;
    }
}