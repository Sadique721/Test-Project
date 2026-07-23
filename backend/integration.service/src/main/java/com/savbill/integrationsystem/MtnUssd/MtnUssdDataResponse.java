package com.savbill.integrationsystem.MtnUssd;

import lombok.Data;

@Data
public class MtnUssdDataResponse {

    private String inboundResponse;

    private Boolean userInputRequired;
}
