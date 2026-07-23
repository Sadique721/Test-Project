package com.savbill.integrationsystem.PaymentIntegration.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnePay {

    @JsonProperty("MerchantUserId")
    private String merchantId;

    @JsonProperty("HashValue")
    private String hash;

    @JsonProperty("OnepayPhoneNo")
    private String OnepayPhoneNo;


    @JsonProperty("Channel")
    private String Channel;

}
