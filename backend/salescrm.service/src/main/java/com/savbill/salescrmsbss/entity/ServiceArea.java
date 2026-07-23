package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.ServiceAreaMessage;
import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblservicearea")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceArea {

	@Id
	@Column(name = "service_area_id")
	private Long id;

	private String name;

	private String status;

	@Column(name = "is_deleted",columnDefinition = "Boolean default false", nullable = false)
	private Boolean isDeleted = false;

	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;

	@Column(name = "latitude", nullable = false, length = 50)
	private String latitude;

	@Column(name = "longitude", nullable = false, length = 50)
	private String longitude;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "areaid")
	@ToString.Exclude
	private Area area;
	@Column(name = "BUID", nullable = false, length = 40, updatable = false)
	private Integer buId;
	
	public ServiceArea(ServiceAreaMessage message, Area area) {
		this.id = message.getId();
		this.name = message.getName();
		this.status = message.getStatus();
		this.isDeleted = message.getIsDeleted();
		this.mvnoId = message.getMvnoId();
		this.latitude = message.getLatitude();
		this.longitude = message.getLongitude();
		this.buId=message.getBuId();
		if(area != null)
			this.area = area;
	}
	
	public ServiceArea(Long id) {
		this.id = id;
	}
}
