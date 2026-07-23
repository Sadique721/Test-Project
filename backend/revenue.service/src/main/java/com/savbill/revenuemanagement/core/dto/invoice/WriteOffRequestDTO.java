package com.savbill.revenuemanagement.core.dto.invoice;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WriteOffRequestDTO {
    private Integer debitDocId;
    private Double writeOffAmount;
    private String remarks;
}
