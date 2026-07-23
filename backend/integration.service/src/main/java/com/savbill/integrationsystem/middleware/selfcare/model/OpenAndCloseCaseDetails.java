package com.savbill.integrationsystem.middleware.selfcare.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OpenAndCloseCaseDetails {
    @JsonProperty("OpenCaseDetails")
    List<OpenCaseDetails> openCaseDetails;

    @JsonProperty("CloseCaseDetails")
    List<CloseCaseDetails> closeCaseDetails;
}