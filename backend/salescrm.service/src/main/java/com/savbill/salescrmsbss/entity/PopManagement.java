package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.PopManagementMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@Table(name = "tblmpopmanagement")
@NoArgsConstructor
public class PopManagement {

	@Id
	@Column(name = "pop_id")
	private Long id;

	@Column(name = "pop_name", nullable = false)
	private String popName;

	@Column(name = "latitude", nullable = false)
	private String latitude;

	@Column(name = "longitude", nullable = false)
	private String longitude;

	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "is_deleted", nullable = false)
	private Boolean isDeleted = false;

	@Column(name = "mvno_id", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;
	
	public PopManagement(PopManagementMessage popManagementMessage) {
		this.id = popManagementMessage.getId();
		this.popName = popManagementMessage.getPopName();
		this.latitude = popManagementMessage.getLatitude();
		this.longitude = popManagementMessage.getLongitude();
		this.status = popManagementMessage.getStatus();
		this.isDeleted = popManagementMessage.getIsDeleted();
		this.mvnoId = popManagementMessage.getMvnoId();
	}

	public PopManagement(Long id) {
		this.id = id;
	}

}
