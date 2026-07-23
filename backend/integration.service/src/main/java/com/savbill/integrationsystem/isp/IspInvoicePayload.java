package com.savbill.integrationsystem.isp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IspInvoicePayload {
    private String invoiceId;
    private String clientId;
    private String invoiceDate;
    private List<ServicePayload> services=new ArrayList<>();
}
