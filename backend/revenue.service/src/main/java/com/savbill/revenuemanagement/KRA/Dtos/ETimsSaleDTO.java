package com.savbill.revenuemanagement.KRA.Dtos;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ETimsSaleDTO {
    private String customerNo;
    private String customerTin;
    private String customerName;
    private String customerMobileNo;

    private String salesType;
    private String paymentType;
    private String traderInvoiceNo;

    private String confirmDate;
    private String salesDate;
    private String stockReleseDate;
    private String receiptPublishDate;
    private String occurredDate;

    private String invoiceStatusCode;
    private String remark;
    private Boolean isPurchaseAccept;
    private String mapping;

    private List<ETimsSaleItemDTO> saleItemList;
    private Integer mvnoId;
}

