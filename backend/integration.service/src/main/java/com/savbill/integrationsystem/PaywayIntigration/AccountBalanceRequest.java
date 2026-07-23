package com.savbill.integrationsystem.PaywayIntigration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceRequest {
    @NotBlank(message = "AccountNo cannot be null or empty")
    @JsonProperty("AccountNo")
    private String AccountNo;

    @NotBlank(message = "PhoneNumber cannot be null or empty")
    @JsonProperty("PhoneNumber")
    private String PhoneNumber;
}
