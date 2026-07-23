package com.savbill.integrationsystem.deviceveri.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.integrationsystem.core.data.IBaseData;

import lombok.Data;

@Data
@Entity
@Table(name = "tbltcreditdoc")
public class CreditDocData implements IBaseData<Long>{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="CREDITDOCID") 
	private Long id;
	@Column(name="PAYMENTDATE") 
	private LocalDateTime paymentdate;
	@Column(name="PAYMODE") 
	private String paymode;
	@Column(name="PAYDETAILS1") 
	private String paydetails1;
	@Column(name="PAYDETAILS2") 
	private String paydetails2;
	@Column(name="PAYDETAILS3") 
	private String paydetails3;
	@Column(name="PAYDETAILS4") 
	private String paydetails4;
	@Column(name="AMOUNT") 
	private Double amount;
	@Column(name="STATUS") 
	private String status;
	@Column(name="APPROVEDBYSTAFFID") 
	private Double approvedbystaffid;
	@Column(name="REMARKS") 
	private String remarks;
	@Column(name="CREATEDATE") 
	private LocalDateTime createdate;
	@Column(name="CREATEDBYSTAFFID") 
	private Double createdbystaffid;
	@Column(name="LASTMODIFIEDBYSTAFFID") 
	private Double lastmodifiedbystaffid;
	@Column(name="LASTMODIFIEDDATE") 
	private LocalDateTime lastmodifieddate;
	@Column(name="referenceno") 
	private String referenceno;
	@Column(name="xmldocument") 
	private String xmldocument;
	@Column(name="chequedate") 
	private LocalDateTime chequedate;
	@Column(name="is_delete") 
	private Integer isDelete;
	@Column(name="tdsflag") 
	private Integer tdsflag;
	@Column(name="tdsamount") 
	private Double tdsamount;
	@Column(name="is_reversed") 
	private Integer isReversed;
	@Column(name="resevrsed_date") 
	private LocalDateTime resevrsedDate;
	@Column(name="resverse_debitdoc_id") 
	private Long resverseDebitdocId;
	@Column(name="tds_received") 
	private Integer tdsReceived;
	@Column(name="tds_received_date") 
	private LocalDateTime tdsReceivedDate;
	@Column(name="tds_credit_doc_id") 
	private Long tdsCreditDocId;
	@Column(name="createbyname") 
	private String createbyname;
	@Column(name="updatebyname") 
	private String updatebyname;
	@Column(name="MVNOID") 
	private Long mvnoid;
	@Column(name="invoiceid") 
	private Long invoiceid;
	@Column(name="type") 
	private String type;
	@Column(name="adjustedamount") 
	private Long adjustedamount;
	@Column(name="paytype") 
	private String paytype;
	@Column(name="bankid") 
	private Long bankid;
	@Column(name="BUID") 
	private Long buid;
	@Column(name="next_team_hir_mapping") 
	private Long nextTeamHirMapping;
	@Column(name="receipt_number") 
	private String receiptNumber;
	@Column(name="filename") 
	private String filename;
	@Column(name="uniquename") 
	private String uniquename;
	@Column(name="barteramount") 
	private Double barteramount;
	@Column(name="lcoid") 
	private Long lcoid;
	@Column(name="tds_amount") 
	private Double tdsAmount;
	@Column(name="abbs_amount") 
	private Double abbsAmount;
	@Column(name="print_counter") 
	private Long printCounter;
	@Column(name="branch") 
	private String branch;
//	@Column(name="destination_bank") 
//	private Long destinationBank;
	@Column(name="onlinesource") 
	private String onlinesource;
	@Column(name="creditdocumentno") 
	private String creditdocumentno;
	@Column(name="customer") 
	private Long customer;
//	@Column(name="remaining_amount") 
//	private Double remainingAmount;
//	@Column(name="ledger_id") 
//	private String ledgerId;


	@Override
	public Long getPrimaryKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeleteFlag(boolean deleteFlag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getDeleteFlag() {
		// TODO Auto-generated method stub
		return false;
	}
}
