package com.savbill.integrationsystem.NewNMSIntegration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.apache.kafka.common.security.oauthbearer.internals.unsecured.OAuthBearerValidationResult;

import java.util.Map;

@Data
public class ONUResponseDTO {
    @JsonProperty("DATA")
    private Map<String, String> data;
    @JsonProperty("RESUTID")
    private String resultId;
    @JsonProperty("RESULTDESC")
    private String resultdesc;
    @JsonProperty("STATUSCODE")
    private Integer statusCode;
}