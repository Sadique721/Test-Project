package com.savbill.revenuemanagement.InvoiceIntigration;

import lombok.Data;

import java.util.List;

@Data
public class SendinvoiceQRMessage {

    List<SendQRDTO> sendQRDTOList;
}
