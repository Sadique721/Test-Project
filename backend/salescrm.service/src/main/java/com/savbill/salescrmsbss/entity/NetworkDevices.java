package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.NetworkDevicesMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblnetworkdevices")
public class NetworkDevices {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "deviceid")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "devicetype")
	private String devicetype;

	@Column(name = "status")
	private String status;

	@Column(name = "latitude")
	private String latitude;

	@Column(name = "longitude")
	private String longitude;

	@ManyToOne
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JoinColumn(name = "servicearea_id")
	private ServiceArea servicearea;

	@Column(name = "is_deleted", columnDefinition = "Boolean default false", nullable = false)
	private Boolean isDeleted = false;

	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;

	@Column(name = "total_in_ports")
	private Integer totalInPorts;

	@Column(name = "available_in_ports")
	private Integer availableInPorts;

	@Column(name = "total_out_ports")
	private Integer totalOutPorts;

	@Column(name = "available_out_ports")
	private Integer availableOutPorts;

	public NetworkDevices(NetworkDevicesMessage message, ServiceArea serviceArea) {
		this.id = message.getId();
		this.name = message.getName();
		this.status = message.getStatus();
		this.devicetype = message.getDevicetype();
		this.latitude = message.getLatitude();
		this.longitude = message.getLongitude();
		this.isDeleted = message.getIsDeleted();
		this.mvnoId = message.getMvnoId();
		this.totalInPorts = message.getTotalInPorts();
		this.availableInPorts = message.getAvailableInPorts();
		this.totalOutPorts = message.getTotalOutPorts();
		this.availableOutPorts = message.getAvailableOutPorts();
		if (serviceArea != null)
			this.servicearea = serviceArea;
	}

}
