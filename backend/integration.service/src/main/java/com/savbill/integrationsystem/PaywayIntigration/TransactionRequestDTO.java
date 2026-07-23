package com.savbill.integrationsystem.PaywayIntigration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@Data
public class TransactionRequestDTO {
    @NotBlank(message = "TransactionId cannot be null or empty")
    @JsonProperty("TransactionId")
    private  String TransactionId;

}
