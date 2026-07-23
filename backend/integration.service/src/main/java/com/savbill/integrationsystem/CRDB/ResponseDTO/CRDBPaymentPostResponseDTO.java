package com.savbill.integrationsystem.CRDB.ResponseDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.savbill.integrationsystem.CRDB.Constants.CRDBConstant;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CRDBPaymentPostResponseDTO {

    @JsonProperty("status")
    private Integer status;

    @JsonProperty("statusDesc")
    private String statusDesc;

    @JsonProperty("data")
    private ReceiptData data;

    public static CRDBPaymentPostResponseDTO error(int statusCode, String desc) {
        CRDBPaymentPostResponseDTO dto = new CRDBPaymentPostResponseDTO();
        dto.setStatus(statusCode);
        dto.setStatusDesc(desc);
        return dto;
    }

    public static CRDBPaymentPostResponseDTO success(String creditDocId) {
        CRDBPaymentPostResponseDTO dto = new CRDBPaymentPostResponseDTO();
        dto.setStatus(CRDBConstant.STATUS_SUCCESS);
        dto.setStatusDesc(CRDBConstant.STATUS_SUCCESS_DESC);
        ReceiptData data = new ReceiptData();
        data.setReceipt(creditDocId);
        dto.setData(data);
        return dto;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReceiptData {

        @JsonProperty("receipt")
        private String receipt;
    }
}
