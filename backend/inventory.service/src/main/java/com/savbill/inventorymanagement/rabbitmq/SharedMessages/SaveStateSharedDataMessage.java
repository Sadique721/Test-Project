package com.savbill.inventorymanagement.rabbitmq.SharedMessages;

import com.savbill.inventorymanagement.modules.MasterManagement.Country.Country;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import lombok.Data;

import java.io.IOException;

@Data

public class SaveStateSharedDataMessage {

    private Integer id;

    private String name;

    private String status;

//    @JsonSerialize(using = CountrySerializer.class)
//    @JsonDeserialize(using = CountryDeserializer.class)
    private Country country;

    private Boolean isDeleted;

    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;

    class CountrySerializer extends JsonSerializer<Country> {
        @Override
        public void serialize(Country country, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            // Serialize only the necessary fields of the Partner entity
            jsonGenerator.writeStartObject();
            jsonGenerator.writeNumberField("id", country.getId());
            jsonGenerator.writeStringField("name", country.getName());
            // Serialize other necessary fields...
            jsonGenerator.writeEndObject();
        }
    }

    class CountryDeserializer extends JsonDeserializer<Country> {

        @Override
        public Country deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            Integer id = node.get("id").asInt();
            String name = node.get("name").asText();
            // Deserialize other necessary fields...
            return new Country(id, name);
        }
    }
}
