package com.savbill.radius.entity;

import javax.persistence.*;

import com.savbill.radius.helper.DeviceDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLMDEVICE")
@ApiModel(value = "Device Entity", description = "This is device entity which is used to update device data")
public class Device extends Auditable<Long> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated device id")
	@Column(name = "deviceid", nullable = false)
	private Long deviceId;

	@ApiModelProperty(notes = "This is device profile name", required = true)
	@Column(name = "name", nullable = false, length = 150)
	private String deviceProfileName;

	@ApiModelProperty(notes = "This is device description", required = false)
	@Column(name = "description", length = 500)
	private String description;

	@ApiModelProperty(notes = "Check item for device", required = false)
	@Column(name = "checkitem", length = 250)
	private String checkItem;

	@ApiModelProperty(notes = "This is device priority", required = true)
	@Column(name = "priority", nullable = false)
	private Integer priority;

	@ApiModelProperty(allowableValues = "HTTP,COA", value = "This field accept value only : HTTP or COA", notes = "This is device type", required = true)
	@Column(name = "type", nullable = false, length = 10)
	private String type;

	@ApiModelProperty(notes = "login url for http type device", required = false)
	@Column(name = "loginurl", length = 1000)
	private String loginurl;

	@ApiModelProperty(notes = "logout url for http type device", required = false)
	@Column(name = "logouturl", length = 1000)
	private String logouturl;

	@ApiModelProperty(allowableValues = "Active,Inactive", value = "This field accept value only : Active or Inactive", notes = "This is gateway type", required = true)
	@Column(name = "status", nullable = false, length = 10)
	private String status;

	@ApiModelProperty(notes = "This is coa profile id", required = false)
	@Column(name = "coadmprofileid", nullable = false, length = 100)
	private Long coaDmProfileId;

	@ApiModelProperty(notes = "This is mvno id")
	@Column (name="mvnoid", nullable = false)
	private Integer mvnoId;

	@LazyCollection(LazyCollectionOption.FALSE)
	@ApiModelProperty(notes = "This is device-client mapping configuration",required = true)
	@OneToMany(targetEntity = Client.class, cascade = CascadeType.DETACH)
	@JoinColumn(name = "deviceid")
	private List<Client> clientList;


	public Device(DeviceDto deviceDto)
	{
		this.deviceProfileName = deviceDto.getDeviceProfileName();
		if (deviceDto.getCheckItem() != null) 
		{
			this.checkItem = deviceDto.getCheckItem();
		}
		if (deviceDto.getDescription() != null) 
		{
			this.description = deviceDto.getDescription();
		}
		if (deviceDto.getLoginurl() != null) {
			this.loginurl = deviceDto.getLoginurl();
		}
		if (deviceDto.getLogouturl() != null) {
			this.logouturl = deviceDto.getLogouturl();
		}
		this.priority = deviceDto.getPriority();
		this.status = deviceDto.getStatus();
		this.type = deviceDto.getType();
//		this.mvnoId = ValidateCrudTransactionData.validateMvnoId(deviceDto.getMvnoId());
	}
}