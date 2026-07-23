package com.savbill.revenuemanagement.InvoiceIntigration;

import lombok.Data;

import java.util.List;

@Data
public class SendInvoiceMessage {

    List<SendInvoiceDTO> sendInvoiceDTOList;
}
