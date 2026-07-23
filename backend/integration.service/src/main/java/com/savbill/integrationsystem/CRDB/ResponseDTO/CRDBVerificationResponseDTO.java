package com.savbill.integrationsystem.CRDB.ResponseDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.savbill.integrationsystem.CRDB.Constants.CRDBConstant;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CRDBVerificationResponseDTO {

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("statusDesc")
    private String statusDesc;

    @JsonProperty("data")
    private Data data;


    public static CRDBVerificationResponseDTO error(int statusCode, String desc) {
        CRDBVerificationResponseDTO dto = new CRDBVerificationResponseDTO();
        dto.setStatus(statusCode);
        dto.setStatusDesc(desc);
        return dto;
    }

    public static CRDBVerificationResponseDTO success(Data data) {
        CRDBVerificationResponseDTO dto = new CRDBVerificationResponseDTO();
        dto.setStatus(CRDBConstant.STATUS_SUCCESS);
        dto.setStatusDesc(CRDBConstant.STATUS_SUCCESS_DESC);
        dto.setData(data);
        return dto;
    }


    @lombok.Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Data {

        @JsonProperty("payerName")
        private String payerName;

        @JsonProperty("amount")
        private String amount;

        /** FIXED | FLEXIBLE | FULL */
        @JsonProperty("amountType")
        private String amountType;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("paymentReference")
        private String paymentReference;

        @JsonProperty("paymentType")
        private String paymentType;

        @JsonProperty("paymentDesc")
        private String paymentDesc;

        @JsonProperty("payerID")
        private String payerID;
    }
}
