package com.savbill.integrationsystem.etims.DTO;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ETimsGetSalesDTO {
    private String traderInvoiceNo;
    private Integer mvnoId;
}
