package com.savbill.integrationsystem.middleware.selfcare.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class CloseCaseDetails {
    @JsonProperty("LastModifiedDate")
    String LastModifiedDate;

    @JsonProperty("TicketNo")
    String TicketNo;

    @JsonProperty("Title")
    String Title;

    @JsonProperty("SubSubCategory")
    String SubSubCategory;

    @JsonProperty("Status")
    String Status;

    @JsonProperty("CreatedDate")
    String CreatedDate;
}