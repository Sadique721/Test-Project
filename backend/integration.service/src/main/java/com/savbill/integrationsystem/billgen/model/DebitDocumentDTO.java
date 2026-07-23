package com.savbill.integrationsystem.billgen.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DebitDocumentDTO {
	private Integer id;
	private String debitdocumentnumber;
	private LocalDateTime billdate;
	private LocalDateTime startdate;
	private LocalDateTime endate;
	private LocalDateTime duedate;
	private LocalDateTime latepaymentdate;
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
	private Long creditDocId;
	private Integer custpackrelid;
	private String status;
	private String paymentowner;
    private List<DebitDocDetailsDTO> debitDocDetailsList;
    private List<DebitDocumentTAXRelDTO> debitDocumentTAXRels;
    private Integer customerId;
    private String custRefName;
    private String irdSync;
    private String irdRespCode;
	private Long inventoryMappingId;
}
