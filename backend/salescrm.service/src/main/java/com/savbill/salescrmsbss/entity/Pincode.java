package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.SavePincodeSharedDataMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblmpincode")
public class Pincode {

	@Id
	@Column(name = "pincodeid", nullable = false, length = 40)
	private Long id;

	private String pincode;
	
	private String status;
	
	@Column(name = "is_deleted")
	private Boolean isDeleted = false;

	@Column(name = "COUNTRYID", nullable = false, length = 40)
	private Integer countryId;

	@Column(name = "CITYID", nullable = false, length = 40)
	private Integer cityId;

	@Column(name = "STATEID", nullable = false, length = 40)
	private Integer stateId;

	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;
	
	public Pincode(SavePincodeSharedDataMessage message) {
		this.id = message.getId();
		this.pincode = message.getPincode();
		this.status = message.getStatus();
		this.isDeleted = message.getIsDeleted();
		this.countryId = message.getCountryId();
		this.stateId = message.getStateId();
		this.cityId = message.getCityId();
		this.mvnoId = message.getMvnoId();
	}
}
