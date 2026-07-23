package com.savbill.integrationsystem.middleware.selfcare.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Ticket {
    @JsonProperty("OpenCaseDetails")
    OpenCaseDetails openCaseDetails;

    @JsonProperty("CloseCaseDetails")
    CloseCaseDetails closeCaseDetails;

    @JsonProperty("SubCategoryId")
    String subCategoryId;

    @JsonProperty("Email")
    String email;

    @JsonProperty("Mobile")
    String mobile;

    @JsonProperty("Priority")
    String priority;

    @JsonProperty("Status")
    String status;

    @JsonProperty("combassign")
    String combassign;

    @JsonProperty("Remarks")
    String remarks;

    @JsonProperty("CategoryId")
    double categoryId;
}