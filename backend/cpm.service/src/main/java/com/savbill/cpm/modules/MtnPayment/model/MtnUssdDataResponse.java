package com.savbill.cpm.modules.MtnPayment.model;

import com.savbill.cpm.devCode.MultilineStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
public class MtnUssdDataResponse {

    @JsonSerialize(using = MultilineStringSerializer.class)
    private String inboundResponse;

    private Boolean userInputRequired;
}
