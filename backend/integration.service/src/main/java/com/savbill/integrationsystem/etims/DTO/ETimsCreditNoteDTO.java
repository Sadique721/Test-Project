package com.savbill.integrationsystem.etims.DTO;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ETimsCreditNoteDTO {
    private Integer orgInvoiceNo;
    private String traderInvoiceNo;
    private String salesType;
    private String paymentType;
    private String creditNoteDate;
    private String confirmDate;
    private String salesDate;
    private String stockReleseDate;
    private String receiptPublishDate;
    private String occurredDate;
    private String creditNoteReason;
    private String invoiceStatusCode;
    private Boolean isPurchaseAccept;
    private String remark;
    private String mapping;

    private List<ETimsCreditNoteItemDTO> creditNoteItemsList;
    private Integer mvnoId;
}
