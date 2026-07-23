package com.savbill.integrationsystem.deviceveri.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Price {

    @JsonProperty("master_price")
    private String masterPrice;
    
    @JsonProperty("package_price")
    private String packagePrice;

}
