package com.savbill.radius.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tblmclearcachemapping")
@ApiModel(value = "Inactive Profile Mapping ", description = "This is Inactive Profile Mapping entity.")
@Data
public class ClearCacheMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "This is client reply attribute id", required = true)
    @Column(name = "id", nullable = false)
    private Long id;

    @ApiModelProperty(notes = "This is client group id", required = true)
    @Column(name = "clientgroupid", nullable = false)
    private Long clientGroupId;

    @ApiModelProperty(notes = "This is clear Cache attribute", required = true)
    @Column(name = "attribute", nullable = false, length = 250)
    private String attribute;

    @ApiModelProperty(notes = "This is clear Cache attribute value", required = true)
    @Column(name = "attributevalue", nullable = false, length = 250)
    private String attributeValue;

    @ApiModelProperty(notes = "This is ClearCacheMapping checkitem", required = true)
    @Column(name = "criteria")
    private String criteria;

    @ApiModelProperty(notes = "This is ClearCacheMapping checkitem", required = true)
    @Column(name = "checkitem")
    private String checkitem;

}
