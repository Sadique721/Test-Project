package com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage;


import com.savbill.ticketmanagement.core.modules.Country.domain.Country;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
public class UpdateStateSharedDataMessage {
    private Integer id;

    private String name;

    private String status;

    @JsonSerialize(using = CountrySerializer.class)
    @JsonDeserialize(using = CountryDeserializer.class)
    private Country country;

    private Boolean isDeleted;

    private Integer mvnoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;
    private String lastModifiedByName;

}
