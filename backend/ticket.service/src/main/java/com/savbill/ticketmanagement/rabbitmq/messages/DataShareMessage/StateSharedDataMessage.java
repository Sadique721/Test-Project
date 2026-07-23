package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;


import com.savbill.ticketmanagement.core.modules.Country.domain.Country;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import lombok.Data;

import java.io.IOException;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StateSharedDataMessage {

    private Integer id;

    private String name;

    private String status;

//    @JsonSerialize(using = CountrySerializer.class)
//    @JsonDeserialize(using = CountryDeserializer.class)
    private Country country;

    private Boolean isDeleted;

    private Integer mvnoId;


}


class CountrySerializer extends JsonSerializer<Country> {
    @Override
    public void serialize(Country city, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // Serialize only the necessary fields of the Partner entity
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", city.getId());
        jsonGenerator.writeStringField("name", city.getName());
        // Serialize other necessary fields...
        jsonGenerator.writeEndObject();
    }
}

class CountryDeserializer extends JsonDeserializer<Country> {
    @Override
    public Country deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        // Deserialize the necessary fields and construct a Partner object
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Integer id = node.get("id").asInt();
        String name = node.get("name").asText();
        // Deserialize other necessary fields...
        return new Country(id, name);
    }
}
