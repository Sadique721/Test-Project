package com.savbill.integrationsystem.CRDB.RequestDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class CRDBVerificationRequestDTO {

    @JsonProperty("paymentReference")
    private String paymentReference;

    @JsonProperty("token")
    private String token;

    @JsonProperty("checksum")
    private String checksum;

    @JsonProperty("institutionID")
    private Long institutionID;
}
