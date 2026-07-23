package com.savbill.radius.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBLTCUSTOMERLOCATIONMAPPING")
public class CustomerLocationMapping {

	@Id
	@Column(name = "customerlocationid", nullable = false)
	private Long id;
	
	@ApiModelProperty(notes = "This is Customer Id")
	@Column(name = "customerid", nullable = false)
	private Long custId;

	@ApiModelProperty(notes = "Location name from plan")
	@Column(name = "locationid")
	private Long locationId;

	@ApiModelProperty(notes = "Location name from plan")
	@Column(name = "locationname")
	private String locationName;

	@Column(name = "is_deleted")
	private Boolean isDelete;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "is_parent_location")
	private Boolean isParentLocation;

	@Column(name = "mac")
	private String mac;

	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;

	public CustomerLocationMapping(Map<String, Object> locationMap, Long custId) {
		this.id = Long.valueOf(locationMap.get("id").toString());
		this.custId = custId;//Long.valueOf(locationMap.get("custId").toString());
		this.locationId = Long.valueOf(locationMap.get("locationId").toString());
		if(locationMap.containsKey("locationName") && locationMap.get("locationName") != null)
			this.locationName = locationMap.get("locationName").toString();
		this.isDelete = Boolean.valueOf(locationMap.get("isDelete").toString());
		this.isActive = Boolean.valueOf(locationMap.get("isActive").toString());
		this.isParentLocation = Boolean.valueOf(locationMap.get("isParentLocation").toString());
		if(locationMap.get("mac") != null) {
			this.mac = locationMap.get("mac").toString();
		}
		this.mvnoId = Integer.valueOf(locationMap.get("mvnoId").toString());
	}
}
