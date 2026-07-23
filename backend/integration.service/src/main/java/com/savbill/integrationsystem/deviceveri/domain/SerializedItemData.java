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
@Table(name = "tblmserializeditem")
public class SerializedItemData implements IBaseData<Long>{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 40)
    private Long id;
	
	@Column(name = "name")
    private String name;
	
	@Column(name = "mac")
    private String mac;
	
	@Column(name = "serial_number")
    private String serialNumber;
	
	@Column(name = "item_condition")
    private String itemCondition;
	
	@Column(name = "mvno_id")
    private Long mvnoId;
	
	@Column(name = "CREATEDATE")
    private LocalDateTime createDate;
	
	@Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime lastModifiedDate;
	
	@Column(name = "createbyname")
    private String createbyname;
	
	@Column(name = "updatebyname")
    private String updatebyname;
	
	@Column(name = "CREATEDBYSTAFFID")
    private Long createdStaffId;
	
	@Column(name = "LASTMODIFIEDBYSTAFFID")
    private Long LastModifiedStaffId;
	
	@Column(name = "is_deleted")
    private Integer isDeleted;
	
	@Column(name = "product_id")
    private Long productId;
	
	@Column(name = "current_inward_id")
    private Long currentInwardId;
	
	@Column(name = "current_inward_type")
    private String currentInwardType;
	
	@Column(name = "warranty")
    private String warranty;
	
	@Column(name = "warranty_period")
    private Long warrantyPeriod;

	@Column(name = "owner_id")
    private Long owner_id;

	@Column(name = "owner_type")
    private String ownerType;

	@Column(name = "item_status")
    private String itemStatus;

	@Column(name = "ownership_type")
    private String ownershipType;
	
	@Column(name = "external_item_id")
    private Long externalItemId;

	@Column(name = "remaining_days")
    private String remainingDays;

	@Column(name = "intransiant_warrenty")
    private String intransiantWarrenty;

	@Column(name = "remarks")
    private String remarks;

	@Column(name = "intransiant_ownership")
    private Long intransiantOwnership;

	@Column(name = "expiry_date")
    private LocalDateTime expiryDate;
	
	@Column(name = "intransiant_expiry_date")
    private LocalDateTime intransiantExpiryDate;

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
