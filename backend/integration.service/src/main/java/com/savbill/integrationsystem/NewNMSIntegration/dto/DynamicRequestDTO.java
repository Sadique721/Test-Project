package com.savbill.integrationsystem.NewNMSIntegration.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DynamicRequestDTO {
    private String apiName;
    private String customerId;
    private Map<String, String> parameters = new HashMap<>();
    private Map<String, Integer> numberParameters = new HashMap<>();

    // Factory method for creating a request
    public static DynamicRequestDTO createRequest(String apiName, Map<String, String> params) {
        DynamicRequestDTO request = new DynamicRequestDTO();
        request.setApiName(apiName);
        request.setParameters(params);
        return request;
    }

    @JsonAnySetter
    public void setDynamicField(String name, String value) {
        parameters.put(name, value);
    }

    @JsonAnySetter
    public void setIntegerDynamicField(String name, Integer value) {
        numberParameters.put(name, value);
    }
}
