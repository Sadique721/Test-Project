package com.savbill.integrationsystem.rabbitmq;

import com.savbill.integrationsystem.billgen.entity.DebitDocDetails;
import com.savbill.integrationsystem.billgen.entity.DebitDocumentTAXRel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DebitDocumentMessage {
    private Integer id;
    private String docnumber;
    private String billdate;
    private String startdate;
    private String endate;
    private String duedate;
    private String latepaymentdate;
    private Double subtotal;
    private Double tax;
    private Double discount;
    private Double totalamount;
    private Double previousbalance;
    private Double latepaymentfee;
    private Double currentpayment;
    private Double currentdebit;
    private Double currentcredit;
    private Double totaldue;
    private String amountinwords;
    private String dueinwords;
    private Integer billrunid;
    private String billrunstatus;
    private Boolean isDelete = false;
    private Long cstchargeid;
    private Integer custpackrelid;
    private String status;
    private String paymentowner;
    private List<DebitDocDetails> debitDocDetailsList;
    private List<DebitDocumentTAXRel> debitDocumentTAXRels;
    private Integer  customerId;
    private String custRefName;
    private String irdSync;
    private String irdRespCode;
    private Long inventoryMappingId;
}
