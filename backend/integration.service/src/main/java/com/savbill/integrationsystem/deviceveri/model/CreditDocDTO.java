package com.savbill.integrationsystem.deviceveri.model;

import java.time.LocalDateTime;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=false)
public class CreditDocDTO extends Auditable<Long> implements IBaseDto{
	private Long id;
	private LocalDateTime paymentdate;
	private String paymode;
	private String paydetails1;
	private String paydetails2;
	private String paydetails3;
	private String paydetails4;
	private Double amount;
	private String status;
	private Double approvedbystaffid;
	private String remarks;
	private LocalDateTime createdate;
	private Double createdbystaffid;
	private Double lastmodifiedbystaffid;
	private LocalDateTime lastmodifieddate;
	private String referenceno;
	private String xmldocument;
	private LocalDateTime chequedate;
	private Integer isDelete;
	private Integer tdsflag;
	private Double tdsamount;
	private Integer isReversed;
	private LocalDateTime resevrsedDate;
	private Long resverseDebitdocId;
	private Integer tdsReceived;
	private LocalDateTime tdsReceivedDate;
	private Long tdsCreditDocId;
	private String createbyname;
	private String updatebyname;
	private Long mvnoid;
	private Long invoiceid;
	private String type;
	private Long adjustedamount;
	private String paytype;
	private Long bankid;
	private Long buid;
	private Long nextTeamHirMapping;
	private String receiptNumber;
	private String filename;
	private String uniquename;
	private Double barteramount;
	private Long lcoid;
	private Double tdsAmount;
	private Double abbsAmount;
	private Long printCounter;
	private String branch;
//	private Long destinationBank;
	private String onlinesource;
	private String creditdocumentno;
	private Long customer;
//	private Double remainingAmount;
//	private String ledgerId;


	@Override
	public Long getIdentityKey() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public Long getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoid;
	}

	@Override
	public void setMvnoId(Long mvnoId) {
		// TODO Auto-generated method stub
		
	}
}
