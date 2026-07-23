package com.savbill.integrationsystem.kbzIntegration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KbzPayPayload {

    @JsonProperty("timestamp")
    private String timestamp;  // UTC timestamp in seconds

    @JsonProperty("notify_url")
    private String notifyUrl;  // Callback URL

    @JsonProperty("method")
    private String method;     // Fixed value: kbz.payment.precreate

    @JsonProperty("nonce_str")
    private String nonceStr;   // 32-character random string

    @JsonProperty("sign_type")
    private String signType;   // Signature type: SHA256

    @JsonProperty("sign")
    private String sign;       // Request signature

    @JsonProperty("version")
    private String version;    // Version: 1.0


    @JsonProperty("biz_content")
    private BizContent bizContent;

    @Data
    public static class BizContent {

        @JsonProperty("merch_order_id")
        private String merchOrderId; // Merchant order ID

        @JsonProperty("merch_code")
        private String merchantCode;

        @JsonProperty("appid")
        private String appId;               // Application ID (e.g., "kp1234567890987654321aabbccddeef")

        @JsonProperty("trade_type")
        private String tradeType;           // Trade type (e.g., "PWAAPP")
//
//        @JsonProperty("title")
//        private String title;               // Product title (e.g., "iPhoneX")

        @JsonProperty("total_amount")
        private String totalAmount;         // Payment amount in smallest unit (e.g., "5000000" MMK)

        @JsonProperty("trans_currency")
        private String transCurrency;       // Currency (e.g., "MMK")

//        @JsonProperty("timeout_express")
//        private String timeoutExpress;      // Order timeout (e.g., "100m")
//
//        @JsonProperty("callback_info")
//        private String callbackInfo;        // Encoded callback info (e.g., "title%3diphonex")

    }


}


