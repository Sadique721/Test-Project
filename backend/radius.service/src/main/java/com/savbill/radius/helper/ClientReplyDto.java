package com.savbill.radius.helper;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Client Attribute Mapping", description = "This is data transfer object for Client attribute mapping which is used to create new Client attribute")
public class ClientReplyDto {

    @ApiModelProperty(notes = "This is client group id")
    private Long clientGroupId;

    @ApiModelProperty(notes = "This is client reply attribute")
    private String attribute;

    @ApiModelProperty(notes = "This is client reply attribute value")
    private String attributeValue;

    @ApiModelProperty(notes = "This is client reply type value")
    private String type;

    @ApiModelProperty(notes = "This is client checkitem")
    private String checkitem;

    @ApiModelProperty(notes = "This is client checkitem")
    private boolean rejectAttribute;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Long getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(Long clientGroupId) {
        this.clientGroupId = clientGroupId;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

	public String getCheckitem() {
		return checkitem;
	}

	public void setCheckitem(String checkitem) {
		this.checkitem = checkitem;
	}

    public boolean isRejectAttribute() {
        return rejectAttribute;
    }

    public void setRejectAttribute(boolean rejectAttribute) {
        this.rejectAttribute = rejectAttribute;
    }
}
