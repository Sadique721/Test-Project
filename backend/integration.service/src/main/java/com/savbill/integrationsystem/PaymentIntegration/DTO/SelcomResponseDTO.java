package com.savbill.integrationsystem.PaymentIntegration.DTO;

import lombok.Data;

@Data
public class SelcomResponseDTO {
    private String reference;
    private String resultcode;
    private String result;
    private String message;
//    private String name; //optional
//    private String amount; //optional


}
