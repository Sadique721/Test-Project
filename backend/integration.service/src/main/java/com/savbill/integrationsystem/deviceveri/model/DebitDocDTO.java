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
public class DebitDocDTO extends Auditable<Long> implements IBaseDto{
	private Long debitdocumentid;
	private String debitdocumentnumber;
	private Long subscriberid;
	private LocalDateTime billdate;
	private LocalDateTime startdate;
	private LocalDateTime enddate;
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
	private String totalamountinwords;
	private String totaldueinwords;
	private Long billrunid;
	private String billrunstatus;
	private Integer isDelete;
	private Long creditDocId;
	private Long custpackrelid;
	private String createbyname;
	private String updatebyname;
	private Double createdbystaffid;
	private Double lastmodifiedbystaffid;
	private LocalDateTime lastmodifieddate;
	private Long cstchargeid;
	private String status;
	private String paymentowner;
	private String custRefName;
	private Long inventoryMappingId;

	@Override
	public Long getIdentityKey() {
		// TODO Auto-generated method stub
		return debitdocumentid;
	}

	@Override
	public Long getMvnoId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMvnoId(Long mvnoId) {
		// TODO Auto-generated method stub
		
	}
}
