package com.savbill.integrationsystem.middleware.Invoice.dto.customerdetail;

import lombok.Data;

import java.util.List;

@Data
public class SendInvoiceMessage {

    List<SendInvoiceDTO> sendInvoiceDTOList;
}
