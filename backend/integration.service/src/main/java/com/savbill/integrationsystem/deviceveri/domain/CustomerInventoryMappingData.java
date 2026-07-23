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
@Table(name = "tblmcustomer_inventory_mapping")
public class CustomerInventoryMappingData implements IBaseData<Long> {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="mapping_id") 
	private Long mappingId;
	@Column(name="quantity") 
	private Long quantity;
	@Column(name="customer_id") 
	private Long customerId;
	@Column(name="product_id") 
	private Long productId;
	@Column(name="staff_id") 
	private Long staffId;
	@Column(name="assigned_date_time") 
	private LocalDateTime assignedDateTime;
	@Column(name="mvno_id") 
	private Long mvnoId;
	@Column(name="CREATEDATE") 
	private LocalDateTime createdate;
	@Column(name="LASTMODIFIEDDATE") 
	private LocalDateTime lastmodifieddate;
	@Column(name="createbyname") 
	private String createbyname;
	@Column(name="updatebyname") 
	private String updatebyname;
	@Column(name="CREATEDBYSTAFFID") 
	private Long createdbystaffid;
	@Column(name="LASTMODIFIEDBYSTAFFID") 
	private Long lastmodifiedbystaffid;
	@Column(name="is_deleted") 
	private Integer isDeleted;
	@Column(name="status") 
	private String status;
	@Column(name="expiry_date_time") 
	private LocalDateTime expiryDateTime;
	@Column(name="next_approver") 
	private Long nextApprover;
	@Column(name="team_hierarchy_mapping_id") 
	private Long teamHierarchyMappingId;
	@Column(name="previous_approve_id") 
	private Long previousApproveId;
	@Column(name="inward_id") 
	private Long inwardId;
	@Column(name="external_item_id") 
	private Long externalItemId;
	@Column(name="service_id") 
	private Long serviceId;
	@Column(name="custpack_id") 
	private Long custpackId;
	@Column(name="item_id") 
	private Long itemId;
	@Column(name="itemassemblyid") 
	private Long itemassemblyid;
	@Column(name="is_invoice_created") 
	private Long isInvoiceCreated;
	@Column(name="connection_no") 
	private String connectionNo;
	@Column(name="replacement_reason") 
	private String replacementReason;
	@Column(name="plan_id") 
	private Long planId;
	@Column(name="mapping_ref_id") 
	private Long mappingRefId;
	@Column(name="remark") 
	private String remark;
	@Column(name="plangroup_id") 
	private Long plangroupId;
	@Column(name="offer_price") 
	private Long offerPrice;
	@Column(name="charge_id") 
	private Long chargeId;
	@Column(name="bill_to") 
	private String billTo;
	@Column(name="is_invoice_to_org") 
	private Long isInvoiceToOrg;
	@Column(name="new_amount") 
	private Long newAmount;
	@Column(name="discount") 
	private Long discount;
	@Column(name="is_required_approval") 
	private Long isRequiredApproval;
	@Column(name="is_free") 
	private Long isFree;
	@Column(name="payment_owner_id") 
	private Long paymentOwnerId;
	@Column(name="billable_cust_id") 
	private Long billableCustId;
	@Column(name="ezybill_stock_id") 
	private String ezybillStockId;
	@Column(name="pairstatus") 
	private String pairstatus;


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
