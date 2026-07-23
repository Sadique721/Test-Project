package com.savbill.radius.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "TBLMLOCATIONMASTER")
@ApiModel(value = "LocationMaster Entity", description = "This is LocationMaster entity ")
public class LocationMaster {
	@Id
	@ApiModelProperty(notes = "The database generated Location Master Id")
	@Column(name = "locationid", nullable = false)
	private Long locationMasterId;

	@ApiModelProperty(notes = "This is Location Master Name")
	@Column(name = "name", nullable = false, length = 250)
	private String name;

	@ApiModelProperty(notes = "Check item for location master", required = false)
	@Column(name = "checkitem", length = 250)
	private String checkItem;

	private String status;

	@ApiModelProperty(notes = "This is mvnoid")
	@Column(name = "mvnoid", nullable = false, length = 10)
	private Integer mvnoId;

	@ApiModelProperty(notes = "This is Location Master locationIdentifyAttribute.",required=false)
	@Column(name = "location_identify_attribute", nullable = false, length = 10)
	private String locationIdentifyAttribute;

	public LocationMaster() {
		super();

	}

//	public LocationMaster(LocationMaster locationMasterDto, Integer mvnoId) {
//
//		this.name = locationMasterDto.getName();
//		this.checkItem = locationMasterDto.getCheckItem();
//		this.status = locationMasterDto.getStatus();
//		this.mvnoId = mvnoId;
//		this.locationIdentifyAttribute=locationMasterDto.getLocationIdentifyAttribute();
//	}

	public LocationMaster(LocationMaster locationMasterDto, Integer mvnoId) {
		this.locationMasterId = locationMasterDto.getLocationMasterId();
		this.name = locationMasterDto.getName();
		this.checkItem = locationMasterDto.getCheckItem();
		this.status = locationMasterDto.getStatus();
		this.mvnoId = mvnoId;
		this.locationIdentifyAttribute=locationMasterDto.getLocationIdentifyAttribute();
	}


}