package com.savbill.radius.helper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "COA Profile Attribute Mapping",description = "This is data transfer object for COA Profile attribute mapping which is used to create new COA profile attribute")
public class CoaDMProfileAttributeDto
{
	@ApiModelProperty(notes = "This is COA Profile attribute Name")
    private Long coaDMProfileId;

	@ApiModelProperty(notes = "Radius Attribute of the COA Profile attribute")
    private String radiusAtt;

	@ApiModelProperty(notes = "Profile Attribute of the COA Profile attribute")
    private String profileAtt;
	@ApiModelProperty(notes = "Check item for Profile Attribute")
	private String checkitem;

	public Long getCoaDMProfileId() {
		return coaDMProfileId;
	}

	public void setCoaDMProfileId(Long coaDMProfileId) {
		this.coaDMProfileId = coaDMProfileId;
	}

	public String getRadiusAtt() {
		return radiusAtt;
	}

	public void setRadiusAtt(String radiusAtt) {
		this.radiusAtt = radiusAtt;
	}

	public String getProfileAtt() {
		return profileAtt;
	}

	public void setProfileAtt(String profileAtt) {
		this.profileAtt = profileAtt;
	}

	public String getCheckitem() {
		return checkitem;
	}

	public void setCheckitem(String checkitem) {
		this.checkitem = checkitem;
	}
}
