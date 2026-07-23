package com.savbill.integrationsystem.billgen.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class CreditDocumentDTO {
    private Integer id;
    private Integer customer;
    private String paymode;
    private String chequedate;
    private String paydetails1; //Bank
    private String paydetails2; //Branch
    private String paydetails3; //ChequeNo
    private String paydetails4; //PaymentReferenceNo
    private Double amount = 0.0;
    private String status;
    private Integer approverid;
    private String remarks;
    private String referenceno;
    private String xmldocument;
    private Boolean isDelete;
    private Boolean tdsflag;
    private Boolean isReversed;
    private LocalDate resevrsedDate;
    private Integer resverseDebitdocId;
    private Boolean tdsReceived;
    private LocalDate tdsReceivedDate;
    private Integer tdsCreditDocId;
    private Integer mvnoId;
    private Long buID;
    private Integer lcoid;
    private Integer invoiceId;
    private String paytype;
    private String type;
    private Integer nextTeamHierarchyMappingId;
    private String reciptNo;
    private Double adjustedAmount;
    private Long bankManagement;
    private Long destinationBank;
    private String filename;
    private String uniquename;
    private Double barteramount;
    private Double tdsAmount;
    private Double abbsAmount;
    private String branchname;
    private String onlinesource;
    private String creditdocumentno;
    private String paymentdate;
    private String irdSync;
    private String irdRespCode;
	private String createdate;
	private Double createdbystaffid;
	private Double lastmodifiedbystaffid;
	private String lastmodifieddate;

    private List<CreditDebitDocMappingDTO> creditDebitDocMappingList;

    private String ledgerId;
    double remainingAmount;

}
