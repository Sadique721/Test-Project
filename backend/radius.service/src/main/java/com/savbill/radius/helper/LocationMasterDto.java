package com.savbill.radius.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@ApiModel(value = "LocationMaster",description = "This is data transfer object for LocationMaster which is used to create new LocationMaster")
public class LocationMasterDto {
	@ApiModelProperty(notes = "Name of the Location Master",required=true)
	private String name;
	
	@ApiModelProperty(notes = "This is Location Master status. (Active or Inactive)",required=true)
	private String status;
	
	@ApiModelProperty(notes = "This is Location Master check item.",required=true)
	private String checkItem;
	
	@ApiModelProperty(notes = "This is Location Master locationIdentifyAttribute.",required=false)
	private String locationIdentifyAttribute;

	
	
	
}