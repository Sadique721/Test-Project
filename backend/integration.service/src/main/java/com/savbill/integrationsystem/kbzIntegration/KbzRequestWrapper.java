package com.savbill.integrationsystem.kbzIntegration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KbzRequestWrapper {

    @JsonProperty("Request")
    private KbzPayPayload kbzPayPayload;

}
