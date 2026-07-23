package com.savbill.revenuemanagement.core.service.prepaid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebitDocCustDTO {
    private Integer id;
    private Integer custpackrelid;
    private String customerUsername;
    private Double totalamount;
}
