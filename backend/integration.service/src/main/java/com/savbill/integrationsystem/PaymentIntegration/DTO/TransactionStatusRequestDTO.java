package com.savbill.integrationsystem.PaymentIntegration.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatusRequestDTO {

    @JsonProperty("MsgInfo")
    private MsgInfo msgInfo;
    @JsonProperty("MsgData")
    private MsgData msgData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MsgInfo {
        @JsonProperty("VersionNo")
        private String versionNo;
        @JsonProperty("MsgID")
        private String msgID;
        @JsonProperty("TimeStamp")
        private String timeStamp;
        @JsonProperty("MsgType")
        private String msgType;
        @JsonProperty("InsID")
        private String insID;
    }

    // Inner class for MsgData
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MsgData {
        @JsonProperty("RequestID")
        private String requestID;
        @JsonProperty("MerchantUserID")
        private String merchantUserID;
    }
}
