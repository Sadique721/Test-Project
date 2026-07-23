package com.savbill.radius.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Device Entity",description = "This is device entity which is used to update device data")
public class DeviceDto {

	@ApiModelProperty(notes = "This is device profile name",required=true)
	private String deviceProfileName;
	@ApiModelProperty(notes = "This is device description",required=true)
	private String description;
	@ApiModelProperty(notes = "Check item for device",required=true)
	private String checkItem;
	@ApiModelProperty(notes = "This is device priority",required=true)
	private Integer priority;
	@ApiModelProperty(allowableValues = "HTTP,COA",  value = "This field accept value only : HTTP or COA",  notes = "This is device type",required = true)
	private String type;
	@ApiModelProperty(notes = "login url for http type device",required=true)
	private String loginurl;
	@ApiModelProperty(notes = "logout url for http type device",required=true)
	private String logouturl;
	@ApiModelProperty(allowableValues = "Active,Inactive",  value = "This field accept value only : Active or Inactive",  notes = "This is gateway type",required = true)
	private String status;
	@ApiModelProperty(notes = "This is coa profile name", required = false)
	private String coaProfileName;
	@ApiModelProperty(notes = "This is radius client id list", required = false)
	private List<Long> clientIds;
}
