package com.savbill.integrationsystem.waveMoney;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONString;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaveMoneyPayload {
    @JsonProperty("merchant_id")
    private String merchantId;

    @JsonProperty("order_id")
    private String orderId;

    // orderId for savbill
    @JsonProperty("merchant_reference_id")
    private String merchantReferenceId;

    @JsonProperty("frontend_result_url")
    private String frontendResultUrl;

    @JsonProperty("backend_result_url")
    private String backendResultUrl;

    @JsonProperty("amount")
    private Integer amount;

    @JsonProperty("time_to_live_in_seconds")
    private Integer timeToLiveInSeconds;

    @JsonProperty("payment_description")
    private String paymentDescription;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("hash")
    private String hash;

    @JsonProperty("merchant_name")
    private String merchantName;

    @JsonProperty("items")
    private String items; // If you need to parse it into a list, see below.

    @Data
    public static class Item {
        private String name;
        private Integer amount;
    }
}

