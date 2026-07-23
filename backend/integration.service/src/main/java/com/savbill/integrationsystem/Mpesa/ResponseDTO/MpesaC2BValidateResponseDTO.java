package com.savbill.integrationsystem.Mpesa.ResponseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MpesaC2BValidateResponseDTO {
    @JsonProperty("ResultCode")
    private String resultCode;
    @JsonProperty("ResultDesc")
    private String resultDesc;
    @JsonProperty("ThirdPartyTransID")
    private String orderId;
}
