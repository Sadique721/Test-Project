package com.savbill.integrationsystem.deviceveri.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Package {

	@JsonProperty("package_name")
    private String packageName;
    @JsonProperty("price")
    private Price price;

}
