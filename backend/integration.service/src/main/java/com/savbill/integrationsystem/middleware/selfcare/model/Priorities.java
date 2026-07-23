package com.savbill.integrationsystem.middleware.selfcare.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Priorities {
    @JsonProperty("Id")
    Integer Id;

    @JsonProperty("Name")
    String Name;
}
