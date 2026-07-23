package com.savbill.integrationsystem.deviceveri.dto.transactiondetail;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TransactionDetailsResponse {

    @JsonProperty("from")
    private String from;
    
    @JsonProperty("to")
    private String to;
    
    @JsonProperty("payments")
    private List<Payment> payments;
    

}
