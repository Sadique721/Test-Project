package com.savbill.integrationsystem.middleware.selfcare.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TicketRequest {
    @JsonProperty("UserId")
    String userId;

    @JsonProperty("Title")
    String title;

    @JsonProperty("CategoryType")
    Double categoryType;

    @JsonProperty("SubCategoryId")
    Double subCategoryId;

    @JsonProperty("Email")
    String email;

    @JsonProperty("Mobile")
    String mobile;

    @JsonProperty("Priority")
    Double priority;

    @JsonProperty("Status")
    String status;

//    @JsonProperty("PartnerId")
//    String partnerId;

    @JsonProperty("combassign")
    String combassign;

    @JsonProperty("Remarks")
    String remarks;

    @JsonProperty("Solution")
    String solution;

    @JsonProperty("CategoryId")
    Double categoryId;
}
