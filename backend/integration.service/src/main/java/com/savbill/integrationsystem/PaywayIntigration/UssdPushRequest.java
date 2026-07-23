package com.savbill.integrationsystem.PaywayIntigration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UssdPushRequest {

    @NotBlank(message = "AccountNo cannot be null or empty")
    @JsonProperty("AccountNo")
    private String AccountNo;

    @NotBlank(message = "MSISDN cannot be null or empty")
    @JsonProperty("MSISDN")
    private String MSISDN;

    @JsonProperty("Amount")
    @Positive(message = "Amount must be positive")
    @NotNull(message = "Amount cannot be null")
    private Double Amount;

    @NotBlank(message = "Narration cannot be null or empty")
    @JsonProperty("Narration")
    private String Narration;
}
