package com.savbill.radius.entity;

import com.savbill.radius.helper.ClientReplyDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "TBLTRADIUSCLIENTREPLY")
@ApiModel(value = "Client Reply", description = "This is Client Reply entity which is used to update client reply data")
public class ClientReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "This is client reply attribute id", required = true)
    @Column(name = "attributeid", nullable = false)
    private Long attributeId;

    @ApiModelProperty(notes = "This is client group id", required = true)
    @Column(name = "clientgroupid", nullable = false)
    private Long clientGroupId;

    @ApiModelProperty(notes = "This is client reply attribute", required = true)
    @Column(name = "attribute", nullable = false, length = 250)
    private String attribute;

    @ApiModelProperty(notes = "This is client reply attribute value", required = true)
    @Column(name = "attributevalue", nullable = false, length = 250)
    private String attributeValue;
    

    @ApiModelProperty(notes = "This is client checkitem attribute", required = false)
    @Column(name = "checkitem", nullable = false, length = 100)
    private String checkitem;

    @ApiModelProperty(hidden = true)
    @Column(name = "createdate")
    @JsonProperty("createDate")
    private Timestamp createdOn;
    @ApiModelProperty(hidden = true)
    @Column(name = "lastmodificationdate")
    @JsonProperty("lastModificationDate")
    private Timestamp lastModifiedOn;

    @ApiModelProperty(hidden = true)
    @Column(name = "mvnoid", nullable = false)
    private Integer mvnoId;
    @ApiModelProperty(notes = "This is client reply attribute value", required = true)
    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @ApiModelProperty(notes = "This is client reply reject attribute value")
    @Column(name = "is_reject_attribute")
    private boolean rejectAttribute;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public Integer getMvnoId() {
        return mvnoId;
    }

    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }

    public Long getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(Long attributeId) {
        this.attributeId = attributeId;
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

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(Timestamp lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public String getCheckitem() {
		return checkitem;
	}

	public void setCheckitem(String checkitem) {
		this.checkitem = checkitem;
	}

	public ClientReply() {
        super();
    }

    public boolean isRejectAttribute() {
        return rejectAttribute;
    }

    public void setRejectAttribute(boolean rejectAttribute) {
        this.rejectAttribute = rejectAttribute;
    }

    public ClientReply(ClientReplyDto clientReplyDto) {
        this.clientGroupId = clientReplyDto.getClientGroupId();
        this.attribute = clientReplyDto.getAttribute();
        this.attributeValue = clientReplyDto.getAttributeValue();
        this.type = clientReplyDto.getType();
        this.checkitem=clientReplyDto.getCheckitem();
        this.rejectAttribute = clientReplyDto.isRejectAttribute();
    }

}
