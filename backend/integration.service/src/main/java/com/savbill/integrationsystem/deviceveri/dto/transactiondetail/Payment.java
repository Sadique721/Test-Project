package com.savbill.integrationsystem.deviceveri.dto.transactiondetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("payment_on")
    private PaymentOn paymentOn;

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("device_type")
    private String deviceType;

    @JsonProperty("package")
    private String _package;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("next_expire")
    private NextExpire nextExpire;

    @JsonProperty("subscriber_code")
    private String subscriberCode;

}
