package com.savbill.integrationsystem.rabbitmq;

import com.savbill.integrationsystem.billgen.entity.CreditDebitDocMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditDocMessage {

    private Integer id;
    private Integer customer;

    private String paymode;
   // private LocalDate paymentdate;

    private String paydetails1; //Bank

    private String paydetails2; //Branch
    private String paydetails3; //ChequeNo
    private String paydetails4; //PaymentReferenceNo

    private Double amount = 0.0;

    private String status;

    private Integer approverid;

    private String remarks;

    private String referenceno;

    private Boolean isDelete;

    private Boolean tdsflag;

    private Double tdsamount;

    private Boolean is_reversed;

    private LocalDate resevrsed_date;
    private Integer resverse_debitdoc_id;
    private Boolean tds_received;
    private LocalDate tds_received_date;

    private Integer tds_credit_doc_id;

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
    private  String paymentdate;
    private String xmldocument;
    private String chequedate;
    private String paymentreferenceno;
    private List<CreditDebitDocMapping> creditDebitDocMappingList;

    private String ledgerId;
    private String createdate;
	private Integer createdbystaffid;
	private Integer lastmodifiedbystaffid;
	private String lastmodifieddate;
    private String irdSync;
    private String irdRespCode;
}
