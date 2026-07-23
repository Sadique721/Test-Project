package com.savbill.integrationsystem.CDATA.Pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceProvisonPojo {

    @JsonProperty("tmplId")
    private String tmplId;

    @JsonProperty("conditionContent")
    private ConditionContent conditionContent;

    @JsonProperty("configDesc")
    private String configDesc;

    // Constructors, Getters, and Setters

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConditionContent {
        @JsonProperty("sn")
        private String sn;

        @JsonProperty("manufacturer")
        private String manufacturer;

        @JsonProperty("model")
        private String model;

        // Constructors, Getters, and Setters
    }

    // Constructors, Getters, and Setters
}
