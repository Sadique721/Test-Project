package com.savbill.radius.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "COADM Profile",description = "This is data transfer object for COA/DM Profile which is used to create new COA/DM Profile")
public class CoaDMProfileDto
{
	@ApiModelProperty(notes = "This is COA/DM Profile Name")
	private Long id;
	@ApiModelProperty(notes = "This is COA/DM Profile Name")
	private String name;
	@ApiModelProperty(notes = "Gateway of the COA/DM Profile")
	private String gateway;
	@ApiModelProperty(notes = "Shared Key of the COA/DM Profile")
	private String sharedkey;
	@ApiModelProperty(notes = "Port of the COA/DM Profile")
	private Integer port;
	@ApiModelProperty(notes = "Port of the COA/DM Profile")
	private String type;


	@ApiModelProperty(notes = "List of COA/DM Profile Attributes")
	private List<CoaDMProfileAttributeDto> coaDMProfileAttributeDtoList;

	private Double timevar;

//	private String unitsOftime;
	
	public Long getId() {
	    return id;
	}
	public void setId(Long id) {
	    this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public String getSharedkey() {
		return sharedkey;
	}

	public void setSharedkey(String sharedkey) {
		this.sharedkey = sharedkey;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public List<CoaDMProfileAttributeDto> getCoaDMProfileAttributeDtoList() {
		return coaDMProfileAttributeDtoList;
	}

	public void setCoaDMProfileAttributeDtoList(List<CoaDMProfileAttributeDto> coaDMProfileAttributeDtoList) {
		this.coaDMProfileAttributeDtoList = coaDMProfileAttributeDtoList;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getTimevar() {
		return timevar;
	}

	public void setTimevar(Double timevar) {
		this.timevar = timevar;
	}
//
//	public String getUnitsOftime() {
//		return unitsOftime;
//	}
//
//	public void setUnitsOftime(String unitsOftime) {
//		this.unitsOftime = unitsOftime;
//	}
}
