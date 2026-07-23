package com.savbill.integrationsystem.middleware.Invoice.dto.customerdetail;

import lombok.Data;

import java.util.List;

@Data
public class SendinvoiceQRMessage {

    List<SendQRDTO> sendQRDTOList;
}
