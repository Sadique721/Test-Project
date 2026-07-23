package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;


import com.savbill.ticketmanagement.core.modules.Partner.domain.Partner;
import com.savbill.ticketmanagement.core.modules.Teams.domain.Teams;
import com.savbill.ticketmanagement.core.modules.staffuser.domain.StaffUser;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Data
public class SaveTeamsSharedSharedData {


    private Long id;


    private String name;


    private String status;


    private Set<StaffUser> staffUser = new HashSet<>();


    private Boolean isDeleted = false;



    @JsonSerialize(using = PartnerSerializer.class)
    @JsonDeserialize(using = PartnerDeserializer.class)
    private Partner partner;


    private Integer mvnoId;


    private Teams parentTeams;

    private String cafStatus;


    private Integer lcoId;
    private String teamType;
}

class PartnerSerializer extends JsonSerializer<Partner> {
    @Override
    public void serialize(Partner partner, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // Serialize only the necessary fields of the Partner entity
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", partner.getId());
        jsonGenerator.writeStringField("name", partner.getName());
        // Serialize other necessary fields...
        jsonGenerator.writeEndObject();
    }
}

class PartnerDeserializer extends JsonDeserializer<Partner> {
    @Override
    public Partner deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        // Deserialize the necessary fields and construct a Partner object
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        Integer id = node.get("id").asInt();
        String name = node.get("name").asText();
        // Deserialize other necessary fields...
        return new Partner(id, name);
    }
}

