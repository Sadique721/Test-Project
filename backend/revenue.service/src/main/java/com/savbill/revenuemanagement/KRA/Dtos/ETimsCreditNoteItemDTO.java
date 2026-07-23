package com.savbill.revenuemanagement.KRA.Dtos;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ETimsCreditNoteItemDTO {
    private String itemCode;
    private Double unitPrice;
    private Integer quantity;
    private Double discountRate;
}

