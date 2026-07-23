package com.savbill.revenuemanagement.KRA.Dtos;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class KRAGenericResponseDTO {
    private int responseCode;
    private String responseMessage;
    private Object data;
    private List dataList;
    private String customerNo;
    private String traderInvoiceNo;
    private String invoiceQR;
    private String KRAInvoiceId;

    public static KRAGenericResponseDTO getGenericDataDTO(List entityList) {
        KRAGenericResponseDTO genericDataDTO = new KRAGenericResponseDTO();
        genericDataDTO.setDataList(entityList);
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        return genericDataDTO;
    }
}
