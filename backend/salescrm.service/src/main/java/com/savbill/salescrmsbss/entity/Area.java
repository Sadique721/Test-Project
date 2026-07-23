package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.SaveAreaSharedDataMessage;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblmarea")
public class Area {

	@Id
	@Column(name = "areaid")
	private Long id;

	private String name;
	
	private String status;
	
	@Column(name = "is_deleted")
	private Boolean isDeleted = false;

	@Column(name = "COUNTRYID", nullable = false, length = 40)
	private Integer countryId;

	@Column(name = "CITYID", nullable = false, length = 40)
	private Integer cityId;

	@Column(name = "STATEID", nullable = false, length = 40)
	private Integer stateId;

	@JsonBackReference
	@ManyToOne()
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JoinColumn(name = "pincodeid")
	private Pincode pincode;

	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;
	
	public Area(SaveAreaSharedDataMessage message) {
		this.id = message.getId();
		this.cityId = message.getCityId();
		this.stateId = message.getStateId();
		this.countryId = message.getCountryId();
		this.isDeleted = message.getIsDeleted();
		this.mvnoId = message.getMvnoId();
		this.name = message.getName();
		if(message.getPincode() != null)
			this.pincode = message.getPincode();
		this.status = message.getStatus();
	}

}
