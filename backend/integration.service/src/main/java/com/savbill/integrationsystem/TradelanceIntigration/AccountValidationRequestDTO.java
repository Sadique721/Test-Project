package com.savbill.integrationsystem.TradelanceIntigration;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
class AccountValidationRequestDTO {
    @NotNull
    @JsonProperty("AccountNo")
    private String AccountNo;

    @NotNull
    @JsonProperty("PhoneNumber")
    private String PhoneNumber;
}

