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
@Table(name = "tbltdebitdocument")
public class DebitDocumentData implements IBaseData<Long>{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="debitdocumentid") 
	private Long debitdocumentid;
	@Column(name="debitdocumentnumber") 
	private String debitdocumentnumber;
	@Column(name="subscriberid") 
	private Long subscriberid;
	@Column(name="billdate") 
	private LocalDateTime billdate;
	@Column(name="startdate") 
	private LocalDateTime startdate;
	@Column(name="enddate") 
	private LocalDateTime enddate;
	@Column(name="duedate") 
	private LocalDateTime duedate;
	@Column(name="latepaymentdate") 
	private LocalDateTime latepaymentdate;
	@Column(name="subtotal") 
	private Double subtotal;
	@Column(name="tax") 
	private Double tax;
	@Column(name="discount") 
	private Double discount;
	@Column(name="totalamount") 
	private Double totalamount;
	@Column(name="previousbalance") 
	private Double previousbalance;
	@Column(name="latepaymentfee") 
	private Double latepaymentfee;
	@Column(name="currentpayment") 
	private Double currentpayment;
	@Column(name="currentdebit") 
	private Double currentdebit;
	@Column(name="currentcredit") 
	private Double currentcredit;
	@Column(name="totaldue") 
	private Double totaldue;
	@Column(name="totalamountinwords") 
	private String totalamountinwords;
	@Column(name="totaldueinwords") 
	private String totaldueinwords;
	@Column(name="billrunid") 
	private Long billrunid;
	@Column(name="billrunstatus") 
	private String billrunstatus;
	@Column(name="is_delete") 
	private Integer isDelete;
	@Column(name="credit_doc_id") 
	private Long creditDocId;
	@Column(name="custpackrelid") 
	private Long custpackrelid;
	@Column(name="createbyname") 
	private String createbyname;
	@Column(name="updatebyname") 
	private String updatebyname;
	@Column(name="CREATEDBYSTAFFID") 
	private Double createdbystaffid;
	@Column(name="LASTMODIFIEDBYSTAFFID") 
	private Double lastmodifiedbystaffid;
	@Column(name="lastmodifieddate") 
	private LocalDateTime lastmodifieddate;
	@Column(name="cstchargeid") 
	private Long cstchargeid;
	@Column(name="status") 
	private String status;
	@Column(name="paymentowner") 
	private String paymentowner;
	@Column(name="cust_ref_name") 
	private String custRefName;

	@Column(name = "inventory_mapping_id")
	private Long inventoryMappingId;

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
