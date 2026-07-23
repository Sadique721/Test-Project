package com.savbill.integrationsystem.PaywayIntigration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class PackageListRequest {
    @NotBlank(message = "AccountNo cannot be null or empty")
    @JsonProperty("AccountNo")
    private String AccountNo;

}
