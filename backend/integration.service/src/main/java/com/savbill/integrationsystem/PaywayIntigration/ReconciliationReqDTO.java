package com.savbill.integrationsystem.PaywayIntigration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReconciliationReqDTO {
    @JsonProperty("PaidAfter")
    private String PaidAfter;

    @JsonProperty("PaidBefore")
    private  String PaidBefore;

    @JsonProperty("ForwardedAfter")
    private String ForwardedAfter;

    @JsonProperty("ForwardedBefore")
    private String ForwardedBefore;
}
