package com.savbill.integrationsystem.isp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class IspMainPayload {
    private List<IspInvoicePayload> invoicePayloads;

    private List<Integer> invoiceIds;

    private String jsonPayload;

    private String responseCode;
}
