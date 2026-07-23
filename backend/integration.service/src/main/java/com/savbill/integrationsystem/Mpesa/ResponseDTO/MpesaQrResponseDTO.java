package com.savbill.integrationsystem.Mpesa.ResponseDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MpesaQrResponseDTO {
    private String responseCode;
    private String responseDescription;
    private String qrCode;
}
